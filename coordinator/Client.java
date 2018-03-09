package coordinator;
import java.nio.channels.SocketChannel;
import java.net.InetAddress;
import java.net.Socket;
import java.io.Closeable;
import java.net.UnknownHostException;
import java.io.IOException;

class Client implements Closeable {
    String id;
    int lastMsgReceived;
    Socket dataSocket;
    boolean registered;
    /**
       On creation, we assume
     */
    Client(String id_){
        id = id_;
        lastMsgReceived = -1;
    }
    void register(String hostName, String port){
        if (dataSocket != null) {
            System.out.println("Warning: registering open client: " + id);
            close();
        }
        try{
            dataSocket = new Socket(InetAddress.getByName(hostName),
                                    Integer.parseInt(port));
            lastMsgReceived = Message.currentMsgNum;
            registered = true;
        }catch (UnknownHostException e) {
            System.out.println("bad host name in client " + id + ": " + hostName);
        }catch (IOException e) {
            System.out.println("Can't connect to client " + id);
        }
    }
    void deregister(){
        close();
        registered = false;
    }
    void disconnect(){
        close();
    }
    void reconnect(String hostName, String port){
        if (dataSocket != null) {
            System.out.println("Warning: reconnect open client: " + id);
            close();
        }
        try{
            dataSocket = new Socket(InetAddress.getByName(hostName), Integer.parseInt(port));
        }catch (UnknownHostException e) {
            System.out.println("bad host name in client " + id + ": " + hostName);
        }catch (IOException e) {
            System.out.println("Can't connect to client " + id);
        }
    }
    @Override
    public void close(){
        if (dataSocket != null) {
            try{
                dataSocket.close();
            }catch (IOException e) {}
            dataSocket = null;
        }
    }
}
