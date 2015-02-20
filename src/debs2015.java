import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintStream;
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
  private BlockingQueue<Q1Elem> queue_q1;
  private BlockingQueue<Q2Elem> queue_q2;
  private String file;

  public IoProcess(BlockingQueue<Q1Elem> queue1,
      BlockingQueue<Q2Elem> queue2, String inputfile) {
    this.queue_q1 = queue1;
    this.queue_q2 = queue2;
    this.file = inputfile;
  }

  @Override
  public void run() {
    try {
      BufferedReader input_file = new BufferedReader(new FileReader(file));
      SimpleDateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
      String line;
      while((line = input_file.readLine()) != null) {
        StringTokenizer st = new StringTokenizer(line, ",");
        Q1Elem q1event = new Q1Elem();
        Q2Elem q2event = new Q2Elem();

        // medallion
        q2event.medallion = st.nextToken();
        // hack license
        q2event.hack_license = st.nextToken();
        // pickup datetime
        Date pdate = datefmt.parse(st.nextToken());
        q1event.pickup_datetime = new java.sql.Timestamp(pdate.getTime());
        q2event.pickup_datetime = new java.sql.Timestamp(pdate.getTime());
        // dropoff datetime
        pdate = datefmt.parse(st.nextToken());
        q1event.dropoff_datetime = new java.sql.Timestamp(pdate.getTime());
        q2event.dropoff_datetime = new java.sql.Timestamp(pdate.getTime());
        // trip time in secs
        st.nextToken();
        // trip distance
        st.nextToken();
        // pickup longitude
        q1event.pickup_longitude = Float.parseFloat(st.nextToken());
        q2event.pickup_longitude = q1event.pickup_longitude;
        // pickup latitude
        q1event.pickup_latitude = Float.parseFloat(st.nextToken());
        q2event.pickup_latitude = q1event.pickup_latitude;
        // dropoff longitude
        q1event.dropoff_longitude = Float.parseFloat(st.nextToken());
        q2event.dropoff_longitude = q1event.dropoff_longitude;
        // dropoff latitude
        q1event.dropoff_latitude = Float.parseFloat(st.nextToken());
        q2event.dropoff_latitude = q1event.dropoff_latitude;
        // payment type
        st.nextToken();
        // fare amount
        q2event.fare_amount = Float.parseFloat(st.nextToken());
        // surcharge
        st.nextToken();
        // mta tax
        st.nextToken();
        // tip amount
        q2event.tip_amount = Float.parseFloat(st.nextToken());
        // tolls amount
        st.nextToken();
        // total amount
        st.nextToken();

        // Put events into queues for Q1 and Q2
        queue_q1.put(q1event);
        queue_q2.put(q2event);
      }

      // Add sentinel
      Q1Elem q1event = new Q1Elem();
      Q2Elem q2event = new Q2Elem();
      q1event.pickup_datetime   = new java.sql.Timestamp(0);
      q1event.dropoff_datetime  = new java.sql.Timestamp(0);
      q1event.pickup_longitude  = 10000000;
      q1event.pickup_latitude   = 0;
      q1event.dropoff_longitude = 0;
      q1event.dropoff_latitude  = 0;
      queue_q1.put(q1event);

      q2event.medallion         = "sentinel";
      q2event.hack_license      = "sentinel";
      q2event.pickup_datetime   = new java.sql.Timestamp(0);
      q2event.dropoff_datetime  = new java.sql.Timestamp(0);
      q2event.pickup_longitude  = 10000000;
      q2event.pickup_latitude   = 0;
      q2event.dropoff_longitude = 0;
      q2event.dropoff_latitude  = 0;
      q2event.fare_amount       = 0;
      q2event.tip_amount        = 0;
      queue_q2.put(q2event);
      input_file.close();
    } catch(Exception e) {
      System.out.println("Error in IoProcess!");
      System.out.println(e.getMessage());
    }
  }
}

/* Q1: Task to perform
 *  *read the data from queue (use take)
 *  *maintain frequency of routes
 *  *output if list of 10 most frequent routes change
 */
class Q1Process implements Runnable {
  private final int WINDOW_CAPACITY = 1000;

