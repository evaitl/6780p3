package coordinator;
import java.nio.channels.SocketChannel;
import java.net.InetAddress;
import java.net.Socket;

class Client{
    SocketChannel commandChannel;
    String id;
    int lastMsgReceived;
    int dataPort;
    InetAddress dataAddr;
    Socket dataSocket;
    boolean connected;
}
