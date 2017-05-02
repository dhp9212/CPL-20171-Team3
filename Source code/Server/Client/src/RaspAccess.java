import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

//사용 예제를 위한 import
import java.util.Scanner;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
 
public class RaspAccess implements SerialPortEventListener {
 
    SerialPort serialPort;
 
    private static final String PORT_NAMES[] = {   
          "/dev/tty.usbserial-A9007UX1",//MAX OS X
          "/dev/ttyACM0" ,//Linux
            "COM6", // Windows 
            };
    
    
    private InputStream input; 
    private OutputStream output;
    private static final int TIME_OUT = 2000; 
    private static final int DATA_RATE = 9600;
    
    
    // for chunk control
    private final int max_chunk = 10;
    private int chunk_cnt = 0;
    private String full_chunk = "";
    
    // for CoAP
    private static String method = "PUT";
    private static String uri = "coap://54.71.172.224:5683/Server";
    //private String url = "coap://192.168.10.100/ServerResource";
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

    public synchronized void serialEvent(SerialPortEvent oEvent) { 
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) { 
            try { 
                int available = input.available();
                byte chunk[] = new byte[available]; 
                input.read(chunk, 0, available); 
                
                payload = new String(chunk);
                
                CoapAccess coap = new CoapAccess(method, uri, payload);
                //통신받은걸 아두이노로 전송
                //System.out.println(new String(chunk));
                //output.write(chunk);
           
                //test 용
                //아두이노로 넘겨준걸 프린트함
                System.out.println(">> " + new String(chunk));
                
            } catch (Exception e) { 
                System.err.println(e.toString()); 
            } 
        }
    }
    
    public void testing(){
    	 Scanner in = new Scanner(System.in);
     
    	 System.out.println("Please enter text");
         while(true){
         	String text = in.next(); 
         	byte[] byteArray = text.getBytes();
         	try {
				output.write(byteArray);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         }
    }
    
    public static void main(String[] args) throws Exception {
       
        RaspAccess main = new RaspAccess(); 
        main.initialize(); 
        System.out.println("Started"); 
        CoapAccess coap = new CoapAccess(method, uri, payload);
        
        //main.testing();
    } 
}