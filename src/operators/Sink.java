package operators;

import java.util.List;

import uk.ac.imperial.lsds.seep.comm.serialization.DataTuple;
import uk.ac.imperial.lsds.seep.operator.StatelessOperator;

public class Sink implements StatelessOperator{

	private static final long serialVersionUID = 1L;

	//time control stuff
	
	@Override
	public void processData(DataTuple data) {
		
		System.out.println(data.getString("pickup_datetime") + ","
				+ data.getString("dropoff_datetime") + "," 
				+ data.getString("top_ten")
				+ data.getLong("delay"));
		System.err.println(data.getString("from_process") + "," +  data.getLong("delay"));
	}

	@Override
	public void processData(List<DataTuple> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUp() {
		// TODO Auto-generated method stub
		
	}

}
