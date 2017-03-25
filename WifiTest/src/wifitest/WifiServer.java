package wifitest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class WifiServer {
	 public static final int ServerPort = 5000;
	 public static final String ServerIP = "118.41.247.153"; 
	 
	 public static final String REQUEST_DATA = "1";
	 public static final String REQUEST_CONTROL = "2";
	 public static final String COLSE = "3";
	 
	 private ServerSocket serverSocket = null;
	 Socket client = null;
	 
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
	            	
	            	if(data.equals(REQUEST_DATA))
	            	{
	            		System.out.println("Client's request : data");
	                    	
	                    String response = "30";
	                    System.out.println("Sending response :" + response);
	                    
	                    wfOut.write(response.getBytes("UTF-8"));
	                    wfOut.flush();
	                }
	            	else if(data.equals(REQUEST_CONTROL))
	            	{
	            		System.out.println("Client's request : control");
	            		
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

	 public static void main(String[] args) throws IOException 
	 {
		 WifiServer wifiServer = new WifiServer();
			while (true) {
				Session session = wifiServer.accept();  
	            new Thread(session).start(); 
			}
	 }
}



