import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.StringTokenizer;

final class HttpRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;

    public HttpRequest(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {processRequest();}catch (Exception e) {System.out.println(e);}
    }

    private void processRequest() throws Exception {
        InputStream instream = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        BufferedReader br = new BufferedReader(new InputStreamReader(instream));//reads the input data

        String requestLine = br.readLine();

        System.out.println();
        System.out.println(requestLine);

        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken();
        String fileName = tokens.nextToken();


        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            InputStream inputStream = getClass().getResourceAsStream(fileName);
            Path temp = Files.createTempFile("resource-", ".ext");
            Files.copy(inputStream, temp, StandardCopyOption.REPLACE_EXISTING);
            fis = new FileInputStream(temp.toFile());
        } catch (FileNotFoundException e) {
            fileExists = false;
        }
        fileName = "." + fileName;
        String statusLine;
        String contentTypeLine;
        String entityBody = null;

        if (fileExists) {
            statusLine = "HTTP/1.0 200 OK" + CRLF;
            contentTypeLine = "Content-type: " + contentType( fileName ) + CRLF;
        }
        else {
            statusLine = "HTTP/1.0 404 Not Found" + CRLF;
            contentTypeLine = "Content-type: " + "text/html" + CRLF;
            entityBody = "<HTML>" +
                    "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
                    "<BODY>Not Found</BODY></HTML>";
        }


        os.writeBytes(statusLine);

        os.writeBytes(contentTypeLine);

        os.writeBytes(CRLF);


        if (fileExists) {
            sendBytes(fis, os);
            os.writeBytes(statusLine);
            os.writeBytes(contentTypeLine);
            fis.close();
        } else {
            os.writeBytes(statusLine);
            os.writeBytes(entityBody);
            os.writeBytes(contentTypeLine);
        }


        System.out.println("*****");
        System.out.println(fileName);
        System.out.println("*****");
        String headerLine;
        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }

        os.close();
        br.close();
        socket.close();

    }

    private static String contentType(String fileName) {
        if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {return "text/html";}
        if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {return "image/jpeg";}
        return "application/octet-stream";
    }

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes;

        while((bytes = fis.read(buffer)) != -1 )
        {
            os.write(buffer, 0, bytes);
        }
    }
}
