import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class MagnusSocketClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 443);
        socket.connect(inetSocketAddress);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("ab".getBytes());
        outputStream.flush();
        socket.close();
    }
}
