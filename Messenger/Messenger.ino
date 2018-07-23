
// Pin to read voltage from
int sensorPin1 = A0; 
int sensorPin2 = A1;

// Holds scaled voltage read in (0 to 1023)
int scaledVoltage1 = 0;
int scaledVoltage2 = 0;

// Sets analog reference to 5v default for voltage calculations.
// This sets the max value to be read in at the analog pin
// Also sets the scaling rate for analogRead
analogReference(DEFAULT);

//Begin the serial transmission signal with a baud rate of 57600 b/s
Serial.begin(9600);
}


/*
 * Reads in the voltage through pin A0 on boarduino
 * Converts it to its value in volts to display to the user
 */
void loop() {
  
  // Read from A5
  scaledVoltage1 = analogRead(sensorPin1);
  scaledVoltage2 = analogRead(sensorPin2);

  // Perform calculation to get voltage value
  // Each unit in the scaledVoltage variable is equal to 4.88mV
  float voltage1 = scaledVoltage1 * .00488;
  float voltage2 = scaledVoltage2 * .00488;

  // Print results to terminal
  Serial.print("The voltage coming in through A0 is : ");
  Serial.print(voltage1);
  Serial.print("V\n");

  /*
  // Print results to terminal
  Serial.print("The voltage coming in through A1 is : ");
  Serial.print(voltage2);
  Serial.print("V\n");

  */
  Serial.print("\n\n\n\n\n\n\n\n\n");
  
  // Sleep for 3 seconds
  delay(2000);

}
