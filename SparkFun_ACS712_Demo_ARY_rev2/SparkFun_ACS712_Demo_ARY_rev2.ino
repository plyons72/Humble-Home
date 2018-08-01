//Tom Revision

const int analogInPin = A0;

const int avgSamples = 10; //DO NOT CHANGE
int sensorValue = 0;

int count_reset=0;
float sensor_array[100]={};
float maxi=0;
int kmax=0;

float sensitivity = 100.0 /60.0; //100mA per 55mV = 0.2. DO NOT CHANGE
float Vref = 2500; // Output voltage with no current: ~ 2500mV or 2.5V

void setup() {
  // initialize serial communications at 9600 bps:
  Serial.begin(9600);
  
}

void loop() {
  //Reset values for finding the max/average
  if(count_reset>100){
    count_reset=0;
    maxi=0;
    kmax=0;
    for (int i=0; i<100;i++){
      sensor_array[i]=0;
    }
  }

  // Read in analog values and add them together
  for (int i = 0; i < avgSamples; i++){
    sensorValue += analogRead(analogInPin);
  }
  
  //take average and adjust for 2.5V offset
  sensorValue = sensorValue / avgSamples;
  float voltage = 4.88 * sensorValue;
  float current = (voltage - Vref) * sensitivity;
  //remove all negative values, only use positive values
  if (current>=65){
      current=current;
  }else if(current<65){
    current=0;
  }
  
  //Store up to 100 current readings into an int array
  sensor_array[count_reset]=current;
  //go through array you are saving values to and find the current maximum value

  for (int k=0; k<100; k++){
    if(sensor_array[k]>maxi){
      maxi=sensor_array[k];
      kmax=k;
    }
  }
  //print the current maximum value. 
  //uncomment if statement if you just want the absolute maximum value of the array of storred values
  //if (count_reset==100){
    Serial.print(maxi);

    //Serial.print("mA");
  //}
  //Print the current value. I'm not sure yet which value will be most accurate
 // Serial.print("\n");
  //Serial.print(current);
  //Serial.print("mA");


  // -- DO NOT UNCOMMENT BELOW THIS LINE --
  Serial.print("\n");
delay(1);
  // Reset the sensor value for the next reading
  sensorValue = 0;
  //increase count value
  count_reset++;
}
