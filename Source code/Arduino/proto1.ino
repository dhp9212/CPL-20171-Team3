#include <DHT.h>
#include <lm35.h>
#include <stdio.h>

#define DHTPIN A1
#define DHTTYPE DHT21

#define ID "RAS"
#define TEMP "TEMP" //온도
#define HUMID "HUMI" //습도


//////////////////////////////////////////////////////////////////////////////////
int moter = 0;

// 센서 핀 정보 /////////////////////////////////////////////////////////////////
lm35 t2(A0);
int moter1 = 9;
int moter2 = 8;
DHT dht(DHTPIN, DHTTYPE);
int incomingByte = 0;
//////////////////////////////////////////////////////////////////////////////////
void setup(){
  Serial.begin(9600);
  dht.begin();

   pinMode(moter1,OUTPUT);
   pinMode(moter2,OUTPUT);
}
void loop(){
  delay(2000);

  // 1번 온도, 습도 값 받는곳////////////////////////////////////////////////////
  float h = dht.readHumidity();
  // Read temperature as Celsius
  float t = dht.readTemperature();  //섭씨
  // Read temperature as Fahrenheit
  float f = dht.readTemperature(true); //화씨
  ///////////////////////////////////////////////////////////////////////////////

  // 2번 온도 값 받는곳 /////////////////////////////////////////////////////////
  t2.MeasureTemp();
  ///////////////////////////////////////////////////////////////////////////////
  // 온도값 출력 ////////////////////////////////////////////////////////////////
  printT1(h,t);
  printT2(t2);
  ///////////////////////////////////////////////////////////////////////////////

  //모터제어는 일단 반복으로만 해둠
  if(moter == 0)
  {
    rotationR(moter1,moter2);
    moter = 1;
  }
  else{
    rotationL(moter1,moter2);
    moter = 0;
  }

  delay(2000);
}

void printT1(float h, float t){
    if(isnan(h) || isnan(t)){
    Serial.println("Failed to read from DHT sensor!");
    return;
  }
  /*test
   
  Serial.print("temp 1: [");
  Serial.print("Humidity: "); 
  Serial.print(h);
  Serial.print(" %");
  Serial.print("\t");
  Serial.print("Temperature: "); 
  Serial.print(t);
  Serial.println(" ]");
  */
  //라즈베리파이로 전송
  //문자열 생성
  //온도
  char temp[18]={};
  char tempt1[5]={};
  //float to string
  // 4: 음수부호와 소수점을 포함한 전체 자리수
  // 1: 소수점을 제외한 소수점 자리수
  dtostrf(t,4,1,tempt1);
  sprintf(temp,"%s_%s_%s:%s@",ID,TEMP,"ONE",tempt1);
  Serial.write(temp);
  Serial.flush();
  
  //습도
  char temp2[18]={};
  char humid[5]={};
  //float to string
  dtostrf(h,4,1,humid);
  sprintf(temp2,"%s_%s_%s:%s@",ID,HUMID,"ONE",humid);
  Serial.write(temp2);
  Serial.flush();
}

void printT2(lm35 t){
  /* test
  Serial.print("temp 2: [");
  Serial.print("Temperature: "); 
  Serial.print(t.TempInCelcius);
  Serial.println(" ]");
 */ //라즈베리파이로 전송
  //문자열 생성
  //온도
  char temp[18]={};
  char tempt2[5]={};
  //float to string
  // 4: 음수부호와 소수점을 포함한 전체 자리수
  // 1: 소수점을 제외한 소수점 자리수
  dtostrf(t.TempInCelcius,4,1,tempt2);
  sprintf(temp,"%s_%s_%s:%s@",ID,TEMP,"TWO",tempt2);
  Serial.write(temp);
  Serial.flush();
}

//모터 제어
void rotationR(int m1, int m2){
  analogWrite(m1,255);
  analogWrite(m2,0);
}
void rotationL(int m1, int m2){
  analogWrite(m1,0);
  analogWrite(m2,255);
}


