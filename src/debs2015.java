import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.LinkedList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Timestamp;

class Q1Elem {
  public Timestamp pickup_datetime;
  public Timestamp dropoff_datetime;
  public Route route;
  public long time_in;
}

class Q2Elem {
  public String medallion_hack_license;
  public Timestamp pickup_datetime;
  public Timestamp dropoff_datetime;
  public Area pickup_area;
  public Area dropoff_area;
  public float total_fare;
  public long time_in;
  public int id;
}

class QOut {
  public String data;
  public long time;
  public short query;
}

/* IoProcessor: Task to perform-
 *  *read from file
 *  *convert data from string to appropriate type
 *  *send the data to Q1 & Q2 threads (use put)
 *  *create and share kernel queues
 */
class IoProcess implements Runnable {
  private ListBlockingQueue<Q1Elem> queue_q1;
  private ListBlockingQueue<Q2Elem> queue_q2;
  private Geo geoq1;
  private Geo geoq2;
  private String inputfile;
  private int id;

  public IoProcess(ListBlockingQueue<Q1Elem> queue1,
      ListBlockingQueue<Q2Elem> queue2, String ifile) {
    this.queue_q1 = queue1;
    this.queue_q2 = queue2;
    this.geoq1 = new Geo(-74.913585f, 41.474937f, 500, 500, 300, 300);
    this.geoq2 = new Geo(-74.913585f, 41.474937f, 250, 250, 600, 600);
    this.inputfile = ifile;
    this.id = 0;
  }

  @Override
  public void run() {
    FileReader reader;
    float pickup_longitude, pickup_latitude, dropoff_longitude, dropoff_latitude;
    Area from, to;

    char buffer[] = new char[Constants.BUFFER_SIZE + Constants.MAX_LINE_SIZE];
    int startbuffer = -1;
    int endbuffer = 0;
    int oldstart = 0;

    try {
      reader = new FileReader(inputfile);

      while(true) {
        try {
          // skipping the line in case
          while(startbuffer != -1 && buffer[startbuffer] != '\n') {
            startbuffer++;
          }
          startbuffer++;

          // init
          Q1Elem q1event = new Q1Elem();
          Q2Elem q2event = new Q2Elem();
          q1event.time_in = System.currentTimeMillis();
          q2event.time_in = System.currentTimeMillis();

          // ensure enough char for one trip
          if(endbuffer-startbuffer < Constants.MAX_LINE_SIZE) {
            if(endbuffer-startbuffer != 0) {
              System.arraycopy(buffer, startbuffer, buffer, 0, endbuffer-startbuffer);
            }
            int n = reader.read(buffer, endbuffer-startbuffer, Constants.BUFFER_SIZE);
            if(n == -1 && startbuffer == endbuffer) {  // EOF
              break;
            }
            if(n != -1) {
              endbuffer = n + endbuffer - startbuffer;
            } else {
              endbuffer = endbuffer - startbuffer;
            }
            startbuffer = 0;
          }

          // medallion
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            startbuffer++;
          }

          // hack license
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            q2event.medallion_hack_license = String.copyValueOf(buffer, oldstart, startbuffer-oldstart);
            startbuffer++;
          }

          // pickup datetime
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            long pdate = Constants.parseDate(buffer, oldstart, startbuffer);
            q1event.pickup_datetime = new java.sql.Timestamp(pdate);
            q2event.pickup_datetime = new java.sql.Timestamp(pdate);
            startbuffer++;
          }

          // dropoff datetime
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            long pdate = Constants.parseDate(buffer, oldstart, startbuffer);
            q1event.dropoff_datetime = new java.sql.Timestamp(pdate);
            q2event.dropoff_datetime = new java.sql.Timestamp(pdate);
            startbuffer++;
          }

