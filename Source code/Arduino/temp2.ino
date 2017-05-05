#include <lm35.h>
lm35 t2(A1);


void setup() {
  // put your setup code here, to run once:
 Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  t2.MeasureTemp();
  Serial.print(t2.TempInCelcius);
  Serial.println("C");
  Serial.print(t2.TempInFahrenheit); // print temp in Fahrenheit
  Serial.println("F");
  delay(2000);
}
