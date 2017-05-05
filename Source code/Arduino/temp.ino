#include <DHT.h>

#define DHTPIN A0
#define DHTTYPE DHT21

DHT dht(DHTPIN, DHTTYPE);

void setup(){
  Serial.begin(9600);
  dht.begin();
}
void loop(){
  delay(2000);
  float h = dht.readHumidity();
  // Read temperature as Celsius
  float t = dht.readTemperature();  //섭씨
  // Read temperature as Fahrenheit
  float f = dht.readTemperature(true); //화씨

  if(isnan(h) || isnan(t) || isnan(f)){
    Serial.println("Failed to read from DHT sensor!");
    return;
  }

  float hi = dht.computeHeatIndex(f,h);

 Serial.print("Humidity: "); 
  Serial.print(h);
  Serial.print(" %");
  Serial.print("\t");
  Serial.print("Temperature: "); 
  Serial.print(t);
  Serial.print(" *C ");
  Serial.print(f);
 // Serial.print(" *F\t");
 // Serial.print("Heat index: ");
 // Serial.print(hi);
  Serial.println(" *F");
  
  
}

