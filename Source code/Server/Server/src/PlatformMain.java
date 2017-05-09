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
	 public static final String ServerIP = "192.168.10.102"; 
	 
	 public static final String REQUEST_CURRENT_TEMP_HUM = "1";
	 public static final String REQUEST_ACCRUE_TEMP = "2";
	 public static final String REQUEST_HUM = "3";
	 public static final String REQUEST_CONTROL = "4";
	 public static final String REQUEST_SIGNUP = "5";
	 public static final String COLSE = "6";
	 
	 
	 private static String method = "PUT";
	 private static String uri = "coap://192.168.10.102:5683/Module";
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
	            		db.test();
	            		
	                    String response = "30/29/28/30/31/32/28/30/24/25/26/25/24/25/23/22";
	                    //hotzone temp(-21 hour)/hotzone temp(-16 hour)/.../hotzone temp(-0 hour) : 8 temps
	                    //coolzone temp(-21 hour)/coolzone temp(-16 hour)/.../coolzone temp(-0 hour) : 8 temps
	                    //total : 16 temps
	                    System.out.println("Sending response :" + response);
	                    
	                    wfOut.write(response.getBytes("UTF-8"));
	                    wfOut.flush();
	            	}
	            	else if(dataArray[0].equals(REQUEST_HUM))
	            	{
	            		System.out.println("Client's request : humidity");
                    	
	            		// TODO: modifiy just select from DB
	            		// select
	            		db.test();
	            		
	                    String response = "40/37";
	                    //current humidity/average humidity
	                    System.out.println("Sending response :" + response);
	                    
	                    wfOut.write(response.getBytes("UTF-8"));
	                    wfOut.flush();
	            	}
	            	
	            	
	            	//////////////////////////////////////////////////////////////
	            	else if(dataArray[0].equals(REQUEST_CONTROL))
	            	{
	            		System.out.println("Client's request : control");
	            		
	            		// TODO: modifiy 
	            		// insert
	            		payload = "Control packet";
	            		Request request = createRequest(method, uri, payload);
	            		processRequest(request);
	            		
	            		
	            		String response = "ok";
	            		System.out.println("Sending response :" + response);
	            		wfOut.write(response.getBytes("UTF-8"));  
		                wfOut.flush();
	                }
	            	else if(dataArray[0].equals(REQUEST_SIGNUP))
	            	{
	            		System.out.println("Client's request : signup");
	            		
	            		// TODO: modifiy 
	            		// insert
	            		db.test();
	            		
	            		id = dataArray[1];
	            		password = dataArray[2];
	            		//signup
	            		
	            		String response = "ok";
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
	 
	 
	 public static Request createRequest(String method, String uri, String payload){ 
		 Request ret = null;
		 
		 if(method.equals("GET"))
			 ret = Request.newGet();
		 else if(method.equals("PUT"))
			 ret = Request.newPut();
		 
		 ret.setURI(uri);
		 ret.setPayload(payload);
		 
		 return ret;
	 }
	 
	 public static void processRequest(Request request){
		 
		 String message;
		 
		 try { 
				request.send(); 
			 
			    // receive response 
			    Response response = null; 
			    	
			    try { 
			    	response = request.waitForResponse(); 
			    } catch (InterruptedException e) { 
			    	System.err.println("Failed to receive response: " + e.getMessage()); 
			    } 
			  
			    // output response 
			    if (response != null) { 
			  
			    	System.out.println(Utils.prettyPrint(response));
			    	System.out.println("Time elapsed (ms): " + response.getRTT()); 
			    	
			    	byte[] resp_payload = response.getPayload();
			    	message = new String(resp_payload, "UTF-8");
			    	System.out.println("message :" + message);
			    	
			  
			     // check of response contains resources 
			    	if (response.getOptions().isContentFormat(MediaTypeRegistry.APPLICATION_LINK_FORMAT)) { 
			  
			    		String linkFormat = response.getPayloadString(); 
			  
			    		// output discovered resources 
			    		System.out.println("\nDiscovered resources:"); 
			    		System.out.println(linkFormat); 
			  
			    	} 
			    	else { 
			    	 	// check if link format was expected by client 
			    	 	if (method.equals("DISCOVER")) { 
			    		System.out.println("Server error: Link format not specified"); 
			    	 	} 
			    	} 
			    }
			    else { 
			    	// no response received  
			    	System.err.println("Request timed out"); 
			    } 

			  } catch (Exception e) { 
				  System.err.println("Failed to execute request: " + e.getMessage()); 
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



