package participant;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Scanner;
import java.net.UnknownHostException;
import java.io.UncheckedIOException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.io.OutputStream;
import static java.lang.System.out;

class ThreadA implements Runnable {
    String id;
    InetSocketAddress serverAddr;
    InetAddress myAddr;
    boolean registered;
    boolean connected;
    ThreadB threadB;
    ThreadA(){
        id = Main.id;
        String [] addrSplit = Main.coordAddr.split("\\s+");
        try{
            serverAddr =
                new InetSocketAddress(InetAddress.getByName(addrSplit[0]),
                                      Integer.parseInt(addrSplit[1]));
        }catch (UnknownHostException e) {
            out.println("Unknown server host: " + addrSplit[0]);
            System.exit(1);
        }
	try(Socket s=new Socket()){
	    s.connect(serverAddr);
	    // Just in case we are multi-homed or something. We need
	    // to use an address for ThreadB that the server can get
	    // to. That address is the address of a socket that can
	    // get to the server.
	    myAddr=s.getLocalAddress();
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}
    }

    /**
       Send a msg on command socket, wait for ack.
     */
    private void sendMsg(String msg){
	msg=id+" "+msg;
        try (Socket s = new Socket()) {
            s.connect(serverAddr);
            OutputStream os = s.getOutputStream();
            os.write(msg.getBytes());
            os.flush();
        }catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
       Start threadB. Return addr, port string.
     */
    private String startB(String portStr){
        assert threadB == null;
        ServerSocket ss = null;
        try{
            ss = new ServerSocket(Integer.parseInt(portStr),2,myAddr);
            ss.setReuseAddress(true);
            threadB = new ThreadB(ss);
            (new Thread(threadB)).start();
        }catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return ss.getInetAddress().getHostAddress() + " " + ss.getLocalPort();
    }

    /**
       Kill threadB
     */
    private void killB(){
        if (threadB != null) {
            threadB.quit();
            threadB = null;
        }
    }

    @Override
    public void run(){
        Scanner sin = new Scanner(System.in);
	out.print("ta# ");
        while (sin.hasNextLine()) {
            String line = sin.nextLine().trim();
            String [] split = line.split("\\s+");
            switch (split[0].toLowerCase()) {
            case "register":
                if (registered) {
                    out.println("NOP. Already registered.");
                }else{
                    sendMsg("register " + startB(split[1]));
                    connected = true;
                    registered = true;
                }
                break;
            case "deregister":
                if (!registered) {
                    out.println("NOP. Not currently registered.");
                }else{
                    killB();
                    sendMsg("deregister");
                    registered = false;
                    connected = false;
                }
                break;
            case "disconnect":
                if (!connected) {
                    out.println("NOP. Not currently connected");
                }else{
                    killB();
                    sendMsg("disconnect");
                    connected = false;
                }
                break;
            case "reconnect":
                if (connected) {
                    out.println("NOP. Already connected");
                }else if (!registered) {
                    out.println("NOP. Must register before connecting.");
                }else{
                    sendMsg("reconnect " + startB(split[1]));
                    connected = true;
                }
                break;
            case "msend":
                sendMsg(line.trim());
                break;
            default:
                System.out.println("Unknown command: " + split[0]);
            }
	    out.print("ta# ");
        }
    }
}
