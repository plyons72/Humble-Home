#include "eRCaGuy_NewAnalogRead.h"


//Global constants
//constants required to determine the voltage at the pin
const float MAX_READING_10_bit = 1023.0;
const float MAX_READING_11_bit = 2046.0;
const float MAX_READING_12_bit = 4092.0;
const float MAX_READING_13_bit = 8184.0;
const float MAX_READING_14_bit = 16368.0;
const float MAX_READING_15_bit = 32736.0;
const float MAX_READING_16_bit = 65472.0;
const float MAX_READING_17_bit = 130944.0;
const float MAX_READING_18_bit = 261888.0;
const float MAX_READING_19_bit = 523776.0;
const float MAX_READING_20_bit = 1047552.0;
const float MAX_READING_21_bit = 2095104.0;

void setup() 
{
  analogReference(DEFAULT);
  Serial.begin(9600);
}

void loop() 
{
  //local variables
  int current_pin = A0;
  int voltage_pin = A1;

  
  int bits_of_precision = 14;
  int num_samples = 10;
  
  float current = adc.newAnalogRead(current_pin, bits_of_precision, num_samples);
  float voltage = adc.newAnalogRead(voltage_pin, bits_of_precision, num_samples);

  boolean ac_load = true;

  current = 5.0 * current / MAX_READING_14_bit;
  voltage = 5.0 * voltage / MAX_READING_14_bit;
  
  Serial.print("Voltage = ");
  Serial.print(voltage, 5); 
  Serial.println("V");
  Serial.println("");

  Serial.print("Voltage Representing Current = ");
  Serial.print(current, 5); 
  Serial.println("V");
  Serial.println("");

  if (ac_load) {
    // Perform Aryana's AC current calculations and scale by 10 (voltage by 24 for AC)
    current = (current * .01 - .0775) * 10;
    voltage *= 24;
  }
  
  else {
    // Perform Aryana's DC current calculations and scale by 10 (voltage by 10 for DC)
    current = (current * 2.7717 - 6.9694) * 10;
    voltage *= 10;
  }
  

  // String representations of voltage, current, and power
  String voltage_string = String(voltage);
  String current_string = String(current);


  // Just debug messages
  Serial.print( "{ Voltage: " );
  Serial.print(voltage_string); 
  Serial.print( "V, " );
  Serial.print(" Current: ");
  Serial.print(current_string); 
  Serial.print( "A }" );
  
  // Delay before reading
  delay(3000);
}

