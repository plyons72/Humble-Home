#include <WiFiEspClient.h>
#include <WiFiEsp.h>
#include <WiFiEspUdp.h>
#include <PubSubClient.h>
#include <SoftwareSerial.h>


#define SSID "Embedded Systems Class"
#define PASS "embedded1234"

/*
// Home WiFi Network
#define SSID "LAN-Solo"
#define PASS "CRtDDc4X"
*/
// Token for humblehome device on thingsboard server
#define TOKEN "lMo6q9a6jUKbqTGLtTVH"

// Endpoint for the server
char humblehome_server[] = "ec2-54-209-17-201.compute-1.amazonaws.com";

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
  client.setServer( humblehome_server, 1883 );
}

void loop() {
  status = WiFi.status();
  if ( status != WL_CONNECTED) {
    while ( status != WL_CONNECTED) {
      Serial.print("Attempting to connect to WPA SSID: ");
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
  // Get multiplier to find the real life AC/DC voltages represented by our scaled down circuit
  // .0049 multiplied by the analog read value gives voltage between 0 and 5
  float ac_voltage = .1176; // .0049 * scaling factor of 24
  float dc_voltage = .049; // .0049 * scaling factor of 10

  
  
  Serial.println("Collecting Circuit Data");

  // Voltage should be read in through A0
  //ac_voltage *= analogRead(A0);
  dc_voltage *= analogRead(A0);

  // Calculate the current for DC current given Aryana's Equations (.0049 = analog read conversion * 10 (scaling factor)
  float dc_current = .049 * analogRead(A1);
  dc_current *= 1.4898;
  dc_current -= 3.7468;

  //float ac_power = ac_current * ac_voltage * ac_power_factor;
  float dc_power = dc_current * dc_voltage * .08;

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

  //String

  String voltage = String(dc_voltage);
  String current = String(dc_current);
  String power = String(dc_power);


  // Just debug messages
  Serial.print( "Sending Voltage and Current : [" );
  Serial.print(voltage); 
  Serial.print( "," );
  Serial.print(current); 
  Serial.print( "," );
  Serial.print(power);
  Serial.print( "]   -> " );

  // Prepare a JSON payload string
  String payload = "{";
  payload += "\"voltage\":"; payload += voltage; payload += ",";
  payload += "\"current\":"; payload += current;
  payload += "\"power\":"; payload += power;
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
    Serial.println(SSID);
    // Connect to WPA/WPA2 network
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
    //if ( client.connect("embedded", mqtt_user, mqtt_pass) ) {
    if ( client.connect("HUMBLEHOME", TOKEN, NULL) ) {
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


