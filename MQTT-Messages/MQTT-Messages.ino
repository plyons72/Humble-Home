// Patrick Lyons
// Humble Home Embedded Code

// Senior Design
// Summer 2018

#include <WiFiEspClient.h>
#include <WiFiEsp.h>
#include <WiFiEspUdp.h>
#include <PubSubClient.h>
#include <SoftwareSerial.h>


// Class WiFi Network
#define SSID "Embedded Systems Class"
#define PASS "embedded1234"


/*
// Home WiFi Network
#define SSID "LAN-Solo"
#define PASS "CRtDDc4X"
*/

// Initialize the Ethernet client object
WiFiEspClient espClient;
PubSubClient client(espClient);
SoftwareSerial soft(13, 12); // RX, TX

int status = WL_IDLE_STATUS;
unsigned long lastSend;

// Server where thingsboard IoT platform is set up
#define humblehome_server "ec2-54-243-18-99.compute-1.amazonaws.com"



void setup() {
  // Initialize serial for debugging
  Serial.begin(9600);

  // Reference for analog in should be 5v
  analogReference(DEFAULT);
  InitWiFi();

  // Set up server connection on port 1883
  client.setServer(humblehome_server, 1883);
  client.setCallback(callback);
  
}

void loop() {
  status = WiFi.status();
  if ( status != WL_CONNECTED) {
    while ( status != WL_CONNECTED) {
      Serial.print("Connecting to: ");
      Serial.println(SSID);
      // Connect to WPA/WPA2 network
      status = WiFi.begin(SSID, PASS);
      delay(500);
    }
    Serial.println("Connected to AP");
  }

  if ( !client.connected() ) {
    reconnect();
  }

  if ( millis() - lastSend > 1000 ) { // Update and send only after 1 seconds
    getAndSendData();
    lastSend = millis();
  }

  client.loop();
}

void getAndSendData()
{
  // Determines whether we do AC or DC calculation
  boolean ac_load = false;

  float voltage_in;
  float current_in;
  float current_constant;

  // AC Load
  if (ac_load) { 
    // .0049 (conversion factor for analog read) * 24 (scaling factor for AC current, where 5V = 120V)
    voltage_in = .1176;

    // .0049 (conversion factor for analog read) * .01 (slope from Aryana's equation for AC current)
    current_in = .000049;

    // Constant term to subtract from current to get the final current value (from Aryana Equation)
    current_constant = .0775;
  }

  // DC Load
  else {
    // .0049 (conversion factor for analog read) * 10 (scaling factor for AC current, where 5V = 50V)
    voltage_in = .049; 

    // .0049 (conversion factor for analog read) * 2.7717 (slope from Aryana's equation for DC current)
    current_in = .01358133;

    // Constant term to subtract from current to get the final current value (from Aryana Equation)
    current_constant = 6.9694;
  }

  // Multiply voltage multiplier by the voltage taken at the analog port to get the equivalent voltage from board
  voltage_in *= analogRead(A0);

  // Perform current calculation, then multiply outcome by scaling factor of 10
  current_in *= analogRead(A1);
  current_in -= current_constant;
  current_in *= 10; 
  
  Serial.print("\nVoltage: ");
  Serial.print(voltage_in);
  Serial.print(" Volts\t");
  Serial.print("Current: ");
  Serial.print(current_in);
  Serial.println(" Amps");
  

  //String representations of voltage, current, and power
  String voltage = String(voltage_in);
  String current = String(current_in);


  // Just debug messages
  Serial.print( "Sending Voltage and Current : [" );
  Serial.print(voltage); 
  Serial.print( "," );
  Serial.print(current); 
  Serial.print( "]   -> " );

  // Prepare a JSON payload string
  String payload = "{";
  payload += "\"voltage\":"; 
  payload += voltage; 
  payload += ",";
  payload += "\"current\":"; 
  payload += current;
  payload += "}";

  // Send payload
  char attributes[100];
  payload.toCharArray( attributes, 100 );
  client.publish( "boarduino/publish", attributes );
  Serial.println( attributes );
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) { Serial.print((char)payload[i]); }
  Serial.println();

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
      client.subscribe("boarduino/subscribe", 1);
      Serial.println( "[DONE]" );
    } 
    
    else {
      Serial.print( "[FAILED] [ rc = " );
      Serial.print( client.state() );
      Serial.println( " : retrying in 5 seconds]" );
      // Wait 3 seconds before retrying
      delay(3000);
    }
  }
}


