/* 
 *  Patrick Lyons
 *  COE 1896 - Senior Design
 *  Summer 2018
 *  University of Pittsburgh
*/

/*
 * HumbleHome Embedded System
 * Boarduino (Atmega 328P) using an ESP 8266 WiFi Module
*/

#include <WiFiEspClient.h>
#include <WiFiEsp.h>
#include <WiFiEspUdp.h>
#include <PubSubClient.h>
#include <SoftwareSerial.h>
#include <eRCaGuy_NewAnalogRead.h>


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
SoftwareSerial soft(13, 12); // RX, TX

// WiFi Status
int status = WL_IDLE_STATUS;

// Holds the time since the last send
unsigned long lastSend;

// Holds the address of which breaker to access for reading
int read_breaker_address[4] = {0,0,0,0};

// Holds the address of which breaker to access for reading
int write_breaker_address[4] = {0,0,0,0};

// Server where thingsboard IoT platform is set up
char humblehome_server[] = "ec2-54-243-18-99.compute-1.amazonaws.com";  

void callback(char* topic, byte* payload, unsigned int length);

PubSubClient client(humblehome_server, 1883, callback, espClient);

void setup() {
  // Initialize serial for debugging
  Serial.begin(9600);

  // Reference for analog in should be 5v
  analogReference(DEFAULT);
  InitWiFi();

  client.subscribe("PutBreakerState");
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

  if ( millis() - lastSend > 2000 ) { // Update and send only after 2 seconds
    getAndSendData();
    lastSend = millis();
  }

  client.loop();
}

void getAndSendData()
{
  // Determines whether we do AC or DC calculation
  boolean ac_load = false;

  // Pins to track voltage and current from the breakers (mux)
  int voltage_pin = A0;
  int current_pin = A1;

  // For taking more precise readings from board. 
  //Precise to 14 bits, and taking average read from 10 samples
  int precision_bits = 14;
  int num_samples = 10;

  // create new precise analog read object
  float voltage_reading = adc.newAnalogRead(voltage_pin, precision_bits, num_samples);
  float current_reading = adc.newAnalogRead(current_pin, precision_bits, num_samples);

  // Precise readings from analog
  const float MAX_READING_14_bit = 16368.0;

  // Perform precise reading, and scale to 5v
  current_reading = 5.0 * current_reading / MAX_READING_14_bit;
  voltage_reading = 5.0 * voltage_reading / MAX_READING_14_bit;
  
  // AC Load
  if (ac_load) { 
    // Perform Aryana's AC current conversion calculations and scale by 10
    current_reading = (current_reading * .01 - .0775) * 10;

    // Scale voltage up by 24 for AC load
    voltage_reading *= 24;
  }

  // DC Load
  else {
    // Perform Aryana's DC Current conversion calculations and scale by 10
    current_reading = (current_reading * 2.7717 - 6.9694) * 10;

    // Scale voltage up by 10 for DC load
    voltage_reading *= 10;
  }
  
  Serial.print("\nVoltage: ");
  Serial.print(voltage_reading);
  Serial.print(" Volts\t");
  Serial.print("Current: ");
  Serial.print(current_reading);
  Serial.println(" Amps");
  

  //String representations of voltage, current, and power
  String voltage = String(voltage_reading);
  String current = String(current_reading);


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

  // Allocate the correct amount of memory for the payload copy
  byte* p = (byte*)malloc(length);
  // Copy the payload to the new buffer
  memcpy(p,payload,length);

  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) { Serial.print((char)p[i]); }
  Serial.println();

  // Free the memory
  free(p);
  
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
        client.subscribe("PutBreakerState");
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


