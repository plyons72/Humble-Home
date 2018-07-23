#include <WiFiEspClient.h>
#include <WiFiEsp.h>
#include <WiFiEspUdp.h>
#include <PubSubClient.h>
#include <SoftwareSerial.h>

/*
#define WIFI_AP "Embedded Systems Class"
#define WIFI_PASSWORD "embedded1234"
*/

#define WIFI_AP "LAN-Solo"
#define WIFI_PASSWORD "CRtDDc4X"

#define TOKEN "OEgIMF0bBPlBPgIQzVsh"
/*
char mqtt_server[] = "b-f6c789c3-b708-4d73-b004-2a6245bd7c5d-1.mq.us-east-1.amazonaws.com";
char mqtt_client[] = "b-f6c789c3-b708-4d73-b004-2a6245bd7c5d-1";
char mqtt_user[] = "user";
char mqtt_pass[] = "humblehome1896";
*/
/*
char mqtt_server[] = "mqtt.lazyengineers.com";
char mqtt_client[] = "b-f6c789c3-b708-4d73-b004-2a6245bd7c5d-1";
char mqtt_user[] = "lazyengineers";
char mqtt_pass[] = "lazyengineers";*/
char thingsboardServer[] = "demo.thingsboard.io";

// Initialize the Ethernet client object
WiFiEspClient espClient;
PubSubClient client(espClient);
SoftwareSerial soft(13, 12); // RX, TX

int status = WL_IDLE_STATUS;
unsigned long lastSend;


void setup() {
  // initialize serial for debugging
  Serial.begin(9600);
  analogReference(DEFAULT);
  InitWiFi();
  client.setServer( thingsboardServer, 1883 );
}

void loop() {
  status = WiFi.status();
  if ( status != WL_CONNECTED) {
    while ( status != WL_CONNECTED) {
      Serial.print("Attempting to connect to WPA SSID: ");
      Serial.println(WIFI_AP);
      // Connect to WPA/WPA2 network
      status = WiFi.begin(WIFI_AP, WIFI_PASSWORD);
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
  // Get multiplier to find the real life AC/DC voltages represented by our scaled down circuit
  // .0049 multiplied by the analog read value gives voltage between 0 and 5
  float ac_voltage = .0049 * 24;
  float dc_voltage = .0049 * 10;
  
  Serial.println("Collecting Circuit Data");

  // Voltage should be read in through A0
  //ac_voltage *= analogRead(A0);
  dc_voltage *= analogRead(A0);

  // Calculate the current for DC current given Aryana's Equations
  float dc_current = .0049 * analogRead(A1) * 10;
  dc_current *= 1.4898;
  dc_current -= 3.7468;

  /*
  float ac_current = .0049 * analogRead(A0) * 10;
  ac_current *= .0164;
  ac_current -= .0001;
  */

  Serial.print("Voltage: ");
  Serial.print(dc_voltage);
  Serial.print(" Volts\t");
  Serial.print("Current: ");
  Serial.print(dc_current);
  Serial.print(" Amps ");

  String voltage = String(dc_voltage);
  String current = String(dc_current);


  // Just debug messages
  Serial.print( "Sending Voltage and Current : [" );
  Serial.print( voltage ); Serial.print( "," );
  Serial.print( current );
  Serial.print( "]   -> " );

  // Prepare a JSON payload string
  String payload = "{";
  payload += "\"voltage\":"; payload += voltage; payload += ",";
  payload += "\"current\":"; payload += current;
  payload += "}";

  // Send payload
  char attributes[100];
  payload.toCharArray( attributes, 100 );
  client.publish( "v1/devices/me/telemetry", attributes );
  Serial.println( attributes );
}

void InitWiFi()
{
  // initialize serial for ESP module
  soft.begin(9600);
  
  // initialize ESP module
  WiFi.init(&soft);
  // check for the presence of the shield
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    // don't continue
    while (true);
  }

  Serial.println("Connecting to AP ...");
  // attempt to connect to WiFi network
  while ( status != WL_CONNECTED) {
    Serial.print("Attempting to connect to WPA SSID: ");
    Serial.println(WIFI_AP);
    // Connect to WPA/WPA2 network
    status = WiFi.begin(WIFI_AP, WIFI_PASSWORD);
    delay(500);
  }
  Serial.println("Connected to AP");
}

void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
    Serial.print("Connecting to MQTT Server ...");
    // Attempt to connect (clientId, username, password)
    //if ( client.connect("embedded", mqtt_user, mqtt_pass) ) {
    if ( client.connect("Arduino Uno Device", TOKEN, NULL) ) {
      Serial.println( "[DONE]" );
    } else {
      Serial.print( "[FAILED] [ rc = " );
      Serial.print( client.state() );
      Serial.println( " : retrying in 5 seconds]" );
      // Wait 5 seconds before retrying
      delay( 5000 );
    }
  }
}


