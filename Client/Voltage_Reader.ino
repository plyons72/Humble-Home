/*
 * Reads in the voltage through pin A0 on boarduino
 * Converts it to its value in volts to display to the user
 */

int sensorPin = A0;
int binaryVoltageIn = 0;

void setup() {
  // Sets analog reference to 5v default for voltage calculations
  analogReference(DEFAULT);
  //Begin the serial transmission signal with a baud rate of 57600 b/s
  Serial.begin(57600);
}

void loop() {
  // Read from A0
  binaryVoltageIn = analogRead(sensorPin);

  // Perform calculation to get voltage value
  float voltageIn = binaryVoltageIn * .00488;

  // Print results to terminal
  Serial.print("The voltage coming in through A0 is : ");
  Serial.print(voltageIn);
  Serial.print("V\n");

  // Sleep for 5 seconds
  delay(5000);
}
