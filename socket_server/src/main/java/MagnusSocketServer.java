import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MagnusSocketServer {
    public static void main(String[] args) throws IOException {
        int port = 443;
        ServerSocket serverSocket = new ServerSocket(443);
        for(;;) {
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            StringBuilder stringBuilder = new StringBuilder();
            while ((len = inputStream.read(bytes)) != -1) {
                stringBuilder.append(new String(bytes), 0, len);
            }
            System.out.println(stringBuilder.toString());
            inputStream.close();
        }
//        serverSocket.close();
    }
}
