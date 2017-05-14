package wifiserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class WifiServer {
	 public static final int ServerPort = 5000;
	 public static final String ServerIP = "14.46.3.32"; 
	 
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
	 
	 private ServerSocket serverSocket = null;
	 Socket client = null;
	 
	 static String id;
	 static String password;
	 
	 public WifiServer() throws IOException
	 {
		 System.out.println("Server : Start");
		 serverSocket = new ServerSocket(ServerPort);
	 }
	 
	 public Session accept() throws IOException 
	 {
		 System.out.print("Connecting...");
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
                    	
	                    String response = "30/29/28/30/31/32/28/30/24/25/26/25/24/25/23/22";
	                    //hotzone temp(-21 hour)/hotzone temp(-16 hour)/.../hotzone temp(-0 hour) : 8 temps
	                    //coolzone temp(-21 hour)/coolzone temp(-16 hour)/.../coolzone temp(-0 hour) : 8 temps
	                    //total : 16 temps
	                    System.out.println("Sending response :" + response);
	                    
	                    wfOut.write(response.getBytes("UTF-8"));
	                    wfOut.flush();
	            	}
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
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	                }
	            	else if(dataArray[0].equals(APP_LIGHT_ON))
	            	{
	            		System.out.println("Client's request : lightOn");
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_LIGHT_OF))
	            	{
	            		System.out.println("Client's request : lightOFF");
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_LIGHT_AU))
	            	{
	            		System.out.println("Client's request : lightAuto");
	            		
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
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_HITTE_OF))
	            	{
	            		System.out.println("Client's request : hitterOFF");
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_HITTE_AU))
	            	{
	            		System.out.println("Client's request : hitterAuto");
	            		
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
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_HUMID_OF))
	            	{
	            		System.out.println("Client's request : humidifierOFF");
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_HUMID_AU))
	            	{
	            		System.out.println("Client's request : humidifierAuto");
	            		
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
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_MOTOR_RI))
	            	{
	            		System.out.println("Client's request : humidifierOFF");
	            		
	            		String response = "S";//failed : F
	            		System.out.println("Sending response :" + response);
	            		
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(APP_MOTOR_OF))
	            	{
	            		System.out.println("Client's request : humidifierAuto");
	            		
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

	 public static void main(String[] args) throws IOException 
	 {
		 WifiServer wifiServer = new WifiServer();
			while (true) {
				Session session = wifiServer.accept();  
	            new Thread(session).start(); 
			}
	 }
}



