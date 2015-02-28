import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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
  public long time_in;

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
  public long time_in;

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
  private BufferedReader inputstream;

  public IoProcess(BlockingQueue<Q1Elem> queue1,
      BlockingQueue<Q2Elem> queue2, BufferedReader stream) {
    this.queue_q1 = queue1;
    this.queue_q2 = queue2;
    this.inputstream = stream;
  }

  @Override
  public void run() {
    try {
      SimpleDateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
      String line;
      while((line = inputstream.readLine()) != null) {
        try {
          StringTokenizer st = new StringTokenizer(line, ",");
          Q1Elem q1event = new Q1Elem();
          Q2Elem q2event = new Q2Elem();
          q1event.time_in = System.currentTimeMillis();
          q2event.time_in = System.currentTimeMillis();
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
        } catch(Exception e) {
          System.out.println("Error in parsing. Skipping..." + line);
          System.out.println(e.getMessage());
          e.printStackTrace();
        }
      }

      // Add sentinel
      Q1Elem q1event = new Q1Elem();
      Q2Elem q2event = new Q2Elem();
      q1event.pickup_datetime   = new java.sql.Timestamp(0);
      q1event.dropoff_datetime  = new java.sql.Timestamp(0);
      q1event.pickup_longitude  = -100;
      q1event.pickup_latitude   = 0;
      q1event.dropoff_longitude = 0;
      q1event.dropoff_latitude  = 0;
      queue_q1.put(q1event);

      q2event.medallion         = "sentinel";
      q2event.hack_license      = "sentinel";
      q2event.pickup_datetime   = new java.sql.Timestamp(0);
      q2event.dropoff_datetime  = new java.sql.Timestamp(0);
      q2event.pickup_longitude  = -100;
      q2event.pickup_latitude   = 0;
      q2event.dropoff_longitude = 0;
      q2event.dropoff_latitude  = 0;
      q2event.fare_amount       = 0;
      q2event.tip_amount        = 0;
      queue_q2.put(q2event);
      inputstream.close();
    } catch(Exception e) {
      System.out.println("Error in IoProcess!");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}

/* IoProcessor for query 1: Task to perform-
 *  *read from file
 *  *convert data from string to appropriate type
 *  *send the data to Q1 thread (use put)
 *  *create and share kernel queues
 */
class IoProcessQ1 implements Runnable {
  private BlockingQueue<Q1Elem> queue_q1;
  private String file;

  public IoProcessQ1(BlockingQueue<Q1Elem> queue1, String inputfile) {
    this.queue_q1 = queue1;
    this.file = inputfile;
  }

  @Override
  public void run() {
    try {
      BufferedReader input_file = new BufferedReader(new FileReader(file));
      SimpleDateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
      String line;
      while((line = input_file.readLine()) != null) {
        try {
          StringTokenizer st = new StringTokenizer(line, ",");
          Q1Elem q1event = new Q1Elem();
          q1event.time_in = System.currentTimeMillis();
          // medallion
          st.nextToken();
          // hack license
          st.nextToken();
          // pickup datetime
          Date pdate = datefmt.parse(st.nextToken());
          q1event.pickup_datetime = new java.sql.Timestamp(pdate.getTime());
          // dropoff datetime
          pdate = datefmt.parse(st.nextToken());
          q1event.dropoff_datetime = new java.sql.Timestamp(pdate.getTime());
          // trip time in secs
          st.nextToken();
          // trip distance
          st.nextToken();
          // pickup longitude
          q1event.pickup_longitude = Float.parseFloat(st.nextToken());
          // pickup latitude
          q1event.pickup_latitude = Float.parseFloat(st.nextToken());
          // dropoff longitude
          q1event.dropoff_longitude = Float.parseFloat(st.nextToken());
          // dropoff latitude
          q1event.dropoff_latitude = Float.parseFloat(st.nextToken());
          // payment type
          // fare amount
          // surcharge
          // mta tax
          // tip amount
          // tolls amount
          // total amount

          // Put events into queues for Q1
          queue_q1.put(q1event);
        } catch(Exception e) {
          System.out.println("Error parsing for query 1. Skipping..." + line);
          System.out.println(e.getMessage());
          e.printStackTrace();
        }
      }

      // Add sentinel
      Q1Elem q1event = new Q1Elem();
      q1event.pickup_datetime   = new java.sql.Timestamp(0);
      q1event.dropoff_datetime  = new java.sql.Timestamp(0);
      q1event.pickup_longitude  = -100;
      q1event.pickup_latitude   = 0;
      q1event.dropoff_longitude = 0;
      q1event.dropoff_latitude  = 0;
      queue_q1.put(q1event);
      input_file.close();
    } catch(Exception e) {
      System.out.println("Error in IoProcess!");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}

/* IoProcessor for query 2: Task to perform-
 *  *read from file
 *  *convert data from string to appropriate type
 *  *send the data to Q2 thread (use put)
 *  *create and share kernel queues
 */
class IoProcessQ2 implements Runnable {
  private BlockingQueue<Q2Elem> queue_q2;
  private String file;

  public IoProcessQ2(BlockingQueue<Q2Elem> queue2, String inputfile) {
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
        try {
          StringTokenizer st = new StringTokenizer(line, ",");
          Q2Elem q2event = new Q2Elem();
          q2event.time_in = System.currentTimeMillis();
          // medallion
          q2event.medallion = st.nextToken();
          // hack license
          q2event.hack_license = st.nextToken();
          // pickup datetime
          Date pdate = datefmt.parse(st.nextToken());
          q2event.pickup_datetime = new java.sql.Timestamp(pdate.getTime());
          // dropoff datetime
          pdate = datefmt.parse(st.nextToken());
          q2event.dropoff_datetime = new java.sql.Timestamp(pdate.getTime());
          // trip time in secs
          st.nextToken();
          // trip distance
          st.nextToken();
          // pickup longitude
          q2event.pickup_longitude = Float.parseFloat(st.nextToken());
          // pickup latitude
          q2event.pickup_latitude = Float.parseFloat(st.nextToken());
          // dropoff longitude
          q2event.dropoff_longitude = Float.parseFloat(st.nextToken());
          // dropoff latitude
          q2event.dropoff_latitude = Float.parseFloat(st.nextToken());
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
          // total amount

          // Put events into queues for Q2
          queue_q2.put(q2event);
        } catch(Exception e) {
          System.out.println("Error parsing for query 2. Skipping..." + line);
          System.out.println(e.getMessage());
          e.printStackTrace();
        }
      }

      // Add sentinel
      Q2Elem q2event = new Q2Elem();
      q2event.medallion         = "sentinel";
      q2event.hack_license      = "sentinel";
      q2event.pickup_datetime   = new java.sql.Timestamp(0);
      q2event.dropoff_datetime  = new java.sql.Timestamp(0);
      q2event.pickup_longitude  = -100;
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
      e.printStackTrace();
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

      while(newevent.pickup_longitude != -100) {
        Vector<Route> old_ten_max = maxfs.getMaxTenCopy();

        // Check if events are leaving the sliding window and process them
        long currentms = newevent.dropoff_datetime.getTime();
        if(start != end) {
          Q1Elem lastevent = sliding_window.get(start);
          long lastms = lastevent.dropoff_datetime.getTime();

          // Remove the elements from the start of the window
          while((currentms-lastms) >= 1800000) {
            Area from = geo.translate(lastevent.pickup_longitude,
                lastevent.pickup_latitude);
            Area to = geo.translate(lastevent.dropoff_longitude,
                lastevent.dropoff_latitude);

            Route r = new Route(from, to);
            ten_max_changed |= maxfs.update(r, new Freq(-1,
                lastevent.dropoff_datetime));

            start = (start + 1)%WINDOW_CAPACITY;
            if(start != end) {
              lastevent = sliding_window.get(start);
              lastms = lastevent.dropoff_datetime.getTime();
            } else {
              break;
            }
          }
        }

        // Insert the current element in the sliding window
        Area from = geo.translate(newevent.pickup_longitude,
            newevent.pickup_latitude);
        Area to = geo.translate(newevent.dropoff_longitude,
            newevent.dropoff_latitude);
        if(from != null && to != null) {
          Route r = new Route(from, to);
          ten_max_changed |= maxfs.update(r, new Freq(1,
              newevent.dropoff_datetime));

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
                print_stream.print(ten_max.get(i).key.fromArea.x + 1);
                print_stream.print(".");
                print_stream.print(ten_max.get(i).key.fromArea.y + 1);
                print_stream.print(",");
                print_stream.print(ten_max.get(i).key.toArea.x + 1);
                print_stream.print(".");
                print_stream.print(ten_max.get(i).key.toArea.y + 1);
                print_stream.print(",");
              } else{
                print_stream.print("NULL");
                print_stream.print(",");
              }
            }
            long time_out = System.currentTimeMillis();
            print_stream.print(time_out - newevent.time_in);
            print_stream.print("\n");
          }
        }

        // Get the next event to process from the queue
        newevent = queue.take();
        ten_max_changed = false;
      }
    } catch(InterruptedException e) {
      System.out.println("Error in Q1Process!");
      System.out.println(e.getMessage());
      e.printStackTrace();
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
    start30 = 0;
    start15 = 0;
    end = 0;
    this.print_stream = new PrintStream(print_stream);
  }

  @Override
  public void run() {
    try {
      Q2Elem newevent = queue.take();
      while(newevent.pickup_longitude != -100) {
        Vector<Area> old_ten_max = maxpft.getMaxTenCopy();

        // Check if events are leaving the sliding window and process them
        long currentms = newevent.dropoff_datetime.getTime();
        if(start30 != end) {
          Q2Elem event = sliding_window.get(start30);
          long lastms = event.dropoff_datetime.getTime();

          while((currentms-lastms) >= 30*60*1000) {
            maxpft.leaveTaxiSlidingWindow(event.medallion,
                event.hack_license, event.dropoff_datetime);

            start30 = (start30 + 1)%WINDOW_CAPACITY;
            if(start30 != end) {
              event = sliding_window.get(start30);
              lastms = event.dropoff_datetime.getTime();
            } else {
              break;
            }
          }

          // This means sliding window for 15 minutes is not empty
          if(start15 != end) {
            event = sliding_window.get(start15);
            lastms = event.dropoff_datetime.getTime();

            while((currentms-lastms) >= 15*60*1000) {
              Area pickup_area = geo.translate(event.pickup_longitude,
                  event.pickup_latitude);
              maxpft.leaveProfitSlidingWindow(pickup_area,
                  event.fare_amount+event.tip_amount);

              start15 = (start15 + 1)%WINDOW_CAPACITY;
              if(start15 != end) {
                event = sliding_window.get(start15);
                lastms = event.dropoff_datetime.getTime();
              } else {
                break;
              }
            }
          }
        }

        // add the incoming event
        Area dropoff_area = geo.translate(newevent.dropoff_longitude,
            newevent.dropoff_latitude);
        Area pickup_area = geo.translate(newevent.pickup_longitude,
        		newevent.pickup_latitude);
        if(dropoff_area != null) {
          maxpft.enterProfitSlidingWindow(pickup_area,
              newevent.fare_amount+newevent.tip_amount,
              newevent.dropoff_datetime);
          maxpft.enterTaxiSlidingWindow(newevent.medallion,
              newevent.hack_license, dropoff_area, newevent.dropoff_datetime);

          try {
            sliding_window.set(end, newevent);
          } catch (IndexOutOfBoundsException e) {
            // Happens if size < number of events in the window
            sliding_window.add(newevent);
          }
          end = (end + 1)%WINDOW_CAPACITY;
        }

        if(!maxpft.isSameMaxTenKey(old_ten_max)) {
          Vector<KeyVal<Area, Profitability>> ten_max = maxpft.getMaxTen();
          print_stream.print(newevent.pickup_datetime.toString());
          print_stream.print(",");
          print_stream.print(newevent.dropoff_datetime.toString());
          print_stream.print(",");
          for(int i = 0; i < 10; i++) {
            if(ten_max.get(i) != null) {
              print_stream.print(ten_max.get(i).key.x + 1);
              print_stream.print(".");
              print_stream.print(ten_max.get(i).key.y + 1);
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
          long time_out = System.currentTimeMillis();
          print_stream.print(time_out - newevent.time_in);
          print_stream.print("\n");
        }

        //Get the next event to process from the queue
        newevent = queue.take();
      }
    } catch(Exception e) {
      System.out.println("Error in Q2Process!");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}

public class debs2015 {
  private static final boolean TWO_IO_PROCESS = true;
  private static final String Q1_FILE = "out/q1_out.csv";
  private static final String Q2_FILE = "out/q2_out.csv";
  private static final int QUEUE_CAPACITY = 10000;

  private static BlockingQueue<Q1Elem> queue_for_Q1;
  private static BlockingQueue<Q2Elem> queue_for_Q2;

  public static void main(String[] args) throws FileNotFoundException {
    String test_file;
    if(args.length == 0) {
      test_file = "out/sorted_data.csv";
    } else {
      test_file = args[0];
    }

    BufferedReader instream = new BufferedReader(new FileReader(test_file));
    PrintStream q1out = new PrintStream(new FileOutputStream(Q1_FILE, false));
    PrintStream q2out = new PrintStream(new FileOutputStream(Q2_FILE, false));

    // Initializing queues
    queue_for_Q1 = new ArrayBlockingQueue<Q1Elem>(QUEUE_CAPACITY, false);
    queue_for_Q2 = new ArrayBlockingQueue<Q2Elem>(QUEUE_CAPACITY, false);

    // start threads
    if(TWO_IO_PROCESS) {
      Thread threadForIoProcessQ1 = new Thread(new IoProcessQ1(queue_for_Q1, test_file));
      Thread threadForIoProcessQ2 = new Thread(new IoProcessQ2(queue_for_Q2, test_file));
      threadForIoProcessQ1.start();
      threadForIoProcessQ2.start();
    } else{
      Thread threadForIoProcess = new Thread(new IoProcess(queue_for_Q1,
          queue_for_Q2, instream));
      threadForIoProcess.start();
    }
    Thread threadForQ1Process = new Thread(new Q1Process(queue_for_Q1, q1out));
    Thread threadForQ2Process = new Thread(new Q2Process(queue_for_Q2, q2out));

    threadForQ1Process.start();
    threadForQ2Process.start();
  }
}
