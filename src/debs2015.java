import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
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
  private static final String TEST_FILE = "test/sorted_data.csv";

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
        q1Event.dropoff_latitude = Float.parseFloat(st.nextToken());
        q2Event.dropoff_latitude = q1Event.dropoff_latitude;
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
      System.out.println(e.getMessage());
      System.out.println("Error in IoProcess!");
    }
  }
}

/* Q1: Task to perform
 *  *read the data from queue (use take)
 *  *maintain frequency of routes
 *  *output if list of 10 most frequent routes change
 */
// class Q1Process implements Runnable {
//   private static final String Q1_FILE = "test/q1_out.csv";

//   final int windowCapacity = 1000;
//   private BlockingQueue<Q1Elem> queue;
//   private TenMaxFrequency maxFrequenciesDataStructure;
//   private Geo geoObject;
//   private ArrayList<Q1Elem> slidingWindow;
//   int start, end;

//   public Q1Process(BlockingQueue<Q1Elem> queueForQ1) {
//     this.queue = queueForQ1;
//     this.maxFrequenciesDataStructure = new TenMaxFrequency();
//     this.slidingWindow = new ArrayList<Q1Elem>(windowCapacity);
//     this.start = 0;
//     this.end = 0;
//     this.geoObject = new Geo(-74.913585f, 41.474937f, 500, 500, 300, 300);
//   }

//   @Override
//   public void run() {
//     try {
//       BufferedWriter out = new BufferedWriter(new FileWriter(Q1_FILE));

//       Q1Elem new_event = queue.take();
//       Timestamp last_timestamp = new Timestamp(0);
//       Boolean ten_max_changed = false;
//       while(new_event.pickup_longitude != 10000000) {
//         // Check if events are leaving the sliding window and process them
//         long current_milliseconds = new_event.dropoff_datetime.getTime();
//         Vector<KeyVal<Route, Freq>> old_ten_max = maxFrequenciesDataStructure.getMaxTen();
//         try {
//           Q1Elem last_event = slidingWindow.get(start);
//           long last_milliseconds = last_event.dropoff_datetime.getTime();

//           // Remove the elements from the start of the window
//           while((current_milliseconds - last_milliseconds > 1800000) && (start != end)){
//             if(last_timestamp.equals(last_event.dropoff_datetime) == false){
//               if(ten_max_changed == true){
//                 Vector<KeyVal<Route, Freq>> ten_max = maxFrequenciesDataStructure.getMaxTen();
//                 if(!maxFrequenciesDataStructure.isSameMaxTenKey(old_ten_max)){
//                   System.out.print(new_event.pickup_datetime.toString());
//                   System.out.print(",");
//                   System.out.print(new_event.dropoff_datetime.toString());
//                   System.out.print(",");
//                   for(int i = 0; i < 10; i++){
//                     if(ten_max.get(i) != null){
//                       System.out.print(ten_max.get(i).key.fromArea.x);
//                       System.out.print(".");
//                       System.out.print(ten_max.get(i).key.fromArea.y);
//                       System.out.print(",");
//                       System.out.print(ten_max.get(i).key.toArea.x);
//                       System.out.print(".");
//                       System.out.print(ten_max.get(i).key.toArea.y);
//                     }
//                     else{
//                       System.out.print("NULL");
//                     }
//                   }
//                   old_ten_max = ten_max;
//                   System.out.print("\n");
//                 }
//                 ten_max_changed = false;
//                 last_timestamp = last_event.dropoff_datetime;
//               }
//             }

//             Area from = geoObject.translate(last_event.pickup_longitude, last_event.pickup_latitude);
//             Area to = geoObject.translate(last_event.dropoff_longitude, last_event.pickup_latitude);

//             Route r = new Route(from, to);
//             Timestamp ts = last_event.dropoff_datetime;

//             if(maxFrequenciesDataStructure.update(r, new Freq(-1,ts))){
//               ten_max_changed = true;
//             }
//             start = (start + 1)%windowCapacity;
//             last_event = slidingWindow.get(start);
//             last_milliseconds = last_event.dropoff_datetime.getTime();
//           }
//         }
//         catch(IndexOutOfBoundsException e){
//           // No event at start, sliding window is empty, nothing to do here
//         }

