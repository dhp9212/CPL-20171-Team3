import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
//��� ������ ���� import
import java.util.Scanner;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
 
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
    
    private Calendar mCalendar ;	//����ð����� ���� ������ ����
    TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");	//����� ����
    
    // for auto control
    private static String MOTOR_L ="APP_MOTOR_LE00000";//����
    private static String MOTOR_R ="APP_MOTOR_RI00000";//����
    private static String MOTOR_OFF ="APP_MOTOR_OF00000";//����
    private static String RELAY_HUMID_ON ="APP_HUMID_ON00000";//������ ������
    private static String RELAY_HUMID_OFF ="APP_HUMID_OF00000";//������ ������
    private static String RELAY_HUMID_AUTO ="APP_HUMID_AU00000";//������ ������
    private static String RELAY_HITTE_ON ="APP_HITTE_ON00000";//������ ����
    private static String RELAY_HITTE_OFF ="APP_HITTE_OF00000";//������ ����
    private static String RELAY_HITTE_AUTO ="APP_HITTE_AU00000";//������ ����
    private static String RELAY_LIGHT_ON ="APP_LIGHT_ON00000";//������ ����
    private static String RELAY_LIGHT_OFF ="APP_LIGHT_OF00000";//������ ����
    private static String RELAY_LIGHT_AUTO ="APP_LIGHT_AU00000";//������ ����
    private boolean LIGHFLAG = false;
    private boolean HITTFLAG = false;
    private boolean HUMIFLAG = false;
    private boolean MOTOFLAG = false;
    private int temp = 25;	//auto ��
    private int humi = 30;	//auto ��
    private float t1, t2, h = 0;
    private int motor = 0, light = 0, humid = 0; //0�� ����, 1�� ����(������/ �۵���)
    private int mtime = 10000;	//mtime : ���͸� ����� �ð�(�����ð� : 10�� ����)
    
    // for chunk control
    private final int max_chunk = 10;
    private int chunk_cnt = 0;
    private String full_chunk = "";
    
    // for CoAP
    private static String method = "PUT";
    //private static String uri = "coap://192.168.10.102:5683/Platform";
    private static String uri = "coap://54.71.172.224:5683/Platform";
    private static String payload = "Rasp���� ���� packet";
 
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

    /*    string ����� �Լ�
     * 	  resList : ����� ArrayList<String>���·� ��ȯ��
     * 	  
     * 	  readString : ���� ����
     */

    public ArrayList makeSerial(String data){
    	//resList : ����� ������ ����
    	ArrayList resList = new ArrayList<String>();
    	
    	//readString : ���� ����
    	
    	//��Ʈ���� ó���� ����
    	if(readString.length() == 0){
    		if(data.substring(0,1).equals("R")){
    			String[] array = data.split("@");
    			readString = readString + array[0];
        		
        		if(array.length >1){
        			int i = 1;
        			while(true){
        			
        				//string ó���� �Ϸ�� ���ڿ� ����
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
    	//��Ʈ�� ������ "@"
    	else{
    		String[] array = data.split("@");
    		readString = readString + array[0];
    		if(array.length >1){
    			int i = 1;
    			while(true){
    				//string ó���� �Ϸ�� ���ڿ� ����
    				resList.add(readString);
    				
	    			//System.out.println(readString);
	    			readString="";
	        		readString=readString + array[i++];
	        		if( i == array.length)
	        			break;
    			}
    		}
    	}
    	//��Ʈ�� ���� 17
    	if(readString.length() >= 17){
    		//string ó���� �Ϸ�� ���ڿ� ����
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
                	//�ڵ� ������ ���� ����, �µ� ����
                	String[] array = arr.get(i).toString().split("_");
                	String[] array2 = array[2].split(":");
                	if(array2[0].equals("ONE") && array[1].equals("TEMP")){
                		t1 = Float.parseFloat(array2[1]);
                	}else if(array2[0].equals("ONE") && array2[0].equals("HUMI")){
                		h = Float.parseFloat(array2[1]);
                	}else if(array2[0].equals("TWO") && array[0].equals("TEMP")){
                		t2 = Float.parseFloat(array2[1]);
                	}
                	
                	//������ ������ ���� ����
                	ret += arr.get(i);
                	if(i + 1 != arr.size())
                		ret += "/";
                }
                
                payload = ret;
                if(payload.length() <= 1){
                	return;
                }
                ModuleCoapSend coap = new ModuleCoapSend(method, uri, payload);
                
                //��Ź����� �Ƶ��̳�� ����
                //System.out.println(new String(chunk));
                //output.write(chunk);
           
                System.out.println(">> " + payload);      	
        
                if(LIGHFLAG == true){
                	mCalendar= Calendar.getInstance(tz);
                	int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
                	//12�ú��� 15�ñ��� Ʋ����
                	if( hour >=12 && hour < 15 && light == 0){
                		output.write(RELAY_LIGHT_ON.getBytes());
                		light = 1;
                	}//open
                	else if(light == 1){
                		output.write(RELAY_LIGHT_OFF.getBytes());
                		light = 0;
                	}//c
                }
                if(HITTFLAG == true){
                	if( t1 < temp - 3 && temp == 0){
                		output.write(RELAY_HITTE_ON.getBytes());
                	}
                	else if(t1 > temp + 3 && temp == 1){
                		output.write(RELAY_HITTE_OFF.getBytes());
                	}
                }
                if(HUMIFLAG == true){
                	if( h < humi - 3 && humid == 0){
                		output.write(RELAY_HUMID_ON.getBytes());
                	}
                	else if(h > humi + 3 && humid == 1){
                		output.write(RELAY_HUMID_OFF.getBytes());
                	}
                }
                if(MOTOFLAG == true){
                	mCalendar= Calendar.getInstance(tz);
                	int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
                	
                	//motor == 0 : Ŀư�� ��������
                	//motor == 1 : Ŀư�� ��������
                	if( motor == 0 && hour >= 7 && hour < 22){
                		output.write(MOTOR_L.getBytes());
                		motor = 1;
                		mTimer();	//10�ʵ��� ������ ����
                	}//open
                	else if( motor == 1 && hour >= 22){
                		output.write(MOTOR_R.getBytes());
                		motor = 0;
                		mTimer();	//10�ʵ��� ������ ����
                	}//close
                }
                
                
                
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
         		
         		if(message.equals(RELAY_HUMID_ON) || message.equals(RELAY_HUMID_OFF))
         		{
         			HUMIFLAG = false;
         		}else if(message.equals(RELAY_HUMID_AUTO))
         		{
         			HUMIFLAG = true;
         		}
         		else if(message.equals(RELAY_HITTE_ON) || message.equals(RELAY_HITTE_OFF))
         		{
         			HITTFLAG = false;
         		}
         		else if(message.equals(RELAY_HITTE_AUTO))
         		{
         			HITTFLAG = true;
         		}
         		else if(message.equals(RELAY_LIGHT_ON) || message.equals(RELAY_LIGHT_OFF))
         		{
         			LIGHFLAG = false;
         		}else if(message.equals(RELAY_LIGHT_AUTO))
         		{
         			LIGHFLAG = true;
         		}else if(message.equals(MOTOR_L) || message.equals(MOTOR_R)){
         			mTimer();
         			//10���� ������ �����ǵ��� ��������
         		}
         		
				output.write(byteArray);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
    	System.out.println(":: "+message);
    }
    
    public static void main(String[] args) throws Exception {
    	
    	/* Linux OS �󿡼� ���� �� Terminal�� �Է�
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

	
//������� ���� Ÿ�̸� ����
	public void mTimer(){
		Timer timer = new Timer();
		TimerTask task = new TimerTask(){
			@Override
			public void run(){
				// n���Ŀ� ����� �Լ�
				try {
					output.write(MOTOR_OFF.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		timer.schedule(task,mtime);
	}
	
	
	
}
	
	
