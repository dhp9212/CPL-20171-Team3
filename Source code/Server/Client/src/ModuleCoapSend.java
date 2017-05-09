import java.io.FileInputStream; 
import java.io.FileNotFoundException;
import java.io.IOException; 
import java.io.InputStream; 
import java.net.InetSocketAddress; 
import java.net.URI; 
import java.net.URISyntaxException; 
import java.security.GeneralSecurityException; 
import java.security.KeyStore; 
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey; 
import java.security.cert.Certificate; 
import java.security.cert.CertificateException;
import java.util.logging.Level; 

import org.eclipse.californium.core.CaliforniumLogger; 
import org.eclipse.californium.core.Utils; 
import org.eclipse.californium.core.coap.CoAP; 
import org.eclipse.californium.core.coap.MediaTypeRegistry; 
import org.eclipse.californium.core.coap.Request; 
import org.eclipse.californium.core.coap.Response; 
import org.eclipse.californium.core.network.CoapEndpoint; 
import org.eclipse.californium.core.network.Endpoint; 
import org.eclipse.californium.core.network.EndpointManager; 
import org.eclipse.californium.core.network.config.NetworkConfig; 

public class ModuleCoapSend {

	
	// the trust store file used for DTLS server authentication 
	private static final String TRUST_STORE_LOCATION = "certs/trustStore.jks"; 
	private static final String TRUST_STORE_PASSWORD = "rootPass"; 
	 
	private static final String KEY_STORE_LOCATION = "certs/keyStore.jks"; 
	private static final String KEY_STORE_PASSWORD = "endPass"; 
	  
	// resource URI path used for discovery 
	private static final String DISCOVERY_RESOURCE = "/.well-known/core"; 

	// indices of command line parameters 
	private static final int IDX_METHOD          = 0; 
	private static final int IDX_URI             = 1; 
	private static final int IDX_PAYLOAD         = 2; 

	// exit codes for runtime errors 
	private static final int ERR_MISSING_METHOD  = 1; 
	private static final int ERR_UNKNOWN_METHOD  = 2; 
	private static final int ERR_MISSING_URI     = 3; 
	private static final int ERR_BAD_URI         = 4; 
	private static final int ERR_REQUEST_FAILED  = 5; 
	private static final int ERR_RESPONSE_FAILED = 6; 
	
	private static Endpoint dtlsEndpoint;
	
	 static boolean loop = false; 

	/* method  : GET or PUT
	 * uri     : Uniform Resource Identifier, IP address and port of server
	 * payload : Contents
	 */
	public ModuleCoapSend(String method, String uri, String payload) throws IOException, GeneralSecurityException{
		
		Request request = newRequest(method);
		request.setURI(uri); 	
		request.setPayload(payload); 
		request.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN); 
		
		// for security
		if (request.getScheme().equals(CoAP.COAP_SECURE_URI_SCHEME)) { 
		   // load trust store 
		   KeyStore trustStore = KeyStore.getInstance("JKS"); 
		   InputStream inTrust = new FileInputStream(TRUST_STORE_LOCATION); 
		   trustStore.load(inTrust, TRUST_STORE_PASSWORD.toCharArray()); 
		   // load multiple certificates if needed 
		   Certificate[] trustedCertificates = new Certificate[1]; 
		   trustedCertificates[0] = trustStore.getCertificate("root"); 

		   dtlsEndpoint.start(); 
		   EndpointManager.getEndpointManager().setDefaultSecureEndpoint(dtlsEndpoint); 
		}
		
		processRequest(request);
	}
	
	public void processRequest(Request request){
		
		try{
			request.send();
			do{
				Response response = null;
				
				try{
					response = request.waitForResponse();
				}catch(InterruptedException e){
					System.err.println("Failed to receive response: " + e.getMessage()); 
			    	System.exit(ERR_RESPONSE_FAILED); 
				}
				
				if (response != null) { 
			    	System.out.println(Utils.prettyPrint(response)); 
			    	System.out.println("Time elapsed (ms): " + response.getRTT()); 
			    	
			    	// check of response contains resources 
			    	if (response.getOptions().isContentFormat(MediaTypeRegistry.APPLICATION_LINK_FORMAT)) { 
			      		String linkFormat = response.getPayloadString(); 
			      		// output discovered resources 
			    		System.out.println("\nDiscovered resources:"); 
			    		System.out.println(linkFormat); 
			    	} 
			    }
			    else { 
			    	// no response received  
			    	System.err.println("Request timed out"); 
			    	break; 
			    } 
			}while(loop);
			
		}catch(Exception e){
			
		}
		
	}

	private static Request newRequest(String method) { 
		  if (method.equals("GET")) { 
		   return Request.newGet(); 
		  } else if (method.equals("PUT")) { 
		   return Request.newPut(); 
		  } else if (method.equals("OBSERVE")) { 
			   Request request = Request.newGet(); 
			   request.setObserve(); 
			   loop = true; 
			   return request; 
		  } else { 
			   System.err.println("Unknown method: " + method); 
			   System.exit(ERR_UNKNOWN_METHOD); 
			   return null; 
		  } 
	} 
	
}
