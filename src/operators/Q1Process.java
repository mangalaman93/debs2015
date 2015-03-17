package operators;


import utils.*;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.io.PrintStream;

import uk.ac.imperial.lsds.seep.comm.serialization.DataTuple;
import uk.ac.imperial.lsds.seep.operator.StatelessOperator;
import uk.ac.imperial.lsds.seep.state.StateWrapper;

class Q1Elem {
	public Timestamp pickup_datetime;
	public Timestamp dropoff_datetime;
	public Route route;
	public long time_in;
}


public class Q1Process implements StatelessOperator {

	private static final long serialVersionUID = 1L;

	private TenMaxFrequency maxfs;
	private LinkedList<Q1Elem> sliding_window;
	private Geo geoq1;
	Q1Elem lastevent, newevent;
	long lastms = 0;
	boolean ten_max_changed = false;

	int c = 0;
	int lat_sampler = 0;
	int sec = 0;
	long init;
	float latency = 0;
	
	public Q1Process() {
	}

	@Override
	public void setUp() {
		init = System.currentTimeMillis();
		this.maxfs = new TenMaxFrequency();
		this.sliding_window = new LinkedList<Q1Elem>();
		this.geoq1 = new Geo(-74.913585f, 41.474937f, 500, 500, 300, 300);
	}

	@Override
	public void processData(DataTuple data) {		

		latency += System.currentTimeMillis() - data.getPayload().instrumentation_ts;
		c++;
		if(c > 100000) {
			long currentTime = System.currentTimeMillis();
			System.out.println("Q1P : "+sec+" : "+(c*1000/(currentTime - init))+" : lat : "+latency/100001);
			c = 0;
			latency = 0;
			sec++;
			init = System.currentTimeMillis();
		}
		
		String pickup_datetime = data.getString("pickup_datetime");
		String dropoff_datetime = data.getString("dropoff_datetime");
		float pickup_longitude = data.getFloat("pickup_longitude");
		float pickup_latitude = data.getFloat("pickup_latitude");
		float dropoff_longitude = data.getFloat("dropoff_longitude");
		float dropoff_latitude = data.getFloat("dropoff_latitude");
		
		Area from, to;
		newevent=new Q1Elem();
		try {
			newevent.pickup_datetime = new java.sql.Timestamp(parseDate(pickup_datetime));
			newevent.dropoff_datetime = new java.sql.Timestamp(parseDate(dropoff_datetime));
			from = geoq1.translate(pickup_longitude, pickup_latitude);
			if (from == null) {
//				System.out.println(pickup_datetime+", "+dropoff_datetime+", "+pickup_longitude+", "+pickup_latitude+", "+dropoff_longitude+", "+dropoff_latitude);
//				System.out.println("From translate failure");
				return;
			}
			to = geoq1.translate(dropoff_longitude, dropoff_latitude);
			if (to == null) {
//				System.out.println(pickup_datetime+", "+dropoff_datetime+", "+pickup_longitude+", "+pickup_latitude+", "+dropoff_longitude+", "+dropoff_latitude);
//				System.out.println("To translate failure");
				return;
			}
			newevent.route = new Route (from, to);
			newevent.time_in = data.getPayload().instrumentation_ts;
		} catch (Exception e) {
			System.out.println("Error in IoProcess!");
			System.out.println(pickup_datetime+", "+dropoff_datetime+", "+pickup_longitude+", "+pickup_latitude+", "+dropoff_longitude+", "+dropoff_latitude);
			System.out.println(e.getMessage());
			e.printStackTrace();
			return;
		}
		ten_max_changed = false;
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
				}
				else{
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
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				maxfs.printMaxTen(new PrintStream(baos));
				DataTuple output = data.setValues(pickup_datetime, dropoff_datetime, baos.toString(), (System.currentTimeMillis() - newevent.time_in) );
				api.send(output);
			}
		}
	}
	
	@Override
	public void processData(List<DataTuple> dataList) { }

	public static long parseDate(String date) {
		long timestamp = 1356998400;
		int temp=0;
		int i=1;
		int[] cdays = {	0,
							31,
							31+28,
							31+28+31,
							31+28+31+30,
							31+28+31+30+31,
							31+28+31+30+31+30,
							31+28+31+30+31+30+31,
							31+28+31+30+31+30+31+31,
							31+28+31+30+31+30+31+31+30,
							31+28+31+30+31+30+31+31+30+31,
							31+28+31+30+31+30+31+31+30+31+30};

		for (int c = 0; c < date.length(); c++) {
			char ch = date.charAt(c);
			if (ch == '-' || ch == ':' || ch == ' ') {
				switch (i) {
					case 1 : timestamp += (temp-2013)*365	*24*60*60; break;
					case 2 : timestamp += cdays[temp-1]		*24*60*60; break;
					case 3 : timestamp += (temp-1)			*24*60*60; break;
					case 4 : timestamp += temp				   *60*60; break;
					case 5 : timestamp += temp				      *60; break;
				}
				temp = 0;
				i++;
			}
			else {
				temp *= 10;
				temp += ch -'0';
			}
			//System.out.println("timestamp : "+timestamp+" temp : " + temp + " ch : "+ch +" i : "+ i);
		}
		timestamp += temp;
		return timestamp*1000;
	}
}
