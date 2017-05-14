//릴레이모듈 : digital 10
int relay = 10;
//통신을 위한 byteStream : inputChar

//static string len : 7
String relay_turn_on = "TESTON:";
String relay_turn_off = "TESTOF:";
int num = 7;

enum control{
  on, off, idle
};

//릴레이 컨트롤 변수 rc
control rc = idle;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(relay, OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:

  /*  Serial.readBytesUntil(char,buffer,length)
   *  char : 종료문자열
   *  buffer : 입력받은 데이터를 저장할 buffer
   *  length : 입력받을 문자의 길이
   *  반환 : 읽어온 데이터의 길이 byte값
   *  byte leng = Serial.readByteUntil(  );
   */
  char input[100] = {};
  String temp="";
  int count =0 ;
  if(Serial.available()){
    //Serial.readBytesUntil(':',input,num);
    Serial.readBytes(input,num);
    
    for(count;count<num;count++){
      temp = temp +input[count];
    }
    if(count < num)
    {
      return;
    }
    else{
      //Serial.write(input);
      if(temp.equals(relay_turn_on)){
        //Serial.write("turn_on");
        digitalWrite(relay,LOW); //릴레이 on
        delay(100);
      }
      else if(temp.equals(relay_turn_off)){
        //Serial.write("turn_off");
        digitalWrite(relay,HIGH);  //릴레이 off
        delay(100);
      }
      count = 0;
      temp = "";
  }
  }
}

