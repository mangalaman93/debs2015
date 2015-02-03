import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MyQueue {
    public MyQueue() {
        createQueue();

        Thread write = new Thread() {
            public void run() {
                try {
                    BufferedReader br = new BufferedReader(new FileReader("../sorted_data.csv"));
                    String line;
                    int msg_number=0;
                    while ((line = br.readLine()) != null && msg_number<1999998) {
                        msg_number++;
                        sendMessage(line.substring(0,15));
                    }
                    br.close();
                } catch(Exception v) {
                    System.out.println(v);
                }
            }
        };

        long startTime = System.nanoTime();

        write.start();

        int msg_number=0;
        while(msg_number<1999998) {
            msg_number++;
            receiveMessage();
            //System.out.println("Messages read : " + msg_number);
            //System.out.println(receiveMessage());
        }

        long endTime = System.nanoTime();
        double duration = (double)(endTime - startTime)/1000000000;

        System.out.println("Time taken (in seconds): " + duration);
        System.out.println("Messages read (length 20): " + msg_number);
        System.out.println("Throughput (msg/seconds): " + (msg_number/duration));
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