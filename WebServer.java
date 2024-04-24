import java.io.*;
import java.net.*;
import java.util.*;

public final class WebServer {
    public static void main(String argv[]) throws Exception{
        int port = 0;
        // String dirName = null;
        String dirName = System.getProperty("user.dir");

        try{
            port = Integer.parseInt(argv[0]);
            // dirName = argv[1];
        }catch(ArrayIndexOutOfBoundsException e){
            // System.out.println("Need two arguments 1) port number 2) directory");
            System.out.println("Need argument 1) port number");
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

final class HttpRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;
    String dirName;

    public HttpRequest(Socket socket, String dirName) throws Exception {
        this.socket = socket;
        this.dirName = dirName;
    }

    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String requestLine = br.readLine();

        System.out.println();
        System.out.println(requestLine);

        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }

        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken();
        String fileName = tokens.nextToken();

        fileName = this.dirName + fileName;
        System.out.println(fileName);

        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }

        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;
        if (fileExists) {
            statusLine = "HTTP/1.1 200 OK" + CRLF;
            contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
        } else {
            statusLine = "HTTP/1.1 404 Not Found" + CRLF;
            contentTypeLine = "Content-type: text/html" + CRLF;
            entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY>Not Found</BODY></HTML>";
        }

        os.writeBytes(statusLine);
        os.writeBytes(contentTypeLine);
        os.writeBytes(CRLF);

        if (fileExists) {
            sendBytes(fis, os);
            fis.close();
        } else {
            os.writeBytes(entityBody);
        }

        os.close();
        br.close();
        socket.close();
    }

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes = 0;
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if (fileName.endsWith(".gif")) {
            return "image/gif";
        }
        if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }
}