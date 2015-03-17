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

public class Q2Process implements StatelessOperator {

	private static final long serialVersionUID = 1L;

	private TenMaxProfitability maxpft;
	private LinkedList<Q2Elem> swindow30;
	private LinkedList<Q2Elem> swindow15;
	private Geo geoq2;
	long lastms;
	Q2Elem lastevent, newevent;
	int id = 0;

	int c = 0;
	int lat_sampler = 0;
	int sec = 0;
	long init;
	float latency = 0;
	
	public Q2Process() {
	}

	@Override
	public void setUp() {
		init = System.currentTimeMillis();
		this.maxpft = new TenMaxProfitability();
		this.swindow30 = new LinkedList<Q2Elem>();
		this.swindow15 = new LinkedList<Q2Elem>();
		this.geoq2 = new Geo(-74.913585f, 41.474937f, 250, 250, 600, 600);
	}

	@Override
	public void processData(DataTuple data) {		

		latency += System.currentTimeMillis() - data.getPayload().instrumentation_ts;
		c++;
		if(c > 100000) {
			long currentTime = System.currentTimeMillis();
			System.out.println("Q2P : "+sec+" : "+(c*1000/(currentTime - init))+" : lat : "+latency/100001);
			c = 0;
			latency = 0;
			sec++;
			init = System.currentTimeMillis();
		}
		
		String medallion_hack_license = data.getString("medallion") + data.getString("hack_license");
		String pickup_datetime = data.getString("pickup_datetime");
		String dropoff_datetime = data.getString("dropoff_datetime");
		float pickup_longitude = data.getFloat("pickup_longitude");
		float pickup_latitude = data.getFloat("pickup_latitude");
		float dropoff_longitude = data.getFloat("dropoff_longitude");
		float dropoff_latitude = data.getFloat("dropoff_latitude");
		float total_fare = data.getFloat("fare_amount") + data.getFloat("tip_amount");
		
		Area from, to;
		newevent=new Q2Elem();
		try {
			newevent.medallion_hack_license = medallion_hack_license;
			newevent.pickup_datetime = new java.sql.Timestamp(parseDate(pickup_datetime));
			newevent.dropoff_datetime = new java.sql.Timestamp(parseDate(dropoff_datetime));
			from = geoq2.translate(pickup_longitude, pickup_latitude);
			if (from == null) {
//				System.out.println(pickup_datetime+", "+dropoff_datetime+", "+pickup_longitude+", "+pickup_latitude+", "+dropoff_longitude+", "+dropoff_latitude);
//				System.out.println("From translate failure");
				return;
			}
			to = geoq2.translate(dropoff_longitude, dropoff_latitude);
			if (to == null) {
//				System.out.println(pickup_datetime+", "+dropoff_datetime+", "+pickup_longitude+", "+pickup_latitude+", "+dropoff_longitude+", "+dropoff_latitude);
//				System.out.println("To translate failure");
				return;
			}
			newevent.pickup_area = from;
			newevent.dropoff_area = to;
			newevent.total_fare = total_fare;
			if (newevent.total_fare < 0) {
                return;
            }
			newevent.time_in = data.getPayload().instrumentation_ts;
			newevent.id = id++;
		} catch (Exception e) {
			System.out.println("Error in IoProcess!");
			System.out.println(newevent.medallion_hack_license + ", " +pickup_datetime+", "+dropoff_datetime+", "+pickup_longitude+", "+pickup_latitude+", "+dropoff_longitude+", "+dropoff_latitude+", " + newevent.total_fare);
			System.out.println(e.getMessage());
			e.printStackTrace();
			return;
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
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			maxpft.printMaxTen(new PrintStream(baos));
			DataTuple output = data.setValues(pickup_datetime, dropoff_datetime, baos.toString(), (System.currentTimeMillis() - newevent.time_in) );
			api.send(output);
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
