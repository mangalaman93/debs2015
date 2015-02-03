import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Date;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;

class Q1Elem {
  public Timestamp pickup_datetime;
  public Timestamp dropoff_datetime;
  public float pickup_longitude;
  public float pickup_latitude;
  public float dropoff_longitude;
  public float dropoff_latitude;

  public Q1Elem() {
    this.pickup_datetime   = null;
    this.dropoff_datetime  = null;
    this.pickup_longitude  = 0;
    this.pickup_latitude   = 0;
    this.dropoff_longitude = 0;
    this.dropoff_latitude  = 0;
  }

  public Q1Elem(Timestamp pickup_datetime, Timestamp dropoff_datetime,
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
  public Timestamp pickup_datetime;
  public Timestamp dropoff_datetime;
  public float pickup_longitude;
  public float pickup_latitude;
  public float dropoff_longitude;
  public float dropoff_latitude;
  public float fare_amount;
  public float tip_amount;

  public Q2Elem() {
    this.medallion         = null;
    this.hack_license      = null;
    this.pickup_datetime   = null;
    this.dropoff_datetime  = null;
    this.pickup_longitude  = 0;
    this.pickup_latitude   = 0;
    this.dropoff_longitude = 0;
    this.dropoff_latitude  = 0;
    this.fare_amount       = 0;
    this.tip_amount        = 0;
  }

  public Q2Elem(String medallion, String hack_license,
      Timestamp pickup_datetime, Timestamp dropoff_datetime,
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
  private static final String TEST_FILE = "../test/test.csv";

  private BlockingQueue<Q1Elem> queueForQ1;
  private BlockingQueue<Q2Elem> queueForQ2;

  public IoProcess(BlockingQueue<Q1Elem> queueForQ1,
      BlockingQueue<Q2Elem> queueForQ2) {
    this.queueForQ1 = queueForQ1;
    this.queueForQ2 = queueForQ2;
  }

  @Override
  public void run() {
    // Read from file
    try {
      BufferedReader in = new BufferedReader(new FileReader(TEST_FILE));
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
      String line;
      while ((line = in.readLine()) != null) {
        StringTokenizer st = new StringTokenizer(line, ",");
        Q1Elem q1Event = new Q1Elem();
        Q2Elem q2Event = new Q2Elem();

        // medallion
        q2Event.medallion = st.nextToken();
        // hack license
        q2Event.hack_license = st.nextToken();
        // pickup datetime
        Date parsedDate = dateFormat.parse(st.nextToken());
        q1Event.pickup_datetime = new java.sql.Timestamp(parsedDate.getTime());
        q2Event.pickup_datetime = new java.sql.Timestamp(parsedDate.getTime());
        // dropoff datetime
        parsedDate = dateFormat.parse(st.nextToken());
        q1Event.dropoff_datetime = new java.sql.Timestamp(parsedDate.getTime());
        q2Event.dropoff_datetime = new java.sql.Timestamp(parsedDate.getTime());
        // trip time in secs
        st.nextToken();
        // trip distance
        st.nextToken();
        // pickup longitude
        q1Event.pickup_longitude = Float.parseFloat(st.nextToken());
        q2Event.pickup_longitude = q1Event.pickup_longitude;
        // pickup latitude
        q1Event.pickup_latitude = Float.parseFloat(st.nextToken());
        q2Event.pickup_latitude = q1Event.pickup_latitude;
        // dropoff longitude
        q1Event.dropoff_longitude = Float.parseFloat(st.nextToken());
        q2Event.dropoff_longitude = q1Event.dropoff_longitude;
        // dropoff latitude
        q1Event.dropoff_longitude = Float.parseFloat(st.nextToken());
        q2Event.dropoff_longitude = q1Event.dropoff_longitude;
        // payment type
        st.nextToken();
        // fare amount
        q2Event.fare_amount = Float.parseFloat(st.nextToken());
        // sur charge
        st.nextToken();
        // mta tax
        st.nextToken();
        // tip amount
        q2Event.tip_amount = Float.parseFloat(st.nextToken());
        // tolls amount
        st.nextToken();
        // total amount
        st.nextToken();

        // Put events into queues for Q1 and Q2
        queueForQ1.put(q1Event);
        queueForQ2.put(q2Event);
      }

      Q1Elem q1Event = new Q1Elem();
      Q2Elem q2Event = new Q2Elem();
      // Add sentinel
      q1Event.pickup_datetime = new java.sql.Timestamp(0);
      q1Event.dropoff_datetime = new java.sql.Timestamp(0);
      q1Event.pickup_longitude = 0;
      q1Event.pickup_latitude = 0;
      q1Event.dropoff_longitude = 0;
      q1Event.dropoff_latitude = 0;
      queueForQ1.put(q1Event);

      q2Event.medallion         = "sentinel";
      q2Event.hack_license      = "sentinel";
      q2Event.pickup_datetime   = new java.sql.Timestamp(0);
      q2Event.dropoff_datetime  = new java.sql.Timestamp(0);
      q2Event.pickup_longitude  = 0;
      q2Event.pickup_latitude   = 0;
      q2Event.dropoff_longitude = 0;
      q2Event.dropoff_latitude  = 0;
      q2Event.fare_amount       = 0;
      q2Event.tip_amount        = 0;
      queueForQ2.put(q2Event);
      in.close();
    }
    catch(Exception e) {
      System.out.println("Error in IoProcess!");
    }
  }
}

/* Q1: Task to perform
 *  *read the data from queue (use take)
 *  *maintain frequency of routes
 *  *output if list of 10 most frequent routes change
 */
class Q1Process implements Runnable {
  private static final String Q1_FILE = "../test/q1_out.csv";

  private BlockingQueue<Q1Elem> queue;

  public Q1Process(BlockingQueue<Q1Elem> queueForQ1) {
    this.queue = queueForQ1;
  }

  @Override
  public void run() {
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(Q1_FILE));

      Q1Elem newEvent = queue.take();
      while(newEvent.pickup_longitude != 0) {
        newEvent = queue.take();
      }
      out.close();
    }
    catch(Exception e) {
      System.out.println("Error in Q1Process!");
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
  private static final String Q2_FILE = "../test/q2_out.csv";

  final int windowCapacity = 1000;
  private BlockingQueue<Q2Elem> queue;
  private TenMaxProfitability profitabilityDataStructure;

  private ArrayList<Q2Elem> slidingWindow;
  private int end;
  private int start;

  public Q2Process(BlockingQueue<Q2Elem> queueForQ2) {
    this.queue = queueForQ2;
    this.profitabilityDataStructure = new TenMaxProfitability();
    this.slidingWindow = new ArrayList<Q2Elem>(windowCapacity);
    start = 0;
    end = 0;
  }

  @Override
  public void run() {
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(Q2_FILE));
      Q2Elem newEvent = queue.take();

      while(newEvent.medallion.equals("sentinel") == false) {
        // Check if events are leaving the sliding window and process them
        long current_milliseconds = newEvent.dropoff_datetime.getTime();
        try {
          Q2Elem last_event = slidingWindow.get(start);
          long last_milliseconds = last_event.dropoff_datetime.getTime();
          while((current_milliseconds - last_milliseconds > 1800000) &&
                (start <= end)) {
            profitabilityDataStructure.remove();
            start = (start + 1)%windowCapacity;
            last_event = slidingWindow.get(start);
            last_milliseconds = last_event.dropoff_datetime.getTime();
          }
        } catch(IndexOutOfBoundsException e) {
          System.out.println("Error in Q2Process!");
        }

        // Add this event to the sliding window and process it
        try {
          slidingWindow.set(end, newEvent);
          profitabilityDataStructure.insert();
        } catch (IndexOutOfBoundsException e) {
          // Happens if size < number of events in the window
          slidingWindow.add(newEvent);
        }
        end = (end + 1)%windowCapacity;

        // Get new event
        newEvent = queue.take();
      }

      out.close();
    } catch(Exception e) {
      System.out.println("Error in Q1Process!");
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
