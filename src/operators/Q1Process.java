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
	int delay_sampler = 0;
	int sec = 0;
	long init;
	int latency = 0;
	int delay = 0;
	
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
		if (data.getPayload().instrumentation_ts == 0 ) return;
		latency += System.currentTimeMillis() - data.getPayload().instrumentation_ts;
		c++;
		if(c > 100000) {
			long currentTime = System.currentTimeMillis();
			System.out.println("Q1P : "+sec+" : "+(c*1000/(currentTime - init))+" : lat : "+latency/100001.0+" : delay : "+delay/delay_sampler);
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
			newevent.pickup_datetime = new java.sql.Timestamp(Constants.parseDate(pickup_datetime));
			newevent.dropoff_datetime = new java.sql.Timestamp(Constants.parseDate(dropoff_datetime));
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

        long currentms = newevent.dropoff_datetime.getTime();
        if(sliding_window.size() != 0) {
          lastevent = sliding_window.getFirst();
          lastms = lastevent.dropoff_datetime.getTime();

          // Remove the elements from the start of the window
          while((currentms-lastms) >= Constants.WINDOW30_SIZE) {
            maxfs.decreaseFrequency(lastevent.route, lastevent.dropoff_datetime.getTime());
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
        maxfs.increaseFrequency(newevent.route, newevent.dropoff_datetime.getTime());
        sliding_window.addLast(newevent);

        if(!maxfs.isSameMaxTenKey()){
        	delay += (System.currentTimeMillis() - newevent.time_in);
		delay_sampler++;
			DataTuple output = data.setValues("Q1", pickup_datetime, dropoff_datetime, maxfs.printMaxTen(), (System.currentTimeMillis() - newevent.time_in) );
			api.send(output);
        }
	}
	
	@Override
	public void processData(List<DataTuple> dataList) { }
}
