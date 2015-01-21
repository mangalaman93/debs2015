import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;

class Q1Elem {
  public String pickup_datetime;
  public String dropoff_datetime;
  public float pickup_longitude;
  public float pickup_latitude;
  public float dropoff_longitude;
  public float dropoff_latitude;

  public Q1Elem(){}

  public Q1Elem(String pickup_datetime, String dropoff_datetime,
      float pickup_longitude, float pickup_latitude,
      float dropoff_longitude, float dropoff_latitude) {
    this.pickup_datetime   = pickup_datetime;
    this.dropoff_datetime  = dropoff_datetime;
    this.pickup_longitude  = pickup_longitude;
    this.pickup_latitude   = pickup_latitude;
    this.dropoff_longitude = dropoff_longitude;
    this.dropoff_latitude  = dropoff_latitude;
  }
}

class Q2Elem {
  public String medallion;
  public String hack_license;
  public String pickup_datetime;
  public String dropoff_datetime;
  public float pickup_longitude;
  public float pickup_latitude;
  public float dropoff_longitude;
  public float dropoff_latitude;
  public float fare_amount;
  public float tip_amount;

  public Q2Elem(){}

  public Q2Elem(String medallion, String hack_license,
      String pickup_datetime, String dropoff_datetime,
      float pickup_longitude, float pickup_latitude,
      float dropoff_longitude, float dropoff_latitude,
      float fare_amount, float tip_amount) {
    this.medallion         = medallion;
    this.hack_license      = hack_license;
    this.pickup_datetime   = pickup_datetime;
    this.dropoff_datetime  = dropoff_datetime;
    this.pickup_longitude  = pickup_longitude;
    this.pickup_latitude   = pickup_latitude;
    this.dropoff_longitude = dropoff_longitude;
    this.dropoff_latitude  = dropoff_latitude;
    this.fare_amount       = fare_amount;
    this.tip_amount        = tip_amount;
  }
}

/* IoProcessor: Task to perform-
 *  *read from file
 *  *convert data from string to appropriate type
 *  *send the data to Q1 & Q2 threads (use put)
 *  *create and share kernel queues
 */
class IoProcess implements Runnable {
  private BlockingQueue<Q1Elem> queueForQ1;
  private BlockingQueue<Q2Elem> queueForQ2;

  public IoProcess(BlockingQueue<Q1Elem> queueForQ1,
      BlockingQueue<Q2Elem> queueForQ2) {
    this.queueForQ1 = queueForQ1;
    this.queueForQ2 = queueForQ2;
  }

  @Override
  public void run() {
    //Read from file
    try{
      BufferedReader in = new BufferedReader(new FileReader("../test/test.csv"));

      String line;
      Q1Elem q1Event = new Q1Elem();
      Q2Elem q2Event = new Q2Elem();
      while ((line = in.readLine()) != null){
        StringTokenizer st = new StringTokenizer(line, ",");

        //medallion
        q2Event.medallion = st.nextToken();
        //hack license
        q2Event.hack_license = st.nextToken();
        //pickup datetime
        q1Event.pickup_datetime = st.nextToken();
        q2Event.pickup_datetime = q1Event.pickup_datetime;
        //dropoff datetime
        q1Event.dropoff_datetime = st.nextToken();
        q2Event.dropoff_datetime = q1Event.dropoff_datetime;
        //trip time in secs
        st.nextToken();
        //trip distance
        st.nextToken();
        //pickup longitude
        q1Event.pickup_longitude = Float.parseFloat(st.nextToken());
        q2Event.pickup_longitude = q1Event.pickup_longitude;
        //pickup latitude
        q1Event.pickup_latitude = Float.parseFloat(st.nextToken());
        q2Event.pickup_latitude = q1Event.pickup_latitude;
        //dropoff longitude
        q1Event.dropoff_longitude = Float.parseFloat(st.nextToken());
        q2Event.dropoff_longitude = q1Event.dropoff_longitude;
        //dropoff latitude
        q1Event.dropoff_longitude = Float.parseFloat(st.nextToken());
        q2Event.dropoff_longitude = q1Event.dropoff_longitude;
        //payment type
        st.nextToken();
        //fare amount
        q2Event.fare_amount = Float.parseFloat(st.nextToken());
        //sur charge
        st.nextToken();
        //mta tax
        st.nextToken();
        //tip amount
        q2Event.tip_amount = Float.parseFloat(st.nextToken());
        //tolls amount
        st.nextToken();
        //total amount
        st.nextToken();

        //Put events into queues for Q1 and Q2
        queueForQ1.put(q1Event);
        queueForQ2.put(q2Event);
      }
      in.close();
    }
    catch(Exception e){

    }
    System.out.println("done");
    //TODO create and share kernel queues
  }
}

/* Q1: Task to perform
 *  *read the data from queue (use take)
 *  *maintain frequency of routes
 *  *output if list of 10 most frequent routes change
 */
class Q1Process implements Runnable {
  private BlockingQueue<Q1Elem> queue;

  public Q1Process(BlockingQueue<Q1Elem> queueForQ1) {
    this.queue = queueForQ1;
  }

  @Override
  public void run() {
    try{
      BufferedWriter out = new BufferedWriter(new FileWriter("../test/q1_out.csv"));
      Q1Elem newEvent = queue.take();
      out.write(newEvent.pickup_datetime);
      out.newLine();
      out.close();
    }
    catch(Exception e){

    }
  }
}

/* Q2: Task to perform
 *  *dequeue data from queue (use take)
 *  *maintain profitability (which internally maintains profit
 *    and number of empty taxis
 *  *output 10 most profitable areas when the list change
 */
class Q2Process implements Runnable {
  private BlockingQueue<Q2Elem> queue;
  private TenMaxProfitability profitabilityDataStructure;

  public Q2Process(BlockingQueue<Q2Elem> queueForQ2) {
    this.queue = queueForQ2;
    this.profitabilityDataStructure = new TenMaxProfitability();
  }

  @Override
  public void run() {
    // TODO
    try{
      BufferedWriter out = new BufferedWriter(new FileWriter("../test/q2_out.csv"));
      Q2Elem newEvent = queue.take();
      out.write(newEvent.medallion);
      out.newLine();
      out.close();
    }
    catch(Exception e){

    }

  }
}

public class debs2015 {
  private static BlockingQueue<Q1Elem> queueForQ1;
  private static BlockingQueue<Q2Elem> queueForQ2;

  public static void main(String[] args) {
    final int QueueCapacity = 1000;

    // Initializing queues
    queueForQ1 = new ArrayBlockingQueue<Q1Elem>(QueueCapacity);
    queueForQ2 = new ArrayBlockingQueue<Q2Elem>(QueueCapacity);

    // start threads
    Thread threadForIoProcess = new Thread(new IoProcess(queueForQ1, queueForQ2));
    Thread threadForQ1Process = new Thread(new Q1Process(queueForQ1));
    Thread threadForQ2Process = new Thread(new Q2Process(queueForQ2));

    threadForIoProcess.start();
    threadForQ1Process.start();
    threadForQ2Process.start();
  }
}
