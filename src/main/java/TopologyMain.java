import spouts.Reader;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
//import bolts.EventParser;
import bolts.*;
import utils.Area;


public class TopologyMain {
	public static void main(String[] args) throws InterruptedException {
         
        //Topology definition
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("file-reader",new Reader());
		/*
		builder.setBolt("event-parser", new EventParser())
			.shuffleGrouping("file-reader");
		*/
		builder.setBolt("q1-process", new Q1Process())
			.shuffleGrouping("file-reader","stream1");
		
		builder.setBolt("q1-print", new Q1Print())
			.shuffleGrouping("q1-process");

		builder.setBolt("q2-process", new Q2Process())
                        .shuffleGrouping("file-reader","stream2");

                builder.setBolt("q2-print", new Q2Print())
                        .shuffleGrouping("q2-process");


		
        //Configuration
		Config conf = new Config();
		conf.put("wordsFile", args[0]);
		conf.setDebug(false);
        //Topology run
		//conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("Getting-Started-Toplogie", conf, builder.createTopology());
		//Thread.sleep(200000);
		//cluster.shutdown();
	}
}
