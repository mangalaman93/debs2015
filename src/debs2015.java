import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class Q1Elem {
  public String pickup_datatime;
  public String dropoff_datetime;
  public float pickup_longitude;
  public float pickup_latitude;
  public float dropoff_longitude;
  public float dropoff_latitude;

  public Q1Elem(String pickup_datatime, String dropoff_datetime,
      float pickup_longitude, float pickup_latitude,
      float dropoff_longitude, float dropoff_latitude) {
    this.pickup_datatime   = pickup_datatime;
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
  public String pickup_datatime;
  public String dropoff_datetime;
  public float pickup_longitude;
  public float pickup_latitude;
  public float dropoff_longitude;
  public float dropoff_latitude;
  public float fare_amount;
  public float tip_amount;

  public Q2Elem(String medallion, String hack_license,
      String pickup_datatime, String dropoff_datetime,
      float pickup_longitude, float pickup_latitude,
      float dropoff_longitude, float dropoff_latitude,
      float fare_amount, float tip_amount) {
    this.medallion         = medallion;
    this.hack_license      = hack_license;
    this.pickup_datatime   = pickup_datatime;
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
    // TODO
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
    // TODO
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

  public Q2Process(BlockingQueue<Q2Elem> queueForQ2) {
    this.queue = queueForQ2;
  }

  @Override
  public void run() {
    // TODO
  }
}

public class debs2015 {
  private BlockingQueue<Q1Elem> queueForQ1;
  private BlockingQueue<Q2Elem> queueForQ2;

  public void main(String[] args) {
    final int QueueCapacity = 1000;

    // Initializing queues
    queueForQ1 = new ArrayBlockingQueue<Q1Elem>(QueueCapacity);
    queueForQ2 = new ArrayBlockingQueue<Q2Elem>(QueueCapacity);

    // start threads
    new Thread(new IoProcess(queueForQ1, queueForQ2));
    new Thread(new Q1Process(queueForQ1));
    new Thread(new Q2Process(queueForQ2));
  }
}
