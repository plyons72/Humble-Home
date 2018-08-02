// When a command is entered in to the serial monitor on the computer 
// the Arduino will relay it to the ESP8266


// Wiring setup
//ARDUINO--------------------------------ESP8266
// 3.3V------------------------------------VCC
// 3.3V------------------------------------CH_PD
// GND-------------------------------------GND
// D13-------------------------------------TX
// D12-----------Voltage Divider-----------RX ( 5v Signal from D13 could fry RX of Wifi Module)

#include <SoftwareSerial.h>
SoftwareSerial sw(13,12); // (RX, TX)
 
void setup() 
{
    // Arduino Serial
    Serial.begin(9600); 

    while(!Serial){ ; }
    sw.begin(9600);

 
    Serial.println("");
    Serial.println("Remember to to set Both NL & CR in the serial monitor.");
    Serial.println("Ready");
    Serial.println("");  

    delay(1000);
    
}
 
void loop() 
{
 if (Serial.available()) { sw.write(Serial.read()); }
 if (sw.available()) { Serial.write(sw.read()); }  
}
