package kr.soen.wificlienttest;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;


public class SSSerial implements Serializable{
    Socket socket;
    InputStream mmInStream;
    OutputStream mmOutStream;

    public SSSerial(Socket soc, InputStream in, OutputStream out)
    {
        socket = soc;
        mmInStream = in;
        mmOutStream = out;
    }

    public Socket getSocket()
    {
        return socket;
    }
    public InputStream getInputStream()
    {
        return mmInStream;
    }
    public OutputStream getOutputStream()
    {
        return mmOutStream;
    }

}
