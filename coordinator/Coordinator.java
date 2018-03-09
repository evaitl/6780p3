package coordinator;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

class Coordinator implements Runnable{
    int td;
    ServerSocketChannel ssc;
    HashMap<SocketChannel,Client> clientChannelMap;
    HashMap<String,Client> clientIdMap;
    LinkedList<Message> outstandingMsgs;

    
    Coordinator(int port, int td_){
	td=td_;
	try{
	    ssc=ServerSocketChannel.open();
	    ssc.bind(new InetSocketAddress(port));
	    ssc.configureBlocking(false);
	}catch(IOException e){
	    throw new UncheckedIOException(e);
	}
	clientChannelMap=new HashMap<>();
	clientIdMap=new HashMap<>();
	outstandingMsgs=new LinkedList<>();
    }

    @Override
    public void run(){
	while(true){
	    
	}
    }
    
    public static void main(String [] args){
	if(args.length !=1){
	    System.out.println("usage: coordinator <fname>");
	    System.exit(1);
	}
	int port=0;
	int td=0;
	try(Scanner sin=new Scanner(new File(args[0]))){
	    port=sin.nextInt();
	    td=sin.nextInt();
	}catch(IOException e){
	    System.out.println("Something wrong with cfg file?: "+e);
	    System.exit(1);
	}
	(new Thread(new Coordinator(port,td))).start();
    }
}
