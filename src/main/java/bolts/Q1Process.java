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

public class Q1Process extends BaseBasicBolt {

	TenMaxFrequency maxfs;
  	LinkedList<Q1Elem> sliding_window;
  	int in_count;
  	long last_time;
  	private final static int NUM = 3000;

	@Override
	public void cleanup() {
		System.out.println("OVER");
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		// System.out.println("STARTED");
		this.maxfs = new TenMaxFrequency();
    	this.sliding_window = new LinkedList<Q1Elem>();
    	this.in_count = 0;
    	this.last_time = System.currentTimeMillis();
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		Object v = input.getValue(0);
		Q1Elem newevent = (Q1Elem) v;
		Q1Elem lastevent = null;
		long lastms = 0;
		in_count++;
		//System.out.println("proc " + in_count);
		if(in_count == NUM) {
	          System.out.println("Query 1 throughput: "+(NUM/(System.currentTimeMillis()-last_time)));
        	  in_count = 0;
          	  last_time = System.currentTimeMillis();
        	}

		// System.out.println(q1event.route.fromArea.x + "." + q1event.route.fromArea.y + 
		// 	" " + q1event.route.toArea.x + "." + q1event.route.toArea.y);

		if(newevent.time_in != 0) {
			long currentms = newevent.dropoff_datetime.getTime();
	        if(sliding_window.size() != 0) {
	          lastevent = sliding_window.getFirst();
	          lastms = lastevent.dropoff_datetime.getTime();

	          // Remove the elements from the start of the window
	          while((currentms-lastms) >= Constants.WINDOW30_SIZE) {
			//System.out.println("HERE123");
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
	        maxfs.increaseFrequency(newevent.route, newevent.dropoff_datetime.getTime());
        	sliding_window.addLast(newevent);

        	if(!maxfs.isSameMaxTenKey()){
	        	String s = newevent.pickup_datetime.toString() + "," + newevent.dropoff_datetime.toString() + ",";
	          	s = s + maxfs.printMaxTen();
	          	collector.emit(new Values(s));
	        }
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("q1output"));
	}
}
