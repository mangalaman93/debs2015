import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Date;
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

/* IoProcessor: Task to perform-
 *  *read from file
 *  *convert data from string to appropriate type
 *  *send the data to Q1 & Q2 threads (use put)
 *  *create and share kernel queues
 */
class IoProcess implements Runnable {
	private BlockingQueue<Q1Elem> queue_q1;
	private BlockingQueue<Q2Elem> queue_q2;
	private Geo geoq1;
	private Geo geoq2;
	private String inputfile;

	public IoProcess(BlockingQueue<Q1Elem> queue1,
			BlockingQueue<Q2Elem> queue2, String ifile) {
		this.queue_q1 = queue1;
		this.queue_q2 = queue2;
		this.geoq1 = new Geo(-74.913585f, 41.474937f, 500, 500, 300, 300);
		this.geoq2 = new Geo(-74.913585f, 41.474937f, 250, 250, 600, 600);
		this.inputfile = ifile;
	}

	@Override
	public void run() {
		try {
			BufferedReader inputstream = new BufferedReader(new FileReader(inputfile));
			SimpleDateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			float pickup_longitude, pickup_latitude, dropoff_longitude, dropoff_latitude;
			Area from, to;
			String line;

			while((line = inputstream.readLine()) != null) {
				try {
					StringTokenizer st = new StringTokenizer(line, ",");
					Q1Elem q1event = new Q1Elem();
					Q2Elem q2event = new Q2Elem();

					// medallion+hack license
					q2event.medallion_hack_license = st.nextToken()+st.nextToken();

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
					pickup_longitude = Float.parseFloat(st.nextToken());
					// pickup latitude
					pickup_latitude = Float.parseFloat(st.nextToken());
					// dropoff longitude
					dropoff_longitude = Float.parseFloat(st.nextToken());
					// dropoff latitude
					dropoff_latitude = Float.parseFloat(st.nextToken());
					// payment type
					st.nextToken();
					// fare amount
					q2event.total_fare = Float.parseFloat(st.nextToken());
					// surcharge
					st.nextToken();
					// mta tax
					st.nextToken();
					// tip amount
					q2event.total_fare += Float.parseFloat(st.nextToken());
					// tolls amount
					st.nextToken();
					// total amount
					st.nextToken();

					// geo q1
					from = geoq1.translate(pickup_longitude, pickup_latitude);
					if(from == null) {
						continue;
					}
					to = geoq1.translate(dropoff_longitude, dropoff_latitude);
					if(to == null) {
						continue;
					}
					q1event.route = new Route(from, to);

					// geo q2
					q2event.pickup_area = geoq2.translate(pickup_longitude, pickup_latitude);
					if(q2event.pickup_area==null) {
						continue;
					}
					q2event.dropoff_area = geoq2.translate(dropoff_longitude, dropoff_latitude);
					if(q2event.dropoff_area==null) {
						continue;
					}

					// putting current time
					q1event.time_in = System.currentTimeMillis();
					q2event.time_in = System.currentTimeMillis();

					// Put events into queues for Q1 and Q2
					queue_q1.put(q1event);
					queue_q2.put(q2event);
				} catch(Exception e) {
					System.out.println("Error in parsing. Skipping..." + line);
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}

			// Add sentinel in Q1
			Q1Elem q1event = new Q1Elem();
			q1event.time_in = 0;
			queue_q1.put(q1event);

			// sentinel in Q2
			Q2Elem q2event = new Q2Elem();
			q2event.time_in = 0;
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
	private Geo geoq1;
	private String inputfile;

	public IoProcessQ1(BlockingQueue<Q1Elem> queue1, String ifile) {
		this.queue_q1 = queue1;
		this.geoq1 = new Geo(-74.913585f, 41.474937f, 500, 500, 300, 300);
		this.inputfile = ifile;
	}

	@Override
	public void run() {
		try {
			BufferedReader inputstream = new BufferedReader(new FileReader(inputfile));
			SimpleDateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Area from, to;
			String line;

			while((line = inputstream.readLine()) != null) {
				try {
					StringTokenizer st = new StringTokenizer(line, ",");
					Q1Elem q1event = new Q1Elem();

					// medallion
					st.nextToken();
					// hack license
					st.nextToken();

					// pickup datetime
					q1event.pickup_datetime = new java.sql.Timestamp(datefmt.parse(st.nextToken()).getTime());
					// dropoff datetime
					q1event.dropoff_datetime = new java.sql.Timestamp(datefmt.parse(st.nextToken()).getTime());

					// trip time in secs
					st.nextToken();
					// trip distance
					st.nextToken();

					// pickup longitude, pickup latitude, dropoff longitude, dropoff latitude
					from = geoq1.translate(Float.parseFloat(st.nextToken()),
							Float.parseFloat(st.nextToken()));
					if(from == null) {
						continue;
					}
					to = geoq1.translate(Float.parseFloat(st.nextToken()),
									Float.parseFloat(st.nextToken()));
					if(to == null) {
						continue;
					}
					q1event.route = new Route(from, to);

					// current time
					q1event.time_in = System.currentTimeMillis();

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
			q1event.time_in = 0;
			queue_q1.put(q1event);
			inputstream.close();
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
	private Geo geoq2;
	private String inputfile;
	private int id;

	public IoProcessQ2(BlockingQueue<Q2Elem> queue2, String ifile) {
		this.queue_q2 = queue2;
		this.geoq2 = new Geo(-74.913585f, 41.474937f, 250, 250, 600, 600);
		this.inputfile = ifile;
		id = 0;
	}

	@Override
	public void run() {
		try {
			BufferedReader inputstream = new BufferedReader(new FileReader(inputfile));
			SimpleDateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String line;

			while((line = inputstream.readLine()) != null) {
				try {
					StringTokenizer st = new StringTokenizer(line, ",");
					Q2Elem q2event = new Q2Elem();

					// medallion+hack license
					q2event.medallion_hack_license = st.nextToken()+st.nextToken();

					// pickup datetime
					q2event.pickup_datetime = new java.sql.Timestamp(datefmt.parse(st.nextToken()).getTime());
					// dropoff datetime
					q2event.dropoff_datetime = new java.sql.Timestamp(datefmt.parse(st.nextToken()).getTime());

					// trip time in secs
					st.nextToken();
					// trip distance
					st.nextToken();

					// pickup longitude, pickup latitude
					q2event.pickup_area = geoq2.translate(Float.parseFloat(st.nextToken()),
							Float.parseFloat(st.nextToken()));
					if(q2event.pickup_area==null) {
						continue;
					}

					// dropoff longitude, dropoff latitude
					q2event.dropoff_area = geoq2.translate(Float.parseFloat(st.nextToken()),
							Float.parseFloat(st.nextToken()));
					if(q2event.dropoff_area==null) {
						continue;
					}

					// payment type
					st.nextToken();
					// fare amount
					q2event.total_fare = Float.parseFloat(st.nextToken());
					if(q2event.total_fare < 0) {
					  continue;
					}
					// surcharge
					st.nextToken();
					// mta tax
					st.nextToken();
					// tip amount
					q2event.total_fare += Float.parseFloat(st.nextToken());

					// current time
					q2event.time_in = System.currentTimeMillis();

					// Put events into queues for Q2
					q2event.id = id++;
					queue_q2.put(q2event);
				} catch(Exception e) {
					System.out.println("Error parsing for query 2. Skipping..." + line);
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}

			// Add sentinel
			Q2Elem q2event = new Q2Elem();
			q2event.time_in = 0;
			queue_q2.put(q2event);
			inputstream.close();
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
	private BlockingQueue<Q1Elem> queue;
	private TenMaxFrequency maxfs;
	private LinkedList<Q1Elem> sliding_window;
	private PrintStream print_stream;

	public Q1Process(BlockingQueue<Q1Elem> queue, OutputStream print_stream) {
		this.queue = queue;
		this.maxfs = new TenMaxFrequency();
		this.sliding_window = new LinkedList<Q1Elem>();
		this.print_stream = new PrintStream(print_stream);
	}

	@Override
	public void run() {
		int in_count = 0;
		long last_time = System.currentTimeMillis();

		try {
			Q1Elem lastevent, newevent=queue.take();
			long lastms = 0;
			boolean ten_max_changed = false;

			while(newevent.time_in != 0) {
				ten_max_changed = false;
				in_count++;
				if(in_count == 100000) {
					System.out.println("throughput: "+(100000/(System.currentTimeMillis()-last_time)));
					in_count = 0;
					last_time = System.currentTimeMillis();
				}

				maxfs.storeMaxTenCopy();

				// Check if events are leaving the sliding window and process them
				long currentms = newevent.dropoff_datetime.getTime();
				if(sliding_window.size() != 0) {
					lastevent = sliding_window.getFirst();
					lastms = lastevent.dropoff_datetime.getTime();

					// Remove the elements from the start of the window
					while((currentms-lastms) >= 1800000) {
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
				if(!ten_max_changed){
					ten_max_changed = maxfs.increaseFrequency(newevent.route, newevent.dropoff_datetime.getTime());
				}
				else{
					maxfs.increaseFrequency(newevent.route, newevent.dropoff_datetime.getTime());
				}
				sliding_window.addLast(newevent);

				if(ten_max_changed){
					if(!maxfs.isSameMaxTenKey()) {
						print_stream.print(newevent.pickup_datetime.toString());
						print_stream.print(",");
						print_stream.print(newevent.dropoff_datetime.toString());
						print_stream.print(",");
						maxfs.printMaxTen(print_stream);
						print_stream.print(System.currentTimeMillis() - newevent.time_in);
						print_stream.print("\n");
					}
				}

				// Get the next event to process from the queue
				newevent = queue.take();
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
	private BlockingQueue<Q2Elem> queue;
	private TenMaxProfitability maxpft;
	private LinkedList<Q2Elem> swindow30;
	private LinkedList<Q2Elem> swindow15;
	private PrintStream print_stream;

	public Q2Process(BlockingQueue<Q2Elem> queue2, OutputStream print_stream) {
		this.queue = queue2;
		this.maxpft = new TenMaxProfitability();
		this.swindow30 = new LinkedList<Q2Elem>();
		this.swindow15 = new LinkedList<Q2Elem>();
		this.print_stream = new PrintStream(print_stream);
	}

	@Override
	public void run() {
		int in_count = 0;
		long last_time = System.currentTimeMillis();
		try {
			Q2Elem lastevent, newevent = queue.take();
			long lastms;

			while(newevent.time_in != 0) {
				in_count++;
				if(in_count == 10000) {
					System.out.println("throughput: "+(10000/(System.currentTimeMillis()-last_time)));
					in_count = 0;
					last_time = System.currentTimeMillis();
				}
				// maxpft.storeMaxTenCopy();

				// Check if events are leaving the sliding window and process them
				long currentms = newevent.dropoff_datetime.getTime();
				if(swindow30.size() != 0) {
					lastevent = swindow30.getFirst();
					lastms = lastevent.dropoff_datetime.getTime();

					while((currentms-lastms) >= 1800000) {
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

						while((currentms-lastms) >= 900000) {
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
					print_stream.print(newevent.pickup_datetime.toString());
					print_stream.print(",");
					print_stream.print(newevent.dropoff_datetime.toString());
					print_stream.print(",");
					maxpft.printMaxTen(print_stream);
					print_stream.print(System.currentTimeMillis() - newevent.time_in);
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
	private static BlockingQueue<Q1Elem> queue_for_Q1;
	private static BlockingQueue<Q2Elem> queue_for_Q2;

	public static void main(String[] args) throws FileNotFoundException {
		String test_file;
		if(args.length == 0) {
			test_file = Constants.DEFAULT_INPUT_FILE;
		} else {
			test_file = args[0];
		}

		// Initializing queues
		// queue_for_Q1 = new ArrayBlockingQueue<Q1Elem>(Constants.QUEUE1_CAPACITY, false);
		queue_for_Q2 = new ArrayBlockingQueue<Q2Elem>(Constants.QUEUE2_CAPACITY, false);

		// start threads
		if(Constants.TWO_IO_PROCESS) {
			// Thread threadForIoProcessQ1 = new Thread(new IoProcessQ1(queue_for_Q1, test_file));
			Thread threadForIoProcessQ2 = new Thread(new IoProcessQ2(queue_for_Q2, test_file));
			// threadForIoProcessQ1.start();
			threadForIoProcessQ2.start();
		} else {
			Thread threadForIoProcess = new Thread(new IoProcess(queue_for_Q1, queue_for_Q2, test_file));
			threadForIoProcess.start();
		}

		// PrintStream q1out = new PrintStream(new FileOutputStream(Constants.Q1_FILE, false));
		// Thread threadForQ1Process = new Thread(new Q1Process(queue_for_Q1, q1out));
		// threadForQ1Process.start();

		PrintStream q2out = new PrintStream(new FileOutputStream(Constants.Q2_FILE, false));
		Thread threadForQ2Process = new Thread(new Q2Process(queue_for_Q2, q2out));
		threadForQ2Process.start();
	}
}