//         // Print for the last event(s) that left the window
//         if(ten_max_changed == true){
//           Vector<KeyVal<Route, Freq>> ten_max = maxFrequenciesDataStructure.getMaxTen();
//           if(!maxFrequenciesDataStructure.isSameMaxTenKey(old_ten_max)){
//             System.out.print(new_event.pickup_datetime.toString());
//             System.out.print(",");
//             System.out.print(new_event.dropoff_datetime.toString());
//             System.out.print(",");
//             for(int i = 0; i < 10; i++){
//               if(ten_max.get(i) != null){
//                 System.out.print(ten_max.get(i).key.fromArea.x);
//                 System.out.print(".");
//                 System.out.print(ten_max.get(i).key.fromArea.y);
//                 System.out.print(",");
//                 System.out.print(ten_max.get(i).key.toArea.x);
//                 System.out.print(".");
//                 System.out.print(ten_max.get(i).key.toArea.y);
//               }
//               else{
//                 System.out.print("NULL");
//               }
//             }
//             old_ten_max = ten_max;
//             System.out.print("\n");
//           }
//           ten_max_changed = false;
//         }

//         // Insert the current element in the sliding window
//         Area from = geoObject.translate(new_event.pickup_longitude, new_event.pickup_latitude);
//         Area to = geoObject.translate(new_event.dropoff_longitude, new_event.pickup_latitude);
//         Route r = new Route(from, to);
//         Timestamp ts = new_event.dropoff_datetime;
//         ten_max_changed = false;

//         if(maxFrequenciesDataStructure.update(r, new Freq(1,ts))){
//           ten_max_changed = true;
//         }

//         if(ten_max_changed == true){
//           Vector<KeyVal<Route, Freq>> ten_max = maxFrequenciesDataStructure.getMaxTen();
//           if(!maxFrequenciesDataStructure.isSameMaxTenKey(old_ten_max)){
//             System.out.print(new_event.pickup_datetime.toString());
//             System.out.print(",");
//             System.out.print(new_event.dropoff_datetime.toString());
//             System.out.print(",");
//             for(int i = 0; i < 10; i++){
//               if(ten_max.get(i) != null){
//                 System.out.print(ten_max.get(i).key.fromArea.x);
//                 System.out.print(".");
//                 System.out.print(ten_max.get(i).key.fromArea.y);
//                 System.out.print(",");
//                 System.out.print(ten_max.get(i).key.toArea.x);
//                 System.out.print(".");
//                 System.out.print(ten_max.get(i).key.toArea.y);
//                 System.out.print(",");
//               }
//               else{
//                 System.out.print("NULL");
//                 System.out.print(",");
//               }
//             }
//             System.out.print("\n");
//           }
//           old_ten_max = ten_max;
//           ten_max_changed = false;
//         }

//         try {
//           slidingWindow.set(end, new_event);
//         } catch (IndexOutOfBoundsException e) {
//           // Happens if size < number of events in the window
//           slidingWindow.add(new_event);
//         }
//         end = (end + 1)%windowCapacity;
//         //Get the next event to process from the queue
//         new_event = queue.take();
//       }
//       out.close();
//     }
//     catch(Exception e) {
//       System.out.println(e.getMessage());
//       System.out.println("Error in Q1Process!");
//     }
//   }
// }

/* Q2: Task to perform
 *  *dequeue data from queue (use take)
 *  *maintain profitability (which internally maintains profit
 *    and number of empty taxis
 *  *output 10 most profitable areas when the list change
 */

class Q2Process implements Runnable {
  private static final String Q2_FILE = "test/q2_out.csv";

  final int windowCapacity = 1000;
  private BlockingQueue<Q2Elem> queue;
  private TenMaxProfitability profitabilityDataStructure;
  private Geo geoObject;
  private ArrayList<Q2Elem> slidingWindow;
  private int end;
  private int start_30, start_15;

