/*
 * Patrick Lyons
 * COE 1896 - Senior Design
 * Summer 2018
 * University of Pittsburgh
*/

/*
 * HumbleHome Embedded System
 * Boarduino (Atmega 328P) using an ESP-8266 WiFi Module
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

// Decimal value of the binary address we reference below
int readIndex = 0;

// Holds the breaker number of the breaker we are reading globally
int breaker = 0;

/*
 * Bits to reference the address of the value to read in from the right mux 
 * Index----------Address----------AC/DC----------Current/Voltage[BreakerNum]
 * 0                0000             DC                  Current[8]
 * 1                0001             DC                  Voltage[8]
 * 2                0010             DC                  Current[7]
 * 3                0011             DC                  Voltage[7]
 * 4                0100             AC                  Current[6]
 * 5                0101             AC                  Voltage[6]
 * 6                0110             AC                  Current[5]
 * 7                0111             AC                  Voltage[5]
 * 8                1000             AC                  Current[4]
 * 9                1001             AC                  Voltage[4]
 * 10               1010             AC                  Current[3]
 * 11               1011             AC                  Voltage[3]
 * 12               1100             AC                  Current[2]
 * 13               1101             AC                  Voltage[2]
 * 14               1110             AC                  Current[1]
 * 15               1111             AC                  Voltage[1]
 */

// Pins corresponding to the address bits used to read data from mux
const int bIn_0 = 5;
const int bIn_1 = 19;
const int bIn_2 = 17;
const int bIn_3 = 8;

// Pins corresponding to the address bits used to change breaker states
const int bOut_0 = 3;
const int bOut_1 = 4;
const int bOut_2 = 10;
const int bOut_3 = 9;


int bitsIn[4] = {bIn_0, bIn_1, bIn_2, bIn_3};
int bitsOut[4] = {bOut_0, bOut_1, bOut_2, bOut_3};

// Toggle the breakers by setting this pin from low to high
// Analog 0
int togglePin = 18;

// Read Current and Voltages
// Analog 1
int readPin = 15;

// Pin to set high for wifi module to operate properly
//int wifiEnable = 18;

void setup() {
  // Initialize serial for debugging
  Serial.begin(9600);

  // All pins used to address breakers, and current/voltage values should be outputs
  for (int i = 0; i < 4; i++) { 
    pinMode(bitsIn[i], OUTPUT);
    pinMode(bitsOut[i], OUTPUT);
  }

  // Pin modes to read data in and write data out
  pinMode(readPin, INPUT);
  pinMode(togglePin, OUTPUT);

  // Initially set toggle pin low so that jump to logic level 1 causes a toggle
  digitalWrite(togglePin, LOW);

  // Wifi Enable (CHPD) should stay high
//  pinMode(wifiEnable, OUTPUT);
//  digitalWrite(wifiEnable, HIGH);

  // Reference for analog in should be 5v
  analogReference(DEFAULT);

  // Initialize the wifi and subscribe to the PutBreakerState endpoint with QOS1
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

  // 5 Second period to receive messages
  unsigned long receiveLoop = millis() + 5000;
  while(millis() < receiveLoop) { client.loop(); }
}

// Given a breaker number, set the address bits to change that breaker state
void setWriteMux(char breakerNum) {
  switch(breakerNum) {
    case '1':
      digitalWrite(bOut_0, LOW);
      digitalWrite(bOut_1, LOW);
      digitalWrite(bOut_2, LOW);
      digitalWrite(bOut_3, LOW);
      Serial.println("Toggling Breaker 1");
      break;
      
    case '2':
      digitalWrite(bOut_0, HIGH);
      digitalWrite(bOut_1, LOW);
      digitalWrite(bOut_2, LOW);
      digitalWrite(bOut_3, LOW);
      Serial.println("Toggling Breaker 2");
      break;
      
    case '3':
      digitalWrite(bOut_0, LOW);
      digitalWrite(bOut_1, HIGH);
      digitalWrite(bOut_2, LOW);
      digitalWrite(bOut_3, LOW);
      Serial.println("Toggling Breaker 3");
      break;
      
    case '4':
      digitalWrite(bOut_0, HIGH);
      digitalWrite(bOut_1, HIGH);
      digitalWrite(bOut_2, LOW);
      digitalWrite(bOut_3, LOW);
      Serial.println("Toggling Breaker 4");
      break;
      
    case '5':
      digitalWrite(bOut_0, LOW);
      digitalWrite(bOut_1, LOW);
      digitalWrite(bOut_2, HIGH);
      digitalWrite(bOut_3, LOW);
      Serial.println("Toggling Breaker 5");
      break;
      
    case '6':
      digitalWrite(bOut_0, HIGH);
      digitalWrite(bOut_1, LOW);
      digitalWrite(bOut_2, HIGH);
      digitalWrite(bOut_3, LOW);
      Serial.println("Toggling Breaker 6");
      break;
      
    case '7':
      digitalWrite(bOut_0, LOW);
      digitalWrite(bOut_1, HIGH);
      digitalWrite(bOut_2, HIGH);
      digitalWrite(bOut_3, LOW);
      Serial.println("Toggling Breaker 7");
      break;
      
    case '8':
      digitalWrite(bOut_0, HIGH);
      digitalWrite(bOut_1, HIGH);
      digitalWrite(bOut_2, HIGH);
      digitalWrite(bOut_3, LOW);
      Serial.println("Toggling Breaker 8");
      break;

    // Set pins all high to address 15 so the toggle pin doesn't affect anything?
    default:
      digitalWrite(bOut_0, HIGH);
      digitalWrite(bOut_1, HIGH);
      digitalWrite(bOut_2, HIGH);
      digitalWrite(bOut_3, HIGH);
      Serial.println("Not Toggling anything");
      break;
  }
}

