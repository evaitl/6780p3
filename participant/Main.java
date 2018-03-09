package participant;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
class Main {
    static String id;
    static String logName;
    static String coordAddr;
    public static void main(String [] args){
        if (args.length != 1) {
            System.out.println("Usage: participant fname");
            System.exit(1);
        }
        try (Scanner sin = new Scanner(new File(args[1]))) {
            id = sin.nextLine().trim();
            logName = sin.nextLine().trim();
            coordAddr = sin.nextLine().trim();
        }catch (FileNotFoundException e) {
            System.out.println("Missing config file?:" + e);
            System.exit(1);
        }
        (new Thread(new ThreadA())).start();
    }
}
