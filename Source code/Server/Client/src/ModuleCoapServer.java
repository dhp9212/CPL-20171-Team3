import static org.eclipse.californium.core.coap.CoAP.ResponseCode.BAD_REQUEST;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;


public class ModuleCoapServer extends CoapServer{
	
	private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);
	
	public void addEndpoints(){
		for(InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()){
			if(addr instanceof Inet4Address || addr.isLoopbackAddress()){
				InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
				addEndpoint(new CoapEndpoint(bindToAddress));
			}
			else if(addr instanceof Inet6Address){
				InetSocketAddress bintToAddress = new InetSocketAddress(addr, COAP_PORT);
				addEndpoint(new CoapEndpoint(bintToAddress));
			}
		}
	}
	
	// constructor
	public ModuleCoapServer() throws SocketException{
		add(new Resource());
	}
	
	class Resource extends CoapResource{
		
		public Resource(){
			//identifier
			super("Module");
			getAttributes().setTitle("CoAP Server");
		}
		
		@Override
		public void handleGET(CoapExchange exchange){
			// when server receive "GET" message
			
			byte[] payload = exchange.getRequestPayload();
			
			try{
				String message = new String(payload, "UTF-8");
				/**/
				System.out.println(message);
				/**/
			}catch(Exception e){
				e.printStackTrace();
			}

			exchange.respond("Success connection");
		}
		
		@Override
		public void handlePUT(CoapExchange exchange){
			// when server receive "PUT" message
			
			byte[] payload = exchange.getRequestPayload();
			
			ModuleMain main = new ModuleMain();
			
			try{
				String message = new String(payload, "UTF-8");
				main.push(message);
				
				/**/
				System.out.println(message);
				/**/
				exchange.respond("OK");
				
			}catch(Exception e){
				e.printStackTrace();
				exchange.respond(BAD_REQUEST, "Invalid Payload");
			}
			
		}
	}
	
}