// Based on index, sets the address bits to read in the correct value from the breakers
void setReadMux(int index) {
  switch(index){
    
    case 0:
      digitalWrite(bIn_0, LOW);
      digitalWrite(bIn_1, LOW);
      digitalWrite(bIn_2, LOW);
      digitalWrite(bIn_3, LOW);
      breaker = 8;
      break;
      
    case 1:
      digitalWrite(bIn_0, HIGH);
      digitalWrite(bIn_1, LOW);
      digitalWrite(bIn_2, LOW);
      digitalWrite(bIn_3, LOW);
      breaker = 8;
      break;
      
    case 2:
      digitalWrite(bIn_0, LOW);
      digitalWrite(bIn_1, HIGH);
      digitalWrite(bIn_2, LOW);
      digitalWrite(bIn_3, LOW);
      breaker = 7;
      break;
      
    case 3:
      digitalWrite(bIn_0, HIGH);
      digitalWrite(bIn_1, HIGH);
      digitalWrite(bIn_2, LOW);
      digitalWrite(bIn_3, LOW);
      breaker = 7;
      break;
      
    case 4:
      digitalWrite(bIn_0, LOW);
      digitalWrite(bIn_1, LOW);
      digitalWrite(bIn_2, HIGH);
      digitalWrite(bIn_3, LOW);
      breaker = 6;
      break;
      
    case 5:
      digitalWrite(bIn_0, HIGH);
      digitalWrite(bIn_1, LOW);
      digitalWrite(bIn_2, HIGH);
      digitalWrite(bIn_3, LOW);
      breaker = 6;
      break;
      
    case 6:
      digitalWrite(bIn_0, LOW);
      digitalWrite(bIn_1, HIGH);
      digitalWrite(bIn_2, HIGH);
      digitalWrite(bIn_3, LOW);
      breaker = 5;
      break;
    
    case 7:
      digitalWrite(bIn_0, HIGH);
      digitalWrite(bIn_1, HIGH);
      digitalWrite(bIn_2, HIGH);
      digitalWrite(bIn_3, LOW);
      breaker = 5;
      break;
      
    case 8:
      digitalWrite(bIn_0, LOW);
      digitalWrite(bIn_1, LOW);
      digitalWrite(bIn_2, LOW);
      digitalWrite(bIn_3, HIGH);
      breaker = 4;
      break;
      
    case 9:
      digitalWrite(bIn_0, HIGH);
      digitalWrite(bIn_1, LOW);
      digitalWrite(bIn_2, LOW);
      digitalWrite(bIn_3, HIGH);
      breaker = 4;
      break;
      
    case 10:
      digitalWrite(bIn_0, LOW);
      digitalWrite(bIn_1, HIGH);
      digitalWrite(bIn_2, LOW);
      digitalWrite(bIn_3, HIGH);
      breaker = 3;
      break;
      
    case 11:
      digitalWrite(bIn_0, HIGH);
      digitalWrite(bIn_1, HIGH);
      digitalWrite(bIn_2, LOW);
      digitalWrite(bIn_3, HIGH);
      breaker = 3;
      break;
      
    case 12:
      digitalWrite(bIn_0, LOW);
      digitalWrite(bIn_1, LOW);
      digitalWrite(bIn_2, HIGH);
      digitalWrite(bIn_3, HIGH);
      breaker = 2;
      break;
      
    case 13:
      digitalWrite(bIn_0, HIGH);
      digitalWrite(bIn_1, LOW);
      digitalWrite(bIn_2, HIGH);
      digitalWrite(bIn_3, HIGH);
      breaker = 2;
      break;
      
    case 14:
      digitalWrite(bIn_0, LOW);
      digitalWrite(bIn_1, HIGH);
      digitalWrite(bIn_2, HIGH);
      digitalWrite(bIn_3, HIGH);
      breaker = 1;
      break;
      
    case 15:
      digitalWrite(bIn_0, HIGH);
      digitalWrite(bIn_1, HIGH);
      digitalWrite(bIn_2, HIGH);
      digitalWrite(bIn_3, HIGH);
      breaker = 1;
      break;
  }
  
}

