#include <DHT.h>
#include <lm35.h>
#include <stdio.h>

#define DHTPIN A1
#define DHTTYPE DHT21

#define ID "RAS"
#define TEMP "TEMP" //온도
#define HUMID "HUMI" //습도
#define MOTOR_L "APP_MOTOR_LE00000"//모터
#define MOTOR_R "APP_MOTOR_RI00000"//모터
#define MOTOR_OFF "APP_MOTOR_OF00000"//모터
#define RELAY_HUMID_ON "APP_HUMID_ON00000"//릴레이 가습기
#define RELAY_HUMID_OFF "APP_HUMID_OF00000"//릴레이 가습기
#define RELAY_HUMID_AUTO "APP_HUMID_AU00000"//릴레이 가습기
#define RELAY_HITTE_ON "APP_HITTE_ON00000"//릴레이 히터
#define RELAY_HITTE_OFF "APP_HITTE_OF00000"//릴레이 히터
#define RELAY_HITTE_AUTO "APP_HITTE_AU00000"//릴레이 히터
#define RELAY_LIGHT_ON "APP_LIGHT_ON00000"//릴레이 전등
#define RELAY_LIGHT_OFF "APP_LIGHT_OF00000"//릴레이 전등
#define RELAY_LIGHT_AUTO "APP_LIGHT_AU00000"//릴레이 전등
#define num 17
#define FREQUENCY 15 //아두이노 ->라즈베리 빈도수 조절

//////////////////////////////////////////////////////////////////////////////////
int moter = 0;

// 센서 핀 정보 /////////////////////////////////////////////////////////////////
lm35 t2(A0);
int motor1 = 9;
int motor2 = 8;
DHT dht(DHTPIN, DHTTYPE);
int incomingByte = 0;

//릴레이모듈 : digital 10
int hitte= 10;
int humid = 11;
int light = 12;

//아두이노 -> 라즈베리 빈도수 조절
int frequency = 0;

//////////////////////////////////////////////////////////////////////////////////
void setup(){
  Serial.begin(9600);
  dht.begin();

   pinMode(motor1,OUTPUT);
   pinMode(motor2,OUTPUT);
}
void loop(){
  /******************************** 아두이노 -> 라즈베리 *************************************************/
  if(frequency >= FREQUENCY){
      frequency = 0;
      // 1번 온도, 습도 값 받는곳////////////////////////////////////////////////////
      float h = dht.readHumidity();
      // Read temperature as Celsius
      float t = dht.readTemperature();  //섭씨
      // Read temperature as Fahrenheit
      float f = dht.readTemperature(true); //화씨
    
      // 2번 온도 값 받는곳 /////////////////////////////////////////////////////////
      t2.MeasureTemp();
      
      // 온도값 출력 ////////////////////////////////////////////////////////////////
      printT1(h,t);
      printT2(t2);
  }
  /********************************** 라즈베리 -> 아두이노 ***************************************************/
  char input[100] = {};
  String temp="";
  int count = 0;
  if(Serial.available()){
    Serial.readBytes(input,num);
    
    for(count;count<num;count++){
      temp = temp +input[count];
    }
    if(count < num)
    {
      return;
    }
    else{
      //제어 명령
      control(temp);
    
      count = 0;
      temp = "";
    }
  }
  
  /***********************************************************************************************************/

  frequency = frequency + 1; //빈도수 관련
  delay(2000);
}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                          // 함수 영역 //
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
void control(String temp){

  //모터 제어
  if(temp.equals(MOTOR_L)){
    rotationL(motor1,motor2);
  }else if(temp.equals(MOTOR_R)){
    rotationR(motor1,motor2);
  }else if(temp.equals(MOTOR_OFF)){
    rotationStop(motor1,motor2);
  }
  //가습기 제어
  else if(temp.equals(RELAY_HUMID_ON)){
    digitalWrite(humid,LOW);
  }else if(temp.equals(RELAY_HUMID_OFF)){
    digitalWrite(humid,HIGH);
  }
  //히터 제어
  else if(temp.equals(RELAY_HITTE_ON)){
    digitalWrite(hitte,LOW);
  }else if(temp.equals(RELAY_HITTE_OFF)){
    digitalWrite(hitte,HIGH);
  }
  //전등 제어
  else if(temp.equals(RELAY_LIGHT_ON)){
     digitalWrite(light,LOW);
  }else if(temp.equals(RELAY_LIGHT_OFF)){
     digitalWrite(light,HIGH);
  }
}



void printT1(float h, float t){
    if(isnan(h) || isnan(t)){
    //Serial.println("Failed to read from DHT sensor!");
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

//모터 제어(0~255)
void rotationR(int m1, int m2){
  analogWrite(m1,127);  //50%의속력
  analogWrite(m2,0);
}
void rotationL(int m1, int m2){
  analogWrite(m1,0);
  analogWrite(m2,127);
}
void rotationStop(int m1, int m2){
   analogWrite(m1,0);
   analogWrite(m2,0);
}


