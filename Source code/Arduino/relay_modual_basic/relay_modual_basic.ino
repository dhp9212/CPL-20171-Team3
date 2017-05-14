
//릴레이 모듈

int relay = 10; //릴레이에 5v 신호를 보낼 핀 설정

void setup() {
  // put your setup code here, to run once:
  pinMode(relay, OUTPUT); //relay를 output으로 설정한다
}

void loop() {
  // put your main code here, to run repeatedly:
  digitalWrite(relay,HIGH); //릴레이 ON
  delay(1000);  //1초 딜레이
  digitalWrite(relay,LOW);  //릴레이 OFF 
  delay(1000);  //1초 딜레디
}
