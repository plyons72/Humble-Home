# HumbleHome Embedded Software
### For use with Atmega328 MicroController and ESP8266 WiFi Module

### Folders
* MQTT-Messages
  * Contains MQTT-Messages.ino, the full software for the embedded system, to be loaded onto the MicroController for use
* ESP8266ESP8266_Communicator
  * Contains a sketch used to easily send AT commands to the wifi module

### Libraries
* SoftwareSerial
  * Used to connect the wifi module to the MicroController via a software-defined serial connection for data transmission
* PubSubClient
  * The library containing publish and subscribe support for the MQTT telemetry with our server
* WifiEsp
  * Libraries to allow the ESP-8266 WiFi module to connect to the network through the SoftwareSerial Connection


### Notice
In order to run this software, you will need to wire the wifi module in the following way:
  VCC -> 3.3V
  CH_PD -> 3.3V
  TX -> Digital Pin 13 of MicroController
  RX -> Digital pin 12 of MicroController, with a voltage divider such that the voltage coming into the WiFi module is 3.3V at most. A voltage divider is a good solution, using a 180 Ohm Resistor (connected to pin 12 of MicroController) and a 330 Ohm resistor (grounded).

Additionally, you will need to ensure that your ESP8266 is programmed to communicate at 9600 baud. In order to do this, connect the circuit as mentioned above, and upload the ESP8266ESP8266_Communicator sketch. Open a serial monitor at a Baud Rate of 115200. Send the following command: `AT+CIOBAUD=9600`. This should set the rate appropriately.

Now you're ready to use HumbleHome (just use your own server, and wireless network)