  public Q2Process(BlockingQueue<Q2Elem> queueForQ2) {
    this.queue = queueForQ2;
    this.profitabilityDataStructure = new TenMaxProfitability();
    this.slidingWindow = new ArrayList<Q2Elem>(windowCapacity);
    start_30 = 0;
    start_15 = 0;
    end = 0;
    this.geoObject = new Geo(-74.913585f, 41.474937f, 250, 250, 600, 600);
  }

  public void printTopTen(Q2Elem new_event){
    Vector<KeyVal<Area, Profitability>> ten_max = profitabilityDataStructure.getMaxTen();
    System.out.print(new_event.pickup_datetime.toString());
    System.out.print(",");
    System.out.print(new_event.dropoff_datetime.toString());
    for(int i = 0; i < 10; i++){
      if(ten_max.get(i) != null){
        System.out.print(ten_max.get(i).key.x);
        System.out.print(".");
        System.out.print(ten_max.get(i).key.y);
        System.out.print(",");
        System.out.print(ten_max.get(i).val.num_empty_taxis);
        System.out.print(",");
        System.out.print(ten_max.get(i).val.mprofit.getMedian());
        System.out.print(",");
        System.out.print(ten_max.get(i).val.profitability);
        System.out.print(",");
      }
      else{
        System.out.print("NULL");
        System.out.print(",");
      }
    }
    System.out.print("\n");
  }

  public void add(Q2Elem new_event){
    //Get initial ten max values
    Vector<KeyVal<Area, Profitability>> old_ten_max = profitabilityDataStructure.getMaxTen();

    Area dropoff_area = geoObject.translate(new_event.dropoff_longitude, new_event.dropoff_latitude);
    boolean profit_changed = profitabilityDataStructure.enterProfitSlidingWindow(dropoff_area, new_event.fare_amount + new_event.tip_amount, new_event.dropoff_datetime);
    boolean empty_taxis_changed = profitabilityDataStructure.enterTaxiSlidingWindow(new_event.medallion, new_event.hack_license, dropoff_area, new_event.dropoff_datetime);

    if(profit_changed || empty_taxis_changed){
      if(!profitabilityDataStructure.isSameMaxTenKey(old_ten_max)){
        printTopTen(new_event);
      }
    }

    try {
      slidingWindow.set(end, new_event);
    } catch (IndexOutOfBoundsException e) {
      // Happens if size < number of events in the window
      slidingWindow.add(new_event);
    }
    end = (end + 1)%windowCapacity;
  }

  public void removeFromEmptyTaxis(Q2Elem new_event, long timestamp){
    Boolean ten_max_changed = false;
    Q2Elem empty_taxis_event = slidingWindow.get(start_30);
    Vector<KeyVal<Area, Profitability>> old_ten_max = profitabilityDataStructure.getMaxTen();
    while(empty_taxis_event.dropoff_datetime.getTime() == timestamp){
      if(profitabilityDataStructure.leaveTaxiSlidingWindow(empty_taxis_event.medallion, empty_taxis_event.hack_license, empty_taxis_event.dropoff_datetime)){
        ten_max_changed = true;
      }
      start_30 = (start_30 + 1)%windowCapacity;
      if(start_30 != end){
        empty_taxis_event = slidingWindow.get(start_30);
      }
      else{
        break;
      }
    }
    if(ten_max_changed){
      if(!profitabilityDataStructure.isSameMaxTenKey(old_ten_max)){
        printTopTen(new_event);
      }
    }
  }

  public void removeFromProfit(Q2Elem new_event, long timestamp){
    Boolean ten_max_changed = false;
    Q2Elem profit_event = slidingWindow.get(start_15);
    Vector<KeyVal<Area, Profitability>> old_ten_max = profitabilityDataStructure.getMaxTen();
    while(profit_event.dropoff_datetime.getTime() == timestamp){
      Area dropoff_area = geoObject.translate(profit_event.dropoff_longitude, profit_event.dropoff_latitude);
      if(profitabilityDataStructure.leaveProfitSlidingWindow(dropoff_area, profit_event.fare_amount + profit_event.tip_amount)){
        ten_max_changed = true;
      }
      start_15 = (start_15 + 1)%windowCapacity;
      if(start_15 != end){
        profit_event = slidingWindow.get(start_15);
      }
      else{
        break;
      }
    }
    if(ten_max_changed){
      if(!profitabilityDataStructure.isSameMaxTenKey(old_ten_max)){
        printTopTen(new_event);
      }
    }
  }

