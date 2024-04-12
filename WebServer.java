import java.io.*;
import java.net.*;
import java.util.*;

public final class WebServer {
    public static void main(String argv[]) throws Exception{
        int port = 0;
        String dirName = null;
        

        try{
            port = Integer.parseInt(argv[0]);
            dirName = argv[1];
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Need two arguments 1) port number 2) directory");
            System.exit(-1);
        }
        //establish listen socket
        ServerSocket socket = new ServerSocket(port);
        while (true){
            Socket connection = socket.accept();
            //listen on tcp connection request
            HttpRequest request = new HttpRequest(connection, dirName);

            Thread thread = new Thread(request);

            thread.start();
        }
    }   
}