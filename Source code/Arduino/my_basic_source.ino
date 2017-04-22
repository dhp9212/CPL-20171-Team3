int incomingByte = 0;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  if(Serial.available()>0)
  {
    //read the incoming byte

    Serial.write(" : ");
    incomingByte = Serial.read();
    //byte to chart
    char myChar = char(incomingByte);
    
    //Serial.print(" recived : ");
    //Serial.println(myChar);
    Serial.write(incomingByte);
    
    
  }
}
