package participant;
import java.net.ServerSocket;
import java.util.Scanner;
import java.io.PrintStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.io.IOException;
import java.io.FileOutputStream;
class ThreadB implements Runnable, Closeable {
    private boolean killed;
    private ServerSocket ss;
    private Scanner sin;
    private PrintStream ps;
    ThreadB(ServerSocket ss_){
        ss = ss_;
        try{
            ps = new PrintStream(new FileOutputStream(Main.logName,true),true);
        }catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }
    void quit(){
        killed = true;
        // Closing sockets should break out of run loop.
        close();
    }
    @Override
    public void run(){
        try (Scanner sin = new Scanner(ss.accept().getInputStream())) {
            ss.close();
            ss = null;
            while (sin.hasNextLine()) {
                ps.println(sin.nextLine().trim());
            }
        }catch (IOException e) {
            if (!killed) {
                throw new UncheckedIOException(e);
            }
        }
    }
    @Override
    public void close(){
        if (ss != null) {
            try{
                ss.close();
            }catch (IOException e) {}
            ss = null;
        }
        if (sin != null) {
            sin.close();
            sin = null;
        }
        if (ps != null) {
            ps.close();
            ps = null;
        }
    }
}
