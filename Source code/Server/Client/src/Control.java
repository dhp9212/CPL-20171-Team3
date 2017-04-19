import java.io.IOException;
import java.security.GeneralSecurityException;


public class Control {

	private static String method;
	private static String uri;
	private static String payload;
	
	public static void main(String args[]) throws IOException{
		method = "PUT";
		uri = "coap://192.168.10.102/Server";
		payload = "Client에서 간다";
		
		try {
			CoapAccess client = new CoapAccess(method, uri, payload);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}
}
