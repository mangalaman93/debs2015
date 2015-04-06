package bolts;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.tuple.Fields;

import utils.*;
import spouts.*;

public class Q2Process extends BaseBasicBolt {
	private TenMaxProfitability maxpft;
  	private LinkedList<Q2Elem> swindow30;
  	private LinkedList<Q2Elem> swindow15;
	int in_count;
  	long last_time;
  	private final static int NUM = 100000;

	@Override
	public void cleanup() {
		System.out.println("OVER");
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		// System.out.println("STARTED");
		this.maxpft = new TenMaxProfitability();
    		this.swindow30 = new LinkedList<Q2Elem>();
    		this.swindow15 = new LinkedList<Q2Elem>();
		this.in_count = 0;
    		this.last_time = System.currentTimeMillis();
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		Object v = input.getValue(0);
		Q2Elem newevent = (Q2Elem) v;
		Q2Elem lastevent = null;
		long lastms = 0;

		in_count++;
		//System.out.println("proc " + in_count);
		if(in_count == NUM) {
	          System.err.println("Query 2 throughput: "+(NUM/(System.currentTimeMillis()-last_time)));
        	  in_count = 0;
          	  last_time = System.currentTimeMillis();
        	}
		
		if(newevent.time_in != 0) {
			long currentms = newevent.dropoff_datetime.getTime();
        		if(swindow30.size() != 0) {
          			lastevent = swindow30.getFirst();
          			lastms = lastevent.dropoff_datetime.getTime();
          			while((currentms-lastms) >= Constants.WINDOW30_SIZE) {
            				maxpft.leaveTaxiSlidingWindow(lastevent.medallion_hack_license,lastevent.id);
            				swindow30.removeFirst();
            				if(swindow30.size() != 0) {
              					lastevent = swindow30.getFirst();
              					lastms = lastevent.dropoff_datetime.getTime();
            				} else {
              					break;
            				}
          			}	
			}

			if(swindow15.size() != 0) {
				lastevent = swindow15.getFirst();
				lastms = lastevent.dropoff_datetime.getTime();
				while((currentms-lastms) >= Constants.WINDOW15_SIZE) {
              				maxpft.leaveProfitSlidingWindow(lastevent.pickup_area,lastevent.id,lastevent.total_fare);
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

		maxpft.enterProfitSlidingWindow(newevent.pickup_area,newevent.id,newevent.total_fare, newevent.dropoff_datetime.getTime());
        	maxpft.enterTaxiSlidingWindow(newevent.medallion_hack_license,newevent.dropoff_area, newevent.id);
		swindow15.addLast(newevent);
		swindow30.addLast(newevent);
		if(!maxpft.isSameMaxTenKey()) {
			String s = newevent.pickup_datetime.toString() + "," + newevent.dropoff_datetime.toString() + ",";
			s = s + maxpft.printMaxTen();
			collector.emit(new Values(s,newevent.time_in));
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("q2output","q2delay"));
	}
}
