package bolts;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

public class Q2Print extends BaseBasicBolt {
	
	String prev_str;

	@Override
	public void cleanup() {
		System.out.println("OVER");
	}

	/**
	 * On create 
	 */
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		prev_str = "";
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {}


	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String str = input.getString(0);
		long ti = input.getLong(1);
		if(!prev_str.equals(str)) {
			prev_str = str;
			System.out.println(str + (System.currentTimeMillis()-ti) );
		}
	}
}