          // trip time in secs
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            startbuffer++;
          }

          // trip distance
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            break;
          } else {
            startbuffer++;
          }

          // pickup longitude
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            pickup_longitude = Constants.parseFloat(buffer, oldstart, startbuffer);
            startbuffer++;
          }

          // pickup latitude
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            pickup_latitude = Constants.parseFloat(buffer, oldstart, startbuffer);
            startbuffer++;
          }

          // geo q1
          from = geoq1.translate(pickup_longitude, pickup_latitude);
          if(from == null) {
            continue;
          }

          // geo q2
          q2event.pickup_area = geoq2.translate(pickup_longitude, pickup_latitude);
          if(q2event.pickup_area==null) {
            continue;
          }

          // dropoff longitude
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            dropoff_longitude = Constants.parseFloat(buffer, oldstart, startbuffer);
            startbuffer++;
          }

          // dropoff latitude
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            dropoff_latitude = Constants.parseFloat(buffer, oldstart, startbuffer);
            startbuffer++;
          }

          // geo q1
          to = geoq1.translate(dropoff_longitude, dropoff_latitude);
          if(to == null) {
            continue;
          }
          q1event.route = new Route(from, to);

          // geo q2
          q2event.dropoff_area = geoq2.translate(dropoff_longitude, dropoff_latitude);
          if(q2event.dropoff_area==null) {
            continue;
          }

          // payment type
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            startbuffer++;
          }

          // fare amount
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            q2event.total_fare = Constants.parseFloat(buffer, oldstart, startbuffer);
            startbuffer++;
          }

          // surcharge
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            startbuffer++;
          }

          // mta tax
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            startbuffer++;
          }

          // tip amount
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            q2event.total_fare += Constants.parseFloat(buffer, oldstart, startbuffer);
            startbuffer++;
          }
          if(q2event.total_fare < 0) {
            continue;
          }

          // tolls amount
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            startbuffer++;
          }

          // total amount
          oldstart = startbuffer;
          while(buffer[startbuffer] != '\n') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          }

          // Put events into queues for Q1 and Q2
          queue_q1.put(q1event);
          q2event.id = id++;
          queue_q2.put(q2event);
        } catch (Exception e) {
        }
      }

      // Add sentinel in Q1
      Q1Elem q1event = new Q1Elem();
      q1event.time_in = 0;
      queue_q1.putForce(q1event);

      // sentinel in Q2
      Q2Elem q2event = new Q2Elem();
      q2event.time_in = 0;
      queue_q2.putForce(q2event);
      reader.close();
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
  private ListBlockingQueue<Q1Elem> queue_q1;
  private Geo geoq1;
  private String inputfile;

  public IoProcessQ1(ListBlockingQueue<Q1Elem> queue1, String ifile) {
    this.queue_q1 = queue1;
    this.geoq1 = new Geo(-74.913585f, 41.474937f, 500, 500, 300, 300);
    this.inputfile = ifile;
  }

  @Override
  public void run() {
    FileReader reader;
    float pickup_longitude, pickup_latitude, dropoff_longitude, dropoff_latitude;
    Area from, to;

    char buffer[] = new char[Constants.BUFFER_SIZE + Constants.MAX_LINE_SIZE];
    int startbuffer = -1;
    int endbuffer = 0;
    int oldstart = 0;

    try {
      reader = new FileReader(inputfile);

      while(true) {
        try {
          // skipping the line in case
          while(startbuffer != -1 && buffer[startbuffer] != '\n') {
            startbuffer++;
          }
          startbuffer++;

          // init
          Q1Elem q1event = new Q1Elem();
          q1event.time_in = System.currentTimeMillis();

          // ensure enough char for one trip
          if(endbuffer-startbuffer < Constants.MAX_LINE_SIZE) {
            if(endbuffer-startbuffer != 0) {
              System.arraycopy(buffer, startbuffer, buffer, 0, endbuffer-startbuffer);
            }
            int n = reader.read(buffer, endbuffer-startbuffer, Constants.BUFFER_SIZE);
            if(n == -1 && startbuffer == endbuffer) {  // EOF
              break;
            }
            if(n != -1) {
              endbuffer = n + endbuffer - startbuffer;
            } else {
              endbuffer = endbuffer - startbuffer;
            }
            startbuffer = 0;
          }

          // medallion
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            startbuffer++;
          }

          // hack license
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            startbuffer++;
          }

          // pickup datetime
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            q1event.pickup_datetime = new java.sql.Timestamp(Constants.parseDate(buffer, oldstart, startbuffer));
            startbuffer++;
          }

          // dropoff datetime
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            q1event.dropoff_datetime = new java.sql.Timestamp(Constants.parseDate(buffer, oldstart, startbuffer));
            startbuffer++;
          }

          // trip time in secs
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            startbuffer++;
          }

          // trip distance
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            break;
          } else {
            startbuffer++;
          }

          // pickup longitude
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            pickup_longitude = Constants.parseFloat(buffer, oldstart, startbuffer);
            startbuffer++;
          }

          // pickup latitude
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            pickup_latitude = Constants.parseFloat(buffer, oldstart, startbuffer);
            startbuffer++;
          }

          // geo q1
          from = geoq1.translate(pickup_longitude, pickup_latitude);
          if(from == null) {
            continue;
          }

          // dropoff longitude
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            dropoff_longitude = Constants.parseFloat(buffer, oldstart, startbuffer);
            startbuffer++;
          }

          // dropoff latitude
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            dropoff_latitude = Constants.parseFloat(buffer, oldstart, startbuffer);
            startbuffer++;
          }

          // geo q1
          to = geoq1.translate(dropoff_longitude, dropoff_latitude);
          if(to == null) {
            continue;
          }
          q1event.route = new Route(from, to);

          // Put events into queues for Q1 and Q2
          queue_q1.put(q1event);
        } catch (Exception e) {
        }
      }

      // Add sentinel in Q1
      Q1Elem q1event = new Q1Elem();
      q1event.time_in = 0;
      queue_q1.putForce(q1event);
      reader.close();
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
  private ListBlockingQueue<Q2Elem> queue_q2;
  private Geo geoq2;
  private String inputfile;
  private int id;

  public IoProcessQ2(ListBlockingQueue<Q2Elem> queue2, String ifile) {
    this.queue_q2 = queue2;
    this.geoq2 = new Geo(-74.913585f, 41.474937f, 250, 250, 600, 600);
    this.inputfile = ifile;
    id = 0;
  }

  @Override
  public void run() {
    FileReader reader;
    float pickup_longitude, pickup_latitude, dropoff_longitude, dropoff_latitude;

    char buffer[] = new char[Constants.BUFFER_SIZE + Constants.MAX_LINE_SIZE];
    int startbuffer = -1;
    int endbuffer = 0;
    int oldstart = 0;

    try {
      reader = new FileReader(inputfile);

      while(true) {
        try {
          // skipping the line in case
          while(startbuffer != -1 && buffer[startbuffer] != '\n') {
            startbuffer++;
          }
          startbuffer++;

          // init
          Q2Elem q2event = new Q2Elem();
          q2event.time_in = System.currentTimeMillis();

          // ensure enough char for one trip
          if(endbuffer-startbuffer < Constants.MAX_LINE_SIZE) {
            if(endbuffer-startbuffer != 0) {
              System.arraycopy(buffer, startbuffer, buffer, 0, endbuffer-startbuffer);
            }
            int n = reader.read(buffer, endbuffer-startbuffer, Constants.BUFFER_SIZE);
            if(n == -1 && startbuffer == endbuffer) {  // EOF
              break;
            }
            if(n != -1) {
              endbuffer = n + endbuffer - startbuffer;
            } else {
              endbuffer = endbuffer - startbuffer;
            }
            startbuffer = 0;
          }

          // medallion
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            startbuffer++;
          }

          // hack license
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            q2event.medallion_hack_license = String.copyValueOf(buffer, oldstart, startbuffer-oldstart);
            startbuffer++;
          }

          // pickup datetime
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            q2event.pickup_datetime = new java.sql.Timestamp(Constants.parseDate(buffer, oldstart, startbuffer));
            startbuffer++;
          }

          // dropoff datetime
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            q2event.dropoff_datetime = new java.sql.Timestamp(Constants.parseDate(buffer, oldstart, startbuffer));
            startbuffer++;
          }

          // trip time in secs
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            startbuffer++;
          }

          // trip distance
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            break;
          } else {
            startbuffer++;
          }

          // pickup longitude
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            pickup_longitude = Constants.parseFloat(buffer, oldstart, startbuffer);
            startbuffer++;
          }

          // pickup latitude
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            pickup_latitude = Constants.parseFloat(buffer, oldstart, startbuffer);
            startbuffer++;
          }

          // geo q2
          q2event.pickup_area = geoq2.translate(pickup_longitude, pickup_latitude);
          if(q2event.pickup_area==null) {
            continue;
          }

          // dropoff longitude
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            dropoff_longitude = Constants.parseFloat(buffer, oldstart, startbuffer);
            startbuffer++;
          }

          // dropoff latitude
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            dropoff_latitude = Constants.parseFloat(buffer, oldstart, startbuffer);
            startbuffer++;
          }

          // geo q2
          q2event.dropoff_area = geoq2.translate(dropoff_longitude, dropoff_latitude);
          if(q2event.dropoff_area==null) {
            continue;
          }

          // payment type
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            startbuffer++;
          }

          // fare amount
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            q2event.total_fare = Constants.parseFloat(buffer, oldstart, startbuffer);
            startbuffer++;
          }

          // surcharge
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            startbuffer++;
          }

          // mta tax
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            startbuffer++;
          }

          // tip amount
          oldstart = startbuffer;
          while(buffer[startbuffer] != ',') {
            startbuffer++;
          }
          // if empty
          if(oldstart == startbuffer) {
            continue;
          } else {
            q2event.total_fare += Constants.parseFloat(buffer, oldstart, startbuffer);
            startbuffer++;
          }
          if(q2event.total_fare < 0) {
            continue;
          }

          // Put events into queues for Q1 and Q2
          q2event.id = id++;
          queue_q2.put(q2event);
        } catch (Exception e) {
        }
      }

      // sentinel in Q2
      Q2Elem q2event = new Q2Elem();
      q2event.time_in = 0;
      queue_q2.putForce(q2event);
      reader.close();
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
  private ListBlockingQueue<Q1Elem> queue;
  private ArrayBlockingQueue<QOut> output_queue;
  private TenMaxFrequency maxfs;
  private LinkedList<Q1Elem> sliding_window;

  public Q1Process(ListBlockingQueue<Q1Elem> queue,
      ArrayBlockingQueue<QOut> output_queue) {
    this.queue = queue;
    this.output_queue = output_queue;
    this.maxfs = new TenMaxFrequency();
    this.sliding_window = new LinkedList<Q1Elem>();
  }

  @Override
  public void run() {
    try {
      Q1Elem lastevent, newevent=queue.take();
      long lastms = 0;
      boolean ten_max_changed = false;

      while(newevent.time_in != 0) {
        ten_max_changed = false;

        // Check if events are leaving the sliding window and process them
        long currentms = newevent.dropoff_datetime.getTime();
        if(sliding_window.size() != 0) {
          lastevent = sliding_window.getFirst();
          lastms = lastevent.dropoff_datetime.getTime();

          // Remove the elements from the start of the window
          while((currentms-lastms) >= Constants.WINDOW30_SIZE) {
            if(!ten_max_changed) {
              ten_max_changed = maxfs.decreaseFrequency(lastevent.route, lastevent.dropoff_datetime.getTime());
            } else {
              maxfs.decreaseFrequency(lastevent.route, lastevent.dropoff_datetime.getTime());
            }
            sliding_window.removeFirst();

            if(sliding_window.size() != 0) {
              lastevent = sliding_window.getFirst();
              lastms = lastevent.dropoff_datetime.getTime();
            } else {
              break;
            }
          }
        }

        // Insert the current element in the sliding window
        if(!ten_max_changed) {
          ten_max_changed = maxfs.increaseFrequency(newevent.route, newevent.dropoff_datetime.getTime());
        } else {
          maxfs.increaseFrequency(newevent.route, newevent.dropoff_datetime.getTime());
        }
        sliding_window.addLast(newevent);

        if(ten_max_changed) {
          QOut out = new QOut();
          out.data = newevent.pickup_datetime.toString() + "," + newevent.dropoff_datetime.toString() + ",";
          out.data += maxfs.printMaxTen();
          out.time = newevent.time_in;
          out.query = 1;
          output_queue.put(out);
        }

        // Get the next event to process from the queue
        newevent = queue.take();
      }
    } catch(InterruptedException e) {
      System.out.println("Error in Q1Process!");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }

    try {
      QOut out = new QOut();
      out.data = "\0";
      out.time = 0;
      out.query = 1;
      output_queue.put(out);
    } catch (Exception e) {
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
  private ListBlockingQueue<Q2Elem> queue;
  private ArrayBlockingQueue<QOut> output_queue;
  private TenMaxProfitability maxpft;
  private LinkedList<Q2Elem> swindow30;
  private LinkedList<Q2Elem> swindow15;
  // private PrintStream print_stream;

  public Q2Process(ListBlockingQueue<Q2Elem> queue2, ArrayBlockingQueue<QOut> output_queue) {
    this.queue = queue2;
    this.output_queue = output_queue;
    this.maxpft = new TenMaxProfitability();
    this.swindow30 = new LinkedList<Q2Elem>();
    this.swindow15 = new LinkedList<Q2Elem>();
  }

  @Override
  public void run() {
    try {
      Q2Elem lastevent, newevent = queue.take();
      long lastms;

      while(newevent.time_in != 0) {

        // Check if events are leaving the sliding window and process them
        long currentms = newevent.dropoff_datetime.getTime();
        if(swindow30.size() != 0) {
          lastevent = swindow30.getFirst();
          lastms = lastevent.dropoff_datetime.getTime();

          while((currentms-lastms) >= Constants.WINDOW30_SIZE) {
            maxpft.leaveTaxiSlidingWindow(lastevent.medallion_hack_license,
                lastevent.id);
            swindow30.removeFirst();

            if(swindow30.size() != 0) {
              lastevent = swindow30.getFirst();
              lastms = lastevent.dropoff_datetime.getTime();
            } else {
              break;
            }
          }

          // This means sliding window for 15 minutes is not empty
          if(swindow15.size() != 0) {
            lastevent = swindow15.getFirst();
            lastms = lastevent.dropoff_datetime.getTime();

            while((currentms-lastms) >= Constants.WINDOW15_SIZE) {
              maxpft.leaveProfitSlidingWindow(lastevent.pickup_area,
                  lastevent.id,lastevent.total_fare);
              swindow15.removeFirst();

              if(swindow15.size() != 0) {
                lastevent = swindow15.getFirst();
                lastms = lastevent.dropoff_datetime.getTime();
              } else {
                break;
              }
            }
          }
        }

        // add the incoming event
        maxpft.enterProfitSlidingWindow(newevent.pickup_area,newevent.id,
            newevent.total_fare, newevent.dropoff_datetime.getTime());
        maxpft.enterTaxiSlidingWindow(newevent.medallion_hack_license,
            newevent.dropoff_area, newevent.id);

        swindow15.addLast(newevent);
        swindow30.addLast(newevent);

        if(!maxpft.isSameMaxTenKey()) {
          QOut out = new QOut();
          out.data = newevent.pickup_datetime.toString() + "," +
              newevent.dropoff_datetime.toString() + ",";
          out.data += maxpft.printMaxTen();
          out.time = newevent.time_in;
          out.query = 2;
          output_queue.put(out);
        }

        // Get the next event to process from the queue
        newevent = queue.take();
      }
    } catch(Exception e) {
      System.out.println("Error in Q2Process!");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }

    try {
      QOut out = new QOut();
      out.data = "\0";
      out.time = 0;
      out.query = 1;
      output_queue.put(out);
    } catch (Exception e) {
      System.out.println("Error in Q1Process!");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}

/* PrintProcess: Task to perform
 *  *dequeue string from queue (use take)
 *  *print string
 */
class PrintProcess implements Runnable {
  private ArrayBlockingQueue<QOut> queue;
  private int numthreads;

  public PrintProcess(ArrayBlockingQueue<QOut> queue, int num_process_threads) {
    this.queue = queue;
    this.numthreads = num_process_threads;
  }

  @Override
  public void run() {
    try {
      QOut qout = queue.take();
      QOut prevq1 = new QOut();
      prevq1.data = "";
      QOut prevq2 = new QOut();
      prevq2.data = "";
      int count = 0;

      while(true) {
        if(qout.data.equals("\0")) {
          count++;
          if(count == this.numthreads) {
            break;
          } else {
            qout = queue.take();
            continue;
          }
        }

        if(qout.query == 1) {
          if(!qout.equals(prevq1.data)) {
            long delay = System.currentTimeMillis() - qout.time;
            System.out.println(qout.data+String.valueOf(delay));
            prevq1 = qout;
          }
        } else {
          if(!qout.equals(prevq2.data)) {
            long delay = System.currentTimeMillis() - qout.time;
            System.out.println(qout.data+String.valueOf(delay));
            prevq2 = qout;
          }
        }

        while(null == (qout = queue.poll())) {
          Thread.sleep(100);
        }
      }
    } catch(Exception e) {
      System.out.println("Error in PrintProcess!");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}

public class debs2015 {
  private static ListBlockingQueue<Q1Elem> queue_for_Q1;
  private static ListBlockingQueue<Q2Elem> queue_for_Q2;
  private static ArrayBlockingQueue<QOut> output_queue;

  public static void main(String[] args) throws FileNotFoundException {
    String test_file;
    boolean running_q1 = true;
    boolean running_q2 = true;
    boolean two_io_process = true;
    int shift = 0;

    if(args.length == 0) {
      test_file = Constants.DEFAULT_INPUT_FILE;
    } else {
      if(args.length > 0 && !args[0].equals("1") && !args[0].equals("2")) {
        test_file = args[0];
        shift = 1;
      } else {
        test_file = Constants.DEFAULT_INPUT_FILE;
      }
    }

    if(args.length > (0+shift)) {
      if(args[0+shift].equals("1")) {
        two_io_process = false;
      }
    }

    if(args.length > (1+shift)) {
      if(args[1+shift].equals("1")) {
        running_q2 = false;
      } else {
        running_q1 = false;
      }
    }

    // Initializing queues
    output_queue = new ArrayBlockingQueue<QOut>(Constants.QUEUE_OUTPUT_CAPACITY, false);
    if(running_q1) {
      queue_for_Q1 = new ListBlockingQueue<Q1Elem>(Constants.QUEUE1_CAPACITY, Constants.BLOCK_SIZE);
    }
    if(running_q2) {
      queue_for_Q2 = new ListBlockingQueue<Q2Elem>(Constants.QUEUE2_CAPACITY, Constants.BLOCK_SIZE);
    }

    // start threads
    if(two_io_process || (!(running_q1 && running_q2))) {
      Thread threadForIoProcessQ1 = new Thread(new IoProcessQ1(queue_for_Q1, test_file));
      Thread threadForIoProcessQ2 = new Thread(new IoProcessQ2(queue_for_Q2, test_file));
      if(running_q1) threadForIoProcessQ1.start();
      if(running_q2) threadForIoProcessQ2.start();
    } else {
      Thread threadForIoProcess = new Thread(new IoProcess(queue_for_Q1,
          queue_for_Q2, test_file));
      threadForIoProcess.start();
    }

    // print thread
    int num_query_threads = 1;
    if(two_io_process) {
      num_query_threads = 2;
    }
    Thread threadForPrint = new Thread(new PrintProcess(output_queue, num_query_threads));
    threadForPrint.start();

    Thread threadForQ1Process = new Thread(new Q1Process(queue_for_Q1, output_queue));
    if(running_q1) {
      threadForQ1Process.start();
    }

    Thread threadForQ2Process = new Thread(new Q2Process(queue_for_Q2, output_queue));
    if(running_q2) {
      threadForQ2Process.start();
    }
  }
}
