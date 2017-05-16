import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;

public class PlatformMain {
	public static final int ServerPort = 5000;
	 public static final String ServerIP = "54.71.172.224"; 
	 
	 public static final String REQUEST_CURRENT_TEMP_HUM = "1";
	 public static final String REQUEST_ACCRUE_TEMP = "2";
	 public static final String REQUEST_STATE = "3";
	 public static final String REQUEST_SIGNUP = "4";
	 public static final String COLSE = "5";
	 
	 public static final String APP_LIGHT_ON = "10";
	 public static final String APP_LIGHT_OF = "11";
    public static final String APP_LIGHT_AU = "12";

    public static final String APP_HITTE_ON = "20";
	 public static final String APP_HITTE_OF = "21";
	 public static final String APP_HITTE_AU = "22";

	 public static final String APP_HUMID_ON = "30";
	 public static final String APP_HUMID_OF = "31";
	 public static final String APP_HUMID_AU = "32";

	 public static final String APP_MOTOR_LE = "40";
	 public static final String APP_MOTOR_RI = "41";
	 public static final String APP_MOTOR_OF = "42";
	 
	 
	 private static String method = "PUT";
	 private static String uri = "coap://27.35.109.109:5683/Module";
	 //private static String uri = "coap://192.168.10.102:5683/Module";
	 //private static String uri = "coap://54.71.172.224:5683/Platform"; // pi address
	 private static String payload = "";
	 
	 private ServerSocket serverSocket = null;
	 Socket client = null;
	 
	 static String id;
	 static String password;
	 
	 public PlatformMain() throws IOException
	 {
		 System.out.println("Server : Start");
		 serverSocket = new ServerSocket(ServerPort);
	 }
	 
	 public Session accept() throws IOException 
	 {
		 System.out.print("Connecting...\n");
		 client = serverSocket.accept();
		 System.out.println("ok");
		 
		 return new Session(client);
	 }
	 
	 public void dispose() 
	 {
		 System.out.println("Dispose");  
		 if (serverSocket  != null) try {serverSocket.close();} catch (Exception e) {/*ignore*/}
	 }
	 
	 static class Session implements Runnable 
	 {  
		 private Socket c_socket = null;
	     private InputStream wfIn = null;  
	     private OutputStream wfOut = null;  
	  
	     public Session(Socket socket) throws IOException 
	     {  
	         this.c_socket = socket;  
	         this.wfIn = socket.getInputStream();
	         this.wfOut = socket.getOutputStream();
	     }  
	  
