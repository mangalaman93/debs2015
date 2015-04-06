package spouts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import utils.*;
import java.util.StringTokenizer;
import java.sql.Timestamp;

/*
class Q1Elem {
  public Timestamp pickup_datetime;
  public Timestamp dropoff_datetime;
  public Route route;
  public long time_in;
}
*/

public class Reader extends BaseRichSpout {
	BufferedReader reader;
	Geo geoq1;
	Geo geoq2;	
	int in;
	int id;
	private SpoutOutputCollector collector;
	private FileReader fileReader;
	private boolean completed = false;
	///*
	public void ack(Object msgId) {
		//System.out.println("OK:"+msgId);
		//System.out.println("OK:");
	}

	public void fail(Object msgId) {
		System.out.println("FAIL:"+msgId);
	}
	//*/
	public void close() {}

	/**
	 * The only thing that the methods will do It is emit each 
	 * file line
	 */
	public void nextTuple() {
		if(completed){
	        try {
			//completed = false;
	                Thread.sleep(1000);
	        } catch (InterruptedException e) {
	                //Do nothing
	        }
		System.out.println("HHHH");
	        //System.exit(0);
	        //return;
	    }
		
		String line;
		//Open the reader
		//BufferedReader reader = new BufferedReader(fileReader);
		try{
			//Read all lines
			if((line = reader.readLine()) != null){
				/**
				 * By each line emmit a new value with the line as a their
				 */
                		in++;
                		//System.out.println("parse " + in);
                		Area from, to;
                		Q1Elem q1event = new Q1Elem();
                		q1event.time_in = System.currentTimeMillis();
                		StringTokenizer st = new StringTokenizer(line, ",");
                		st.nextToken();
                		st.nextToken();
                		q1event.pickup_datetime = new java.sql.Timestamp(Constants.parseDate(st.nextToken()));
                		q1event.dropoff_datetime = new java.sql.Timestamp(Constants.parseDate(st.nextToken()));
                		st.nextToken();
                		st.nextToken();
                		from = geoq1.translate(Float.parseFloat(st.nextToken()),Float.parseFloat(st.nextToken()));
				if(from == null) return;
            			to = geoq1.translate(Float.parseFloat(st.nextToken()),Float.parseFloat(st.nextToken()));
            			if(to == null) return;
            			q1event.route = new Route(from, to);
                		collector.emit("stream1",new Values(q1event));

				
				Q2Elem q2event = new Q2Elem();
				q2event.time_in = System.currentTimeMillis();
				st = new StringTokenizer(line, ",");
				q2event.medallion_hack_license = st.nextToken()+st.nextToken();
				q2event.pickup_datetime = new java.sql.Timestamp(Constants.parseDate(st.nextToken()));
				q2event.dropoff_datetime = new java.sql.Timestamp(Constants.parseDate(st.nextToken()));
				st.nextToken();
				st.nextToken();
				q2event.pickup_area = geoq2.translate(Float.parseFloat(st.nextToken()),Float.parseFloat(st.nextToken()));
				if(q2event.pickup_area==null) return;
				q2event.dropoff_area = geoq2.translate(Float.parseFloat(st.nextToken()),Float.parseFloat(st.nextToken()));
				if(q2event.dropoff_area==null) return;
				st.nextToken();
				q2event.total_fare = Float.parseFloat(st.nextToken());
				if(q2event.total_fare < 0) return;
				st.nextToken();
				st.nextToken();
				q2event.total_fare += Float.parseFloat(st.nextToken());
				q2event.id = id++;
				collector.emit("stream2",new Values(q2event));	
			}
			else {
				Thread.sleep(1000);
				System.exit(0);
			}
		} catch(Exception e){
			//throw new RuntimeException("Error reading tuple",e);
		} /*finally {
			completed = true;
		}
		*/
	}

	/**
	 * We will create the file and get the collector object
	 */
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		try {
			this.fileReader = new FileReader(conf.get("wordsFile").toString());
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error reading file ["+conf.get("wordFile")+"]");
		}
		this.collector = collector;
		this.completed = false;
		reader = new BufferedReader(fileReader);
		this.in = 0;
		this.id = 0;
		this.geoq1 = new Geo(-74.913585f, 41.474937f, 500, 500, 300, 300);
		this.geoq2 = new Geo(-74.913585f, 41.474937f, 250, 250, 600, 600);
	}

	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		//declarer.declare(new Fields("q1event"));
		outputFieldsDeclarer.declareStream("stream1", new Fields("q1event"));
		outputFieldsDeclarer.declareStream("stream2", new Fields("q2event"));
	}
}
