import java.net.ServerSocket;
import java.net.Socket;

public final class WebServer {

    public static void main(String[] argv) throws Exception {

        int port = 6789;
        ServerSocket SVRSOCK = new ServerSocket(port);

        while(true) {
            Socket SOCKET = SVRSOCK.accept();
            HttpRequest request = new HttpRequest(SOCKET);
            request.run();
            Thread thread = new Thread(request);
            thread.start();
        }
    }
}