  public void removeFromBoth(Q2Elem new_event, long timestamp){
    Boolean ten_max_changed = false;
    Q2Elem empty_taxis_event = slidingWindow.get(start_30);
    Vector<KeyVal<Area, Profitability>> old_ten_max = profitabilityDataStructure.getMaxTen();
    while(empty_taxis_event.dropoff_datetime.getTime() == timestamp){
      if(profitabilityDataStructure.leaveTaxiSlidingWindow(empty_taxis_event.medallion, empty_taxis_event.hack_license, empty_taxis_event.dropoff_datetime)){
        ten_max_changed = true;
      }
      start_30 = (start_30 + 1)%windowCapacity;
      if(start_30 != end){
        empty_taxis_event = slidingWindow.get(start_30);
      }
      else{
        break;
      }
    }

    Q2Elem profit_event = slidingWindow.get(start_15);
    while(profit_event.dropoff_datetime.getTime() == timestamp){
      Area dropoff_area = geoObject.translate(profit_event.dropoff_longitude, profit_event.dropoff_latitude);
      if(profitabilityDataStructure.leaveProfitSlidingWindow(dropoff_area, profit_event.fare_amount + profit_event.tip_amount)){
        ten_max_changed = true;
      }
      start_15 = (start_15 + 1)%windowCapacity;
      if(start_15 != end){
        profit_event = slidingWindow.get(start_15);
      }
      else{
        break;
      }
    }

    if(ten_max_changed){
      if(!profitabilityDataStructure.isSameMaxTenKey(old_ten_max)){
        printTopTen(new_event);
      }
    }
  }

  @Override
  public void run() {
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(Q2_FILE));
      Q2Elem new_event = queue.take();

      while(new_event.medallion.equals("sentinel") == false) {
        // Check if events are leaving the sliding window and process them
        long current_milliseconds = new_event.dropoff_datetime.getTime();
        if(start_30 != end){ //This means sliding window for 30 minutes is not empty
          Q2Elem empty_taxis_event = slidingWindow.get(start_30);
          long empty_taxis_milliseconds = empty_taxis_event.dropoff_datetime.getTime() + 30*60*100;
          long profit_milliseconds = current_milliseconds;
          if(start_15 != end){  //This means sliding window for 15 minutes is not empty
            Q2Elem profit_event = slidingWindow.get(start_15);
            profit_milliseconds = profit_event.dropoff_datetime.getTime() + 15*60*100;
          }

          if(empty_taxis_milliseconds < current_milliseconds && empty_taxis_milliseconds < profit_milliseconds){
            // Remove from empty taxis window
            removeFromEmptyTaxis(new_event, empty_taxis_milliseconds - 30*60*100);
          }
          else if(empty_taxis_milliseconds < current_milliseconds && profit_milliseconds < empty_taxis_milliseconds){
            // Remove from profit window
            removeFromProfit(new_event, profit_milliseconds - 15*60*100);
          }
          else if(empty_taxis_milliseconds < current_milliseconds && profit_milliseconds == empty_taxis_milliseconds){
            // Remove from both the windows
            removeFromBoth(new_event, profit_milliseconds - 15*60*100);
          }
          else if(current_milliseconds <= profit_milliseconds && current_milliseconds <= empty_taxis_milliseconds){
            // Add new event in the window
            add(new_event);
            //Get the next event to process from the queue
            new_event = queue.take();
          }
        }
        else{
          // No event in the sliding window, just add the incoming event
          add(new_event);
          //Get the next event to process from the queue
          new_event = queue.take();
        }
      }

      out.close();
    } catch(Exception e) {
      System.out.println(e.getMessage());
      System.out.println("Error in Q2Process!");
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
    //Thread threadForQ1Process = new Thread(new Q1Process(queueForQ1));
    Thread threadForQ2Process = new Thread(new Q2Process(queueForQ2));

    threadForIoProcess.start();
    //threadForQ1Process.start();
    threadForQ2Process.start();
  }
}
