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
import java.nio.channels.Selector;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.util.Set;
import java.util.Iterator;

class Coordinator implements Runnable {
    int td;
    Selector selector;
    HashMap<String, Client> clientIdMap;
    LinkedList<Message> outstandingMsgs;
    void addClientChannel(ServerSocketChannel ssc){
    }
    /**
        Parse, ack, disconnect, process command. Which means, one
        message per client connection. I'll make the (wrong, but
        likely true) assumption that the whole message is less than 1K
        and is available on the first read(). Good enough for
        homework.

        I'm making the assumption that we won't have two clients with
        the same ID show up. Bad Things will happen if that rule is
        broken.
     */
    void processCommand(SocketChannel sc){
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        try{
            sc.read(buffer);
            sc.write(ByteBuffer.wrap("ACK\n".getBytes()));
            sc.close();
        }catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        String cmd = new String(buffer.array()).trim();
        String [] split = cmd.split("\\s+");
        String id = split[0];
        Client c = clientIdMap.get(id);
        if (c == null) {
            c = new Client(id);
            clientIdMap.put(id, new Client(id));
        }
        switch (split[1]) {
        case "register":
            c.register(split[2], split[3]);
            break;
        case "deregister":
            c.deregister();
            clientIdMap.remove(id);
            break;
        case "disconnect":
            c.disconnect();
            break;
        case "reconnect":
            c.reconnect(split[2], split[3]);
            break;
        case "msend":
            addMessage(String.join(" ", Arrays.copyOfRange(split, 2, split.length)));
            break;
        default:
            System.out.println("Unknown command " + split[1] + " from " + id);
        }
        broadcastMessages();
    }
    Coordinator(int port, int td_){
        td = td_;
        try{
            selector = Selector.open();
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(port));
            ssc.configureBlocking(false);
            ssc.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            ssc.register(selector, SelectionKey.OP_ACCEPT, null);
        }catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        clientIdMap = new HashMap<>();
        outstandingMsgs = new LinkedList<>();
    }
    @Override
    public void run(){
        try{
            while (true) {
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext()) {
                    SelectionKey ky = iter.next();
                    if (ky.isAcceptable()) {
                        SocketChannel sc = ((ServerSocketChannel)ky.channel()).accept();
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);
                    }else if (ky.isReadable()) {
                        processCommand((SocketChannel)ky.channel());
                    }
                    iter.remove();
                }
            }
        }catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    void addMessage(String msg){
        outstandingMsgs.addLast(new Message(msg));
    }
    void pruneMessages(){
        long currentTime = System.currentTimeMillis() / 1000;

        while (!outstandingMsgs.isEmpty()) {
            Message msg = outstandingMsgs.getFirst();
            if (msg.timeReceived - currentTime > td) {
                outstandingMsgs.removeFirst();
            }else{
                break;
            }
        }
    }
    void broadcastMessages(){
        pruneMessages();
        for (Client c: clientIdMap.values()) {
            if (c.dataSocket == null || c.registered == false) {
                continue;
            }
            if (c.lastMsgReceived >= Message.currentMsgNum) {
                continue;
            }
            for (Message m: outstandingMsgs) {
                if (m.msgNum > c.lastMsgReceived) {
                    try{
                        c.dataSocket.getOutputStream().write((m.msg + "\n").getBytes());
                        c.lastMsgReceived = m.msgNum;
                    }catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            }
        }
    }
    public static void main(String [] args){
        if (args.length != 1) {
            System.out.println("usage: coordinator <fname>");
            System.exit(1);
        }
        int port = 0;
        int td = 0;
        try (Scanner sin = new Scanner(new File(args[0]))) {
            port = sin.nextInt();
            td = sin.nextInt();
        }catch (IOException e) {
            System.out.println("Something wrong with cfg file?: " + e);
            System.exit(1);
        }
        (new Thread(new Coordinator(port, td))).start();
    }
}
