import java.io.*;
import java.net.*;
import java.util.*;

final class HttpRequest implements Runnable{
    final static String CRLF = "\r\n";
    Socket socket;
    String dirName;

    public HttpRequest(Socket socket, String dirName) throws Exception{
        this.socket = socket;
        this.dirName = dirName;
    }

    public void run(){
        try{
            processRequest();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception{
        InputStream is = new InputStream();
        DataOutputStream os = new OutputStream();

        BufferedReader br = new BufferedReader();

        String requestLine = ?;

        System.out.println();
        System.out.println(requestLine);

        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0){
            System.out.print(headerLine);
        }
        os.close();
        br.close();
        socket.close();
    }
}