import java.util.ArrayList;
import java.util.List;

import uk.ac.imperial.lsds.seep.api.QueryBuilder;
import uk.ac.imperial.lsds.seep.api.QueryComposer;
import uk.ac.imperial.lsds.seep.api.QueryPlan;
import operators.*;
import uk.ac.imperial.lsds.seep.operator.Connectable;
import uk.ac.imperial.lsds.seep.state.StateWrapper;

public class Base implements QueryComposer {

	@Override
	public QueryPlan compose() {
		
		// Declare src
		List<String> srcFields = new ArrayList<String>();
		srcFields.add("pickup_datetime");
		srcFields.add("dropoff_datetime");
		srcFields.add("pickup_longitude");
		srcFields.add("pickup_latitude");
		srcFields.add("dropoff_longitude");
		srcFields.add("dropoff_latitude");
		
//		Connectable src = QueryBuilder.newStatelessSource(new Source(), 0, srcFields);
//		Connectable q1 = QueryBuilder.newStatelessOperator(new Q1Process(), 2, srcFields);

		List<String> src2Fields = new ArrayList<String>();
		src2Fields.add("medallion");
		src2Fields.add("hack_license");
		src2Fields.add("pickup_datetime");
		src2Fields.add("dropoff_datetime");
		src2Fields.add("pickup_longitude");
		src2Fields.add("pickup_latitude");
		src2Fields.add("dropoff_longitude");
		src2Fields.add("dropoff_latitude");
		src2Fields.add("fare_amount");
		src2Fields.add("tip_amount");

		Connectable src2 = QueryBuilder.newStatelessSource(new Source2(), 1, src2Fields);
		Connectable q2 = QueryBuilder.newStatelessOperator(new Q2Process(), 3, src2Fields);
		
		List<String> snkFields = new ArrayList<String>();
		snkFields.add("pickup_datetime");
		snkFields.add("dropoff_datetime");
		snkFields.add("top_ten");
		snkFields.add("delay");

		Connectable snk = QueryBuilder.newStatelessSink(new Sink(), 5, snkFields);

		//Connect operators
//		src.connectTo(q1, true, 0);
//		q1.connectTo(snk, true, 0);
		src2.connectTo(q2, true, 0);
		q2.connectTo(snk, true, 0);
		
		return QueryBuilder.build();
	}
}