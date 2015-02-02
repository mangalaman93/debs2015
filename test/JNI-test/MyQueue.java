import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MyQueue {
    public MyQueue() {
        createQueue();
        //sendMessage("Hello World !!");
        //System.out.println(receiveMessage());
        //File file = new File("sorted_data.csv");
        try {
            BufferedReader br = new BufferedReader(new FileReader("sorted_data.csv"));
            String line;
            while ((line = br.readLine()) != null) {
               //System.out.println(line);
                sendMessage(line.substring(0,15));
                System.out.println(receiveMessage());
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        MyQueue q = new MyQueue();
    }

    public native void createQueue();
    public native void sendMessage(String msg);
    public native String receiveMessage();

    static {
        System.loadLibrary("MyQueue");
    }
}