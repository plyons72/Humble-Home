/*
    Patrick Lyons
    COE 1896 - Senior Design
    Summer 2018
    University of Pittsburgh
*/

/*
   HumbleHome Embedded System
   Boarduino (Atmega 328P) using an ESP 8266 WiFi Module
*/

#include <WiFiEspClient.h>
#include <WiFiEsp.h>
#include <WiFiEspUdp.h>
#include <PubSubClient.h>
#include <SoftwareSerial.h>

// Class WiFi Network
#define SSID "Embedded Systems Class"
#define PASS "embedded1234"

// Initialize the Ethernet client object
WiFiEspClient espClient;
SoftwareSerial soft(13, 12); // RX, TX

// WiFi Status
int status = WL_IDLE_STATUS;

// Holds the time since the last send
unsigned long lastSend;

// Server where thingsboard IoT platform is set up
char humblehome_server[] = "ec2-54-243-18-99.compute-1.amazonaws.com";

// Function prototype for callback method, used to receive MQTT messages
void callback(char* topic, byte* payload, unsigned int length);

// Initialize the pubsubclient object for MQTT messaging
PubSubClient client(humblehome_server, 1883, callback, espClient);

void setup() {
  // Initialize serial for debugging
  Serial.begin(9600);

  // Reference for analog in should be 5v
  analogReference(DEFAULT);
  InitWiFi();
  client.subscribe("PutBreakerState", 1);
}

void loop() {
  status = WiFi.status();
  if ( status != WL_CONNECTED) {
    while ( status != WL_CONNECTED) {
      Serial.print("Connecting to: ");
      Serial.println(SSID);
      // Connect to WPA/WPA2 network
      status = WiFi.begin(SSID, PASS);
      delay(1000);
    }
    Serial.println("Connected to AP");
  }

  if ( !client.connected() ) {
    reconnect();
  }

  if ( millis() - lastSend > 3000 ) { // Update and send only after 3 seconds
    getAndSendData();
    lastSend = millis();
  }

  client.loop();
}

void getAndSendData()
{
  // Determines whether we do AC or DC calculation
  boolean AC_Load = false;

  // Pins to track voltage and current from the breakers (mux)
  int voltagePin = A0;
  int currentPin = A1;

  // Variables to hold current and voltage readings
  float current = 0;
  float voltage = 0;

  // AC Load
  if (AC_Load) {

    /*
       Current Calculation
    */
    const int avgSamples = 10; //DO NOT CHANGE

    // Holds 
    float maxCurrent;

    //100mA per 55mV = 0.2. DO NOT CHANGE
    float sensitivity = 100.0 / 60.0;

    // Output voltage with no current: ~ 2500mV or 2.5V
    float vref = 2500;

    for (int i = 0; i < 100; i++) {
      //Holds sum of all current values collected to take an average
      int currentSum = 0;

      // Holds the value of the current determined from this loop
      float tempCurrent = 0;
    
      // Read in analog values and add them together
      for (int i = 0; i < avgSamples; i++) { currentSum += analogRead(currentPin); }
  
      // Divide the 10 analogRead values by 10 to get the average value read
      currentSum /= avgSamples;

      // Perform current calculation and scale up the current by 10 to represent real house current
      tempCurrent = 48.8 * currentSum;
      tempCurrent = (tempCurrent - vref) * sensitivity;
  
      // Ignore negative current
      if (tempCurrent < 65) { tempCurrent = 0; }

      // If this current is greater than the other currents we've found, use this as the max value
      if (tempCurrent > current) { current = tempCurrent; }
    }
    
    /*
       Voltage Calculation
    */

    // Read the value from the voltage pin and scale it up for the AC load
    voltage = analogRead(voltagePin) * 24 * .00488;
  } // End of AC Loop

  // DC Load
  else {
    // Perform Aryana's DC Current conversion calculations, use conversion factor for analogRead, and scale by 10
    current = (analogRead(currentPin) * .00488 * 2.7717 - 6.9694) * 10;

    // Scale voltage up by 10 for DC load
    voltage = analogRead(voltagePin) * .00488* 10;
  } // End of DC Loop

//  // Print to serial for debugging
//  Serial.print("\nVoltage: ");
//  Serial.print(voltage);
//  Serial.print(" V\t");
//  Serial.print("Current: ");
//  Serial.print(current);
//  Serial.println(" mA");

  //String representations of voltage, current, and power
  String voltageString = String(voltage);
  String currentString = String(current);

  // Some Debug Messages to print to Serial Monitor
  Serial.print( "Sending Voltage and Current : [" );
  Serial.print(voltage);
  Serial.print( "V, " );
  Serial.print(current);
  Serial.print( "mA]   -> " );

  // Payload to send to MQTT Broket as {voltage, current} reading
  String payload = "{";
  payload += voltageString;
  payload += ",";
  payload += currentString;
  payload += "}";

  // Send payload
  char msg[50];
  payload.toCharArray(msg, 50);
  if (!client.publish( "boarduino/publish", msg )) {
    Serial.println("Publish failed");
  }

  
  Serial.println(msg);
  client.loop();
}

// When we receive an MQTT message, print it out to the screen
// Send publish message to SetBreakerState to acknowledge reception
void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();

  char attributes = "0";

  if (!client.publish( "SetBreakerState", attributes )) {
    Serial.println("Failed!");
  }
}

void InitWiFi()
{
  // Initialize serial connection for WiFi
  soft.begin(9600);

  // Initialize WiFi
  WiFi.init(&soft);
  // Make sure WiFi is present
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    while (true);
  }

  Serial.println("Connecting to AP ...");
  // Attempt to make connection
  while ( status != WL_CONNECTED) {
    Serial.print("Attempting to connect to ");
    Serial.println(SSID);
    // Connect to network
    status = WiFi.begin(SSID, PASS);
    delay(500);
  }
  Serial.println("Connected to AP");
}

void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
    Serial.print("Connecting to MQTT Server ...");

    // Attempt to connect (clientId, username, password)
    if ( client.connect("HUMBLEHOME", "humblehome", "1896seniordesign") ) {
      client.setCallback(callback);
      client.subscribe("PutBreakerState", 1);
      Serial.println( "[DONE]" );
    }

    else {
      Serial.print( "[FAILED] [ rc = " );
      Serial.print( client.state() );
      Serial.println( " : retrying in 3 seconds]" );
      // Wait 3 seconds before retrying
      delay(3000);
    }
  }
}