// Get data from breakers and then transmit it
void getAndSendData() {
  // Determines whether we do AC or DC calculation
  boolean AC_Load;
  
  // Decides if we're reading in an AC or DC value
  if(readIndex < 4) { AC_Load = false; }
  else { AC_Load = true; }

  // Variables to hold current and voltage readings
  float current = 0;
  float voltage = 0;

  setReadMux(readIndex);
  

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
      for (int i = 0; i < avgSamples; i++) { currentSum += analogRead(readPin); }
  
      // Divide the 10 analogRead values by 10 to get the average value read
      currentSum /= avgSamples;

      // Perform current calculation and scale up the current by 10 to represent real house current
      tempCurrent = 4.88 * currentSum;
      tempCurrent = (tempCurrent - vref) * sensitivity;
  
      // Ignore negative current
      if (tempCurrent < 65) { tempCurrent = 0; }

      // If this current is greater than the other currents we've found, use this as the max value
      if (tempCurrent > current) { current = tempCurrent; }
    }

    // Increment to get voltage from the same breaker, pausing to allow switch
    readIndex++;
    setReadMux(readIndex);
    delay(1000);
    
    /*
       Voltage Calculation
    */
    
    // Read the value from the voltage pin and scale it up for the AC load
    voltage = analogRead(readPin) * 24 * .00488;
  } // End of AC Loop

  // DC Load
  else {
    // Perform Aryana's DC Current conversion calculations, use conversion factor for analogRead, and scale by 10
    current = (analogRead(readPin) * .00488 * 2.7717 - 6.9694) * 10;
    if (current < 0) { current = 0; }
    
    //Increment to get the right value from breaker
    readIndex++;
    setReadMux(readIndex);
    delay(1000);

    // Scale voltage up by 10 for DC load
    voltage = analogRead(readPin) * .00488* 10;
  } // End of DC Loop

  //String representations of voltage, current, and breaker number
  String voltageString = String(voltage);
  String currentString = String(current);
  String breakerString = String(breaker);

  // Some Debug Messages to print to Serial Monitor
  Serial.print( "Sending Voltage and Current : [" );
  Serial.print(voltage);
  Serial.print( "V, " );
  Serial.print(current);
  Serial.print( "mA, Breaker ");
  Serial.print(breaker); 
  Serial.print( "]  -> " );

  // Payload to send to MQTT Broket as {voltage, current} reading
  String payload = "{";
  payload += voltageString;
  payload += ", ";
  payload += currentString;
  payload += ", ";
  payload += breakerString;
  payload += "}";

  // Send payload
  char msg[100];
  
  payload.toCharArray(msg, 100);
  
  if (!client.publish( "boarduino/publish", msg )) { Serial.println("Publish failed"); }
  client.loop();
  
  Serial.println(msg);

  // Increment for next passthrough
  readIndex++;

  // If we read in the last value from the breakers, reset back to the start
  if (readIndex == 16) { readIndex = 0; }

  // 5 Second period to receive messages
  unsigned long receiveLoop = millis() + 5000;
  while(millis() < receiveLoop) { client.loop(); }
  
}

// When we receive an MQTT message, print it out to the screen
// Change breaker state of that breaker
// Send publish message to SetBreakerState to acknowledge reception
void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("]  ");
  char breakerNum = (char)payload[0];

  Serial.print("Breaker ");
  Serial.print(breakerNum);
  Serial.println(" to be toggled");
  

  // Send acknowledge back to cloud
  char attributes[] = "Received!";
  if (!client.publish("SetBreakerState", attributes )) { Serial.println("Failed!"); }
  client.loop();

  // Ensure that the breaker starts with low signal, so high signal will toggle it
  digitalWrite(togglePin, LOW);
  
  // Sets the address of the write mux, and waits long enough to ensure it gets low signal
  setWriteMux(breakerNum);
  delay(500);

  // Write high and hold it for 5 seconds to allow it to be received
  digitalWrite(togglePin, HIGH);
  delay(5000);

  // Back down to low
  digitalWrite(togglePin, LOW);
}

// Start wifi module
void InitWiFi() {
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

// Reconnect the connection
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
      Serial.println( " : retrying in 2 seconds]" );
      // Wait 2 seconds before retrying
      delay(2000);
    }
  }
}


