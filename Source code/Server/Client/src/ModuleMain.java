import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
//사용 예제를 위한 import
import java.util.Scanner;
 
public class ModuleMain implements SerialPortEventListener {
 
    SerialPort serialPort;
    String readString = "";
 
    private static final String PORT_NAMES[] = {   
          "/dev/tty.usbserial-A9007UX1",//MAX OS X
          //"/dev/ttyACM0" ,//Linux
          "/dev/ttyS33",
            "COM6", // Windows 
            };
    
    
    private InputStream input; 
    private OutputStream output;
    private static final int TIME_OUT = 2000; 
    private static final int DATA_RATE = 9600;
    
    private static ModuleMain main;
    
    // for chunk control
    private final int max_chunk = 10;
    private int chunk_cnt = 0;
    private String full_chunk = "";
    
    // for CoAP
    private static String method = "PUT";
    //private static String uri = "coap://192.168.10.102:5683/Platform";
    private static String uri = "coap://54.71.172.224:5683/Platform";
    private static String payload = "Rasp에서 가는 packet";
 
    public void initialize() { 
       
       //System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");
       
        CommPortIdentifier portId = null; 
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
 
        while (portEnum.hasMoreElements()) { 
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();

            System.out.println(currPortId.getName());
            
            for (String portName : PORT_NAMES) { 
                if (currPortId.getName().equals(portName)) { 
                    portId = currPortId; 
                    break; 
                } 
            } 
        }
        
        if (portId == null) { 
            System.out.println("Could not find COM port."); 
            return; 
        }
 
        try { 
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
        
            serialPort.setSerialPortParams(DATA_RATE, 
                    SerialPort.DATABITS_8, 
                    SerialPort.STOPBITS_1, 
                    SerialPort.PARITY_NONE);
         
            input = serialPort.getInputStream(); 
            output = serialPort.getOutputStream(); 
         
            System.out.println("[ info ] :: " + input);
            System.out.println("[ info ] :: " + output);
            serialPort.addEventListener(this); 
            serialPort.notifyOnDataAvailable(true);

        } catch (Exception e) { 
            System.err.println(e.toString()); 
        } 
    }

    public synchronized void close() { 
        if (serialPort != null) { 
            serialPort.removeEventListener(); 
            serialPort.close(); 
        } 
    }

    /*    string 만드는 함수
     * 	  resList : 결과를 ArrayList<String>형태로 반환함
     * 	  
     * 	  readString : 전역 변수
     */

    public ArrayList makeSerial(String data){
    	//resList : 결과를 리턴할 변수
    	ArrayList resList = new ArrayList<String>();
    	
    	//readString : 전역 변수
    	
    	//스트링의 처음을 구분
    	if(readString.length() == 0){
    		if(data.substring(0,1).equals("R")){
    			String[] array = data.split("@");
    			readString = readString + array[0];
        		
        		if(array.length >1){
        			int i = 1;
        			while(true){
        			
        				//string 처리가 완료된 문자열 저장
        				resList.add(readString);
        			
	        			//System.out.println(readString);
	        			readString="";
	            		readString=readString + array[i++];
	            		if( i == array.length)
	            			break;
        			}
        		}
    		}
    	}
    	//스트링 구분자 "@"
    	else{
    		String[] array = data.split("@");
    		readString = readString + array[0];
    		if(array.length >1){
    			int i = 1;
    			while(true){
    				//string 처리가 완료된 문자열 저장
    				resList.add(readString);
    				
	    			//System.out.println(readString);
	    			readString="";
	        		readString=readString + array[i++];
	        		if( i == array.length)
	        			break;
    			}
    		}
    	}
    	//스트링 길이 17
    	if(readString.length() >= 17){
    		//string 처리가 완료된 문자열 저장
			resList.add(readString);
    		//System.out.println(readString);
    		readString="";
    	}
    	
    	return resList;
    }
    
    public synchronized void serialEvent(SerialPortEvent oEvent) { 
    	
    	String ret = "";
    	
    	
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) { 
            try { 
                int available = input.available();
                byte chunk[] = new byte[available]; 
                input.read(chunk, 0, available); 
                
                
                ArrayList arr = makeSerial(new String(chunk));
                
                for(int i = 0;i < arr.size();i++){
                	ret += arr.get(i);
                	if(i + 1 != arr.size())
                		ret += "/";
                }
                
                payload = ret;
                if(payload.length() <= 1){
                	return;
                }
                ModuleCoapSend coap = new ModuleCoapSend(method, uri, payload);
                
                //통신받은걸 아두이노로 전송
                //System.out.println(new String(chunk));
                //output.write(chunk);
           
                System.out.println(">> " + payload);      	
                
                
            } catch (Exception e) { 
                System.err.println(e.toString()); 
            } 
        }
    }
    
    public void push(String message){
    	 
         	byte[] byteArray = message.getBytes();
         	try {
         		System.out.println(input);
         		System.out.println(output);
				output.write(byteArray);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
    	System.out.println(":: "+message);
    }
    
    public static void main(String[] args) throws Exception {
    	
    	/* Linux OS 상에서 구동 시 Terminal에 입력
    	if(args.length != 1){
    		System.out.println("Usage : java -Djava.library.path/usr/lib/jni -cp /usr/share/java/RXTXcomm.jar -jar FILENAME.jar IP_ADDRESS");
    		System.exit(0);
    	}
    	*/
        main = new ModuleMain(); 
        
        ModuleCoapServer server = new ModuleCoapServer();
        server.addEndpoints();
        server.start();
        
        main.initialize(); 
        System.out.println("Module Accept Started");
        
    } 
    
    public InputStream getInput(){
    	return input;
    }
    public OutputStream getOutput(){
    	return output;
    }
    
  
	public static ModuleMain getModule() {
		// TODO Auto-generated method stub
		return main;
	}

}