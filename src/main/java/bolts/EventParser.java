package bolts;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import java.sql.Timestamp;
import java.util.Map;
import utils.*;
import java.util.StringTokenizer;

class Q1Elem {
  public Timestamp pickup_datetime;
  public Timestamp dropoff_datetime;
  public Route route;
  public long time_in;
}

public class EventParser extends BaseBasicBolt {

	private Geo geo;

	public void cleanup() {}

	public void prepare(Map stormConf, TopologyContext context) {
	    this.geo = new Geo(-74.913585f, 41.474937f, 500, 500, 300, 300);
	}

	/**
	 * The bolt will receive the line from the
	 * words file and process it to Normalize this line
	 * 
	 * The normalize will be put the words in lower case
	 * and split the line to get all words in this 
	 */
	public void execute(Tuple input, BasicOutputCollector collector) {
		String line = input.getString(0);
		boolean valid = true;
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
		from = geo.translate(Float.parseFloat(st.nextToken()),
	    Float.parseFloat(st.nextToken()));
	    if(from == null) {
	    	valid = false;
	    }
	    to = geo.translate(Float.parseFloat(st.nextToken()),
	    Float.parseFloat(st.nextToken()));
	    if(to == null) {
	    	valid = false;
	    }
	    if(valid) {
	    	q1event.route = new Route(from, to);
	    	collector.emit(new Values(q1event));
	    }
	    else {
	    	//System.out.println("INVALID");
	    }
        // String sentence = input.getString(0);
        // String[] words = sentence.split(" ");
        // for(String word : words){
        //     word = word.trim();
        //     if(!word.isEmpty()){
        //         word = word.toLowerCase();
        //         collector.emit(new Values(word));
        //     }
        // }
	}
	

	/**
	 * The bolt will only emit the field "word" 
	 */
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("q1event"));
	}
}