	     public void run() 
	     {  
	    	 try {     	
	            byte[] buff = new byte[1024]; 
	            clearArray(buff);
	            int n;
	            
	            Database db = new Database();
	            PlatformCoapSend send;
	            
	            while ((n = wfIn.read(buff)) > 0) 
	            {  
	            	String data = new String(buff, 0, n, "UTF-8"); 
	            	
	            	String[] dataArray = new String[3];
	            	StringTokenizer str = new StringTokenizer(data, "/");
	            	int countTokens = str.countTokens();
	            	
	            	for(int i = 0; i < countTokens; i++)
	            	{
	            		dataArray[i] = str.nextToken();
	            	}
	            	
	            	// GET Current Temperature from Server
	            	if(dataArray[0].equals(REQUEST_CURRENT_TEMP_HUM))
	            	{
	            		System.out.println("Client's request : current temp and humidity");
	                    	
	                    String response = "30/24/40/37";//hotzone current temp/coolzone current temp/current humidity/average humidity
	                    System.out.println("Sending response :" + response);
	                    
	                    wfOut.write(response.getBytes("UTF-8"));
	                    wfOut.flush();
	                }
	            	else if(dataArray[0].equals(REQUEST_ACCRUE_TEMP))
	            	{
	            		System.out.println("Client's request : accrue temp");
                    	
	            		// TODO: modifiy  just select from DB
	            		// select
	            		
	            		
	                    String response = "30/29/28/30/31/32/28/30/24/25/26/25/24/25/23/22";
	                    //hotzone temp(-21 hour)/hotzone temp(-16 hour)/.../hotzone temp(-0 hour) : 8 temps
	                    //coolzone temp(-21 hour)/coolzone temp(-16 hour)/.../coolzone temp(-0 hour) : 8 temps
	                    //total : 16 temps
	                    System.out.println("Sending response :" + response);
	                    
	                    wfOut.write(response.getBytes("UTF-8"));
	                    wfOut.flush();
	            	}

	            	//////////////////////////////////////////////////////////////
	            	else if(dataArray[0].equals(REQUEST_STATE))
	            	{
	            		System.out.println("Client's request : state");
	            		
	            		String response = "0/0/0/0/0/0/0/0";
	            		//light : on -> 1, off -> 0/auto on -> 1, auto off -> 0
	            		//hitter : on -> 1, off -> 0/auto on -> 1, auto off -> 0
	            		//humidifier : on -> 1, off -> 0/auto on -> 1, auto off -> 0
	            		//motor : left -> 1, right -> 0/on -> 1, off -> 0
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	                }
	            	else if(dataArray[0].equals(REQUEST_SIGNUP))
	            	{
	            		System.out.println("Client's request : signup");
	            		          		
	            		id = dataArray[1];
	            		password = dataArray[2];
	            		//signup

	            		String response = db.insert(REQUEST_SIGNUP, id + "/" + password);
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	                }
	            	else if(dataArray[0].equals(APP_LIGHT_ON))
	            	{
	            		System.out.println("Client's request : lightOn");
	            		
	            		payload = "APP_LIGHT_ON00000";
	            		send = new PlatformCoapSend(method, uri, payload);
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_LIGHT_OF))
	            	{
	            		System.out.println("Client's request : lightOFF");
	            		
	            		payload = "APP_LIGHT_OF00000";
	            		send = new PlatformCoapSend(method, uri, payload);
	            		
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_LIGHT_AU))
	            	{
	            		System.out.println("Client's request : lightAuto");
	            		
	            		payload = "APP_LIGHT_AU00000";
	            		send = new PlatformCoapSend(method, uri, payload);
	            		
	            		
	            		//if state is 0 in DB, change state to 1
	            		//if state is 1 in DB, change state to 0
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_HITTE_ON))
	            	{
	            		System.out.println("Client's request : hitterOn");
	            		
	            		payload = "APP_HITTE_ON00000";
	            		send = new PlatformCoapSend(method, uri, payload);
	            		
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_HITTE_OF))
	            	{
	            		System.out.println("Client's request : hitterOFF");
	            		
	            		payload = "APP_HITTE_OF00000";
	            		send = new PlatformCoapSend(method, uri, payload);
	            		
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_HITTE_AU))
	            	{
	            		System.out.println("Client's request : hitterAuto");
	            		
	            		payload = "APP_HITTE_AU00000";
	            		send = new PlatformCoapSend(method, uri, payload);
	            		
	            		
	            		//if state is 0 in DB, change state to 1
	            		//if state is 1 in DB, change state to 0
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_HUMID_ON))
	            	{
	            		System.out.println("Client's request : humidifierOn");
	            		
	            		payload = "APP_HUMID_ON00000";
	            		send = new PlatformCoapSend(method, uri, payload);
	            		
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_HUMID_OF))
	            	{
	            		System.out.println("Client's request : humidifierOFF");
	            		
	            		payload = "APP_HUMID_OF00000";
	            		send = new PlatformCoapSend(method, uri, payload);
	            		
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_HUMID_AU))
	            	{
	            		System.out.println("Client's request : humidifierAuto");
	            		
	            		payload = "APP_HUMID_AU00000";
	            		send = new PlatformCoapSend(method, uri, payload);
	            		
	            		
	            		//if state is 0 in DB, change state to 1
	            		//if state is 1 in DB, change state to 0
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_MOTOR_LE))
	            	{
	            		System.out.println("Client's request : humidifierOn");
	            		
	            		
	            		payload = "APP_MOTOR_LE00000";
	            		send = new PlatformCoapSend(method, uri, payload);
	            		
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_MOTOR_RI))
	            	{
	            		System.out.println("Client's request : humidifierOFF");
	            		
	            		payload = "APP_MOTOR_RI00000";
	            		send = new PlatformCoapSend(method, uri, payload);
	            		
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_MOTOR_OF))
	            	{
	            		System.out.println("Client's request : humidifierAuto");
	            		
	            		payload = "APP_MOTOR_OF00000";
	            		send = new PlatformCoapSend(method, uri, payload);
	            		
	            		
	            		//if state is 0 in DB, change state to 1
	            		//if state is 1 in DB, change state to 0
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if (data.equals(COLSE))
					{
	            		break;
					}
	            }
	    	 } catch (Throwable t) {
	    		 System.out.println("Server : Error");
	    		 t.printStackTrace();  
	    	 } finally {  
	    		 close();  
	    	 }
	     }
	        
	     public void close() {  
	    	 System.out.println("Client Close");  
	    	 if (wfIn    != null) try {wfIn.close();} catch (Exception e) {/*ignore*/}  
	    	 if (wfOut   != null) try {wfOut.close();} catch (Exception e) {/*ignore*/}  
	    	 if (c_socket != null) try {c_socket.close();} catch (Exception e) {/*ignore*/} 
	     }  
	        
	     public void clearArray(byte[] buff) {
	    	 for (int i = 0; i < buff.length; i++)
	    	 {
	    		 buff[i] = 0;
	    	 }
	     }
	 }

	 public static void main(String[] args){
			try{
				PlatformCoapServer server = new PlatformCoapServer();
				server.addEndpoints();
				server.start();
				
				// Server에서 라즈베리 패킷을 받는거는 된다,
				// 주는 걸 테스트 해봐야되는데 앱으로 동작시킬 필요가 있다.
				// 앱에 Fix IP를 AWS IP로 바꿔서 올려서 테스트 해야함.
        		
				PlatformMain wifiServer = new PlatformMain();
				while (true) {
					PlatformMain.Session session = wifiServer.accept();  
		            new Thread(session).start(); 
				}
			}
			catch(Exception e){
				System.err.println("Failed to initialize server : " + e.getMessage());
			}
		}
}



