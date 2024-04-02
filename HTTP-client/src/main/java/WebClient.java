import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WebClient {

    public String getWebContentByGet(String urlString, final String charset, int timeout) throws IOException{
        if (urlString == null || urlString.length() == 0) {
            return null;
        }
        urlString = (urlString.startsWith("http://") || urlString.startsWith("https://")) ? urlString : ("http://" + urlString).intern();
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "user/WEBCLIENT");
        conn.setRequestProperty("Accept", "text/html");
        conn.setConnectTimeout(timeout);

        try {
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
        InputStream input = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, charset));
        String line;
        StringBuffer sb = new StringBuffer();
        while((line = reader.readLine()) != null){
            sb.append(line).append("\r\n");
        }
        if(reader != null){
            reader.close();
        }
        if(conn != null){
            conn.disconnect();
        }
        return sb.toString();
    }


    public static void main(String args[]) throws IOException {
        int command;
        String newUrl;
        Scanner scanner = new Scanner(System.in);
        WebClient webClient = new WebClient();

        System.out.print("Select the command (1: GET, 0: Exit): ");
        command = scanner.nextInt();
        if (command == 1) {
            System.out.print("Enter the URL: ");
            newUrl = scanner.next();
            System.out.println(webClient.getWebContentByGet(newUrl, "UTF-8", 10000));
            System.out.println("Done");
        }
        if(command == 0){
            System.exit(0);
        }
    }
}
