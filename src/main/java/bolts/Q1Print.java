package bolts;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

public class Q1Print extends BaseBasicBolt {

	@Override
	public void cleanup() {
		System.out.println("OVER");
	}

	/**
	 * On create 
	 */
	@Override
	public void prepare(Map stormConf, TopologyContext context) {}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {}


	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String str = input.getString(0);
	//	System.out.println(str);
	}
}