  private BlockingQueue<Q1Elem> queue;
  private TenMaxFrequency maxfs;
  private Geo geo;
  private ArrayList<Q1Elem> sliding_window;
  int start, end;
  private PrintStream print_stream;

  public Q1Process(BlockingQueue<Q1Elem> queue, OutputStream print_stream) {
    this.queue = queue;
    this.maxfs = new TenMaxFrequency();
    this.geo = new Geo(-74.913585f, 41.474937f, 500, 500, 300, 300);
    this.sliding_window = new ArrayList<Q1Elem>(WINDOW_CAPACITY);
    this.start = 0;
    this.end = 0;
    this.print_stream = new PrintStream(print_stream);
  }

  @Override
  public void run() {
    try{
      Q1Elem newevent = queue.take();
      boolean ten_max_changed = false;

      while(newevent.pickup_longitude != 10000000) {
        Vector<Route> old_ten_max = maxfs.getMaxTenCopy();

        // Check if events are leaving the sliding window and process them
        long currentms = newevent.dropoff_datetime.getTime();
        if(start != end) {
          Q1Elem lastevent = sliding_window.get(start);
          long lastms = lastevent.dropoff_datetime.getTime();

          // Remove the elements from the start of the window
          while((currentms-lastms) >= 1800000 && start!=end) {
            Area from = geo.translate(lastevent.pickup_longitude,
                lastevent.pickup_latitude);
            Area to = geo.translate(lastevent.dropoff_longitude,
                lastevent.pickup_latitude);

            Route r = new Route(from, to);
            ten_max_changed |= maxfs.update(r, new Freq(-1, lastevent.dropoff_datetime));

            start = (start + 1)%WINDOW_CAPACITY;
            lastevent = sliding_window.get(start);
            lastms = lastevent.dropoff_datetime.getTime();
          }
        }

        // Insert the current element in the sliding window
        Area from = geo.translate(newevent.pickup_longitude,
            newevent.pickup_latitude);
        Area to = geo.translate(newevent.dropoff_longitude,
            newevent.pickup_latitude);
        if(from != null && to != null) {
          Route r = new Route(from, to);
          ten_max_changed |= maxfs.update(r, new Freq(1, newevent.dropoff_datetime));

          try {
            sliding_window.set(end, newevent);
          } catch(IndexOutOfBoundsException e) {
            // Happens if size < number of events in the window
            sliding_window.add(newevent);
          }
          end = (end + 1)%WINDOW_CAPACITY;
        }

        if(ten_max_changed == true) {
          Vector<KeyVal<Route, Freq>> ten_max = maxfs.getMaxTen();

          if(!maxfs.isSameMaxTenKey(old_ten_max)) {
            print_stream.print(newevent.pickup_datetime.toString());
            print_stream.print(",");
            print_stream.print(newevent.dropoff_datetime.toString());
            print_stream.print(",");
            for(int i = 0; i < 10; i++) {
              if(ten_max.get(i) != null) {
                print_stream.print(ten_max.get(i).key.fromArea.x);
                print_stream.print(".");
                print_stream.print(ten_max.get(i).key.fromArea.y);
                print_stream.print(",");
                print_stream.print(ten_max.get(i).key.toArea.x);
                print_stream.print(".");
                print_stream.print(ten_max.get(i).key.toArea.y);
                print_stream.print(",");
              } else{
                print_stream.print("NULL");
                print_stream.print(",");
              }
            }
            print_stream.print("\n");
          }
        }

        // Get the next event to process from the queue
        newevent = queue.take();
        ten_max_changed = false;
      }
    } catch(InterruptedException e) {
      print_stream.println("Error in Q1Process!");
      print_stream.println(e.getMessage());
      e.printStackTrace(print_stream);
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
  private final int WINDOW_CAPACITY = 1000;

  private BlockingQueue<Q2Elem> queue;
  private TenMaxProfitability maxpft;
  private Geo geo;
  private ArrayList<Q2Elem> sliding_window;
  private int end;
  private int start30, start15;
  private PrintStream print_stream;

  public Q2Process(BlockingQueue<Q2Elem> queue2, OutputStream print_stream) {
    this.queue = queue2;
    this.maxpft = new TenMaxProfitability();
    this.geo = new Geo(-74.913585f, 41.474937f, 250, 250, 600, 600);
    this.sliding_window = new ArrayList<Q2Elem>(WINDOW_CAPACITY);
    start30 = -1;
    start15 = -1;
    end = 0;
    this.print_stream = new PrintStream(print_stream);
  }

  public void printTopTen(Q2Elem new_event) {
    Vector<KeyVal<Area, Profitability>> ten_max = maxpft.getMaxTen();
    print_stream.print(new_event.pickup_datetime.toString());
    print_stream.print(",");
    print_stream.print(new_event.dropoff_datetime.toString());
    print_stream.print(",");
    for(int i = 0; i < 10; i++) {
      if(ten_max.get(i) != null) {
        print_stream.print(ten_max.get(i).key.x);
        print_stream.print(".");
        print_stream.print(ten_max.get(i).key.y);
        print_stream.print(",");
        print_stream.print(ten_max.get(i).val.num_empty_taxis);
        print_stream.print(",");
        print_stream.print(ten_max.get(i).val.mprofit.getMedian());
        print_stream.print(",");
        print_stream.print(ten_max.get(i).val.profitability);
        print_stream.print(",");
      } else{
        print_stream.print("NULL");
        print_stream.print(",");
      }
    }
    print_stream.print("\n");
  }

  public void add(Q2Elem new_event) {
    Area dropoff_area = geo.translate(new_event.dropoff_longitude,
        new_event.dropoff_latitude);
    if(dropoff_area != null) {
      maxpft.enterProfitSlidingWindow(dropoff_area,
          new_event.fare_amount+new_event.tip_amount,
          new_event.dropoff_datetime);
      maxpft.enterTaxiSlidingWindow(new_event.medallion,
          new_event.hack_license, dropoff_area, new_event.dropoff_datetime);

      try {
        sliding_window.set(end, new_event);
      } catch (IndexOutOfBoundsException e) {
        // Happens if size < number of events in the window
        sliding_window.add(new_event);
      }
      end = (end + 1)%WINDOW_CAPACITY;
    }
  }

  public void removeFromEmptyTaxis(Q2Elem new_event, long timestamp) {
    Q2Elem empty_taxis_event = sliding_window.get(start30);

    while(empty_taxis_event.dropoff_datetime.getTime() == timestamp) {
      maxpft.leaveTaxiSlidingWindow(empty_taxis_event.medallion,
          empty_taxis_event.hack_license, empty_taxis_event.dropoff_datetime);

      start30 = (start30 + 1)%WINDOW_CAPACITY;
      if(start30 != end) {
        empty_taxis_event = sliding_window.get(start30);
      } else{
        break;
      }
    }
  }

  public void removeFromProfit(Q2Elem new_event, long timestamp) {
    Q2Elem profit_event = sliding_window.get(start15);
    Vector<Area> old_ten_max = maxpft.getMaxTenCopy();

    while(profit_event.dropoff_datetime.getTime() == timestamp) {
      Area dropoff_area = geo.translate(profit_event.dropoff_longitude,
          profit_event.dropoff_latitude);
      maxpft.leaveProfitSlidingWindow(dropoff_area,
          profit_event.fare_amount+profit_event.tip_amount);

      start15 = (start15 + 1)%WINDOW_CAPACITY;
      if(start15 != end) {
        profit_event = sliding_window.get(start15);
      } else{
        break;
      }
    }

    if(!maxpft.isSameMaxTenKey(old_ten_max)) {
      printTopTen(new_event);
    }
  }

  public void removeFromBoth(Q2Elem new_event, long timestamp) {
    Q2Elem empty_taxis_event = sliding_window.get(start30);
    Vector<Area> old_ten_max = maxpft.getMaxTenCopy();
    while(empty_taxis_event.dropoff_datetime.getTime() == timestamp) {
      maxpft.leaveTaxiSlidingWindow(empty_taxis_event.medallion,
          empty_taxis_event.hack_license, empty_taxis_event.dropoff_datetime);

      start30 = (start30 + 1)%WINDOW_CAPACITY;
      if(start30 != end) {
        empty_taxis_event = sliding_window.get(start30);
      } else{
        break;
      }
    }

    Q2Elem profit_event = sliding_window.get(start15);
    while(profit_event.dropoff_datetime.getTime() == timestamp) {
      Area dropoff_area = geo.translate(profit_event.dropoff_longitude,
          profit_event.dropoff_latitude);
      maxpft.leaveProfitSlidingWindow(dropoff_area,
          profit_event.fare_amount+profit_event.tip_amount);

      start15 = (start15 + 1)%WINDOW_CAPACITY;
      if(start15 != end) {
        profit_event = sliding_window.get(start15);
      } else{
        break;
      }
    }

    if(!maxpft.isSameMaxTenKey(old_ten_max)) {
      printTopTen(new_event);
    }
  }

  @Override
  public void run() {
    try {
      Q2Elem newevent = queue.take();

      while(newevent.pickup_longitude != 10000000) {
        //        // Check if events are leaving the sliding window and process them
        //        long currentms = newevent.dropoff_datetime.getTime();
        //
        //        // This means sliding window for 30 minutes is not empty
        //        if(start30 > end) {
        //          Q2Elem etevent = sliding_window.get(start30);
        //          long etms = etevent.dropoff_datetime.getTime() + 30*60*100;
        //
        //          long profitms = currentms;
        //          // This means sliding window for 15 minutes is not empty
        //          if(start15 > end) {
        //            Q2Elem pevent = sliding_window.get(start15);
        //            profitms = pevent.dropoff_datetime.getTime() + 15*60*100;
        //          }
        //
        //          if(etms < currentms && etms < profitms) {
        //            // Remove from empty taxis window
        //            removeFromEmptyTaxis(newevent, etms-30*60*100);
        //          } else if(etms < currentms && profitms < etms) {
        //            // Remove from profit window
        //            removeFromProfit(newevent, profitms - 15*60*100);
        //          } else if(etms < currentms && profitms == etms) {
        //            // Remove from both the windows
        //            removeFromBoth(newevent, profitms - 15*60*100);
        //          } else if(currentms <= profitms && currentms <= etms) {
        //            // Add new event in the window
        //            add(newevent);
        //            //Get the next event to process from the queue
        //            newevent = queue.take();
        //          }
        //        } else{
        //          // No event in the sliding window, just add the incoming event
        //          add(newevent);
        //          //Get te next event to process from the queue
        newevent = queue.take();
        //        }
      }
    } catch(Exception e) {
      print_stream.println("Error in Q2Process!");
      print_stream.println(e.getMessage());
      e.printStackTrace(print_stream);
    }
  }
}

public class debs2015 {
  private final static String TEST_FILE = "test/sorted_data.csv";
  private static final String Q1_FILE = "test/q1_out.csv";
  private static final String Q2_FILE = "test/q2_out.csv";
  private static final int QUEUE_CAPACITY = 1000;

  private static BlockingQueue<Q1Elem> queue_for_Q1;
  private static BlockingQueue<Q2Elem> queue_for_Q2;

  public static void main(String[] args) throws FileNotFoundException {
    PrintStream q1out = new PrintStream(new FileOutputStream(Q1_FILE, true));
    PrintStream q2out = new PrintStream(new FileOutputStream(Q2_FILE, true));

    // Initializing queues
    queue_for_Q1 = new ArrayBlockingQueue<Q1Elem>(QUEUE_CAPACITY, false);
    queue_for_Q2 = new ArrayBlockingQueue<Q2Elem>(QUEUE_CAPACITY, false);

    // start threads
    Thread threadForIoProcess = new Thread(new IoProcess(queue_for_Q1,
        queue_for_Q2, TEST_FILE));
    Thread threadForQ1Process = new Thread(new Q1Process(queue_for_Q1, q1out));
    Thread threadForQ2Process = new Thread(new Q2Process(queue_for_Q2, q2out));

    threadForIoProcess.start();
    threadForQ1Process.start();
    threadForQ2Process.start();
  }
}
