package operators;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

import uk.ac.imperial.lsds.seep.comm.serialization.DataTuple;
import uk.ac.imperial.lsds.seep.comm.serialization.messages.TuplePayload;
import uk.ac.imperial.lsds.seep.operator.StatelessOperator;

public class Source implements StatelessOperator {

	private static final long serialVersionUID = 1L;

	/*
	 * Get the information on the structure of processed tuples
	 */
	private DataTuple dataTupleStructure;
	
	/*
	 * Members needed for parsing the data block-wise
	 */
	public final static String[] KEYS = "medallion,hack_license,pickup_datetime,dropoff_datetime,trip_time_in_secs,trip_distance,pickup_longitude,pickup_latitude,dropoff_longitude,dropoff_latitude,payment_type,fare_amount,surcharge,mta_tax,tip_amount,tolls_amount,total_amount".split(",");
	public final static String[] TYPES = "String,String,String,String,Integer,Float,Float,Float,Float,Float,String,Float,Float,Float,Float,Float,Float".split(",");
	public final static Boolean[] REQUIRED = {false,false,true,true,false,false,true,true,true,true,false,false,false,false,false,false,false};
	public final static int outputFieldCount = 6;
	private BufferedReader reader = null;
	private int blockSize = -1;
	private List<DataTuple> block = new ArrayList<DataTuple>();
	
	// data is now a field, so that it is accessible from outside processData()
	DataTuple data;
	
	int bufferSize = 5000000;
	
	@Override
	public void processData(DataTuple arg0) {
		//tuple schema stuff
		Map<String, Integer> mapper = api.getDataMapper();
		data = new DataTuple(mapper, new TuplePayload());
		
		//time control stuff
		int c = 0;
		long init = System.currentTimeMillis();
		
		while(true){
			c++;

			/** 
			 * READ FROM DISK
			 */
			try {
				DataTuple toSend = readNext();
				if(toSend != null){
					toSend.getPayload().instrumentation_ts = System.currentTimeMillis();
					api.send(toSend);
				} else {
					c--;
					try {
					    Thread.sleep(1000);
					} catch(InterruptedException ex) {
					    Thread.currentThread().interrupt();
					}
					System.out.println("SRC + DONE");
					return;
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			
			//TIMING
			if((System.currentTimeMillis() - init) > 1000){
				System.out.println("SRC "+c+" ");
				c = 0;
				init = System.currentTimeMillis();
			}
		}
	}

	public DataTuple readNext() throws Exception {

		if (blockSize <= 0) {
			return readItem();
		}

		if (block.isEmpty()) {
			int read = readBlock();
			if (read == 0)
				return null;
		}

		DataTuple item = block.remove(0);
		return item;
	}
	

	protected int readBlock() throws Exception {
		block.clear();
		int read = 0;
		for (int i = 0; i < blockSize; i++) {
			DataTuple item = readItem();
			if (item != null) {
				block.add(item);
				read++;
			} else
				return read;
		}
		return read;
	}

	protected DataTuple readItem() throws Exception {
//		System.out.println("readItem()");
		if (dataTupleStructure == null){
			System.out.println("dataTupleStructure is null");
			return null;
		}
		
		String line = reader.readLine();
//		System.out.println(line);
		if (line == null)
			return null;

		Object[] values = new Object[outputFieldCount];
		String[] fields = line.split(",");

		int i = 0, j=0;
		int len = line.length();
		int max = len - 1;
		int floating = 0;
		int sign = 1;

		Long longVal = 0L;
		Float floatVal = 0f;

		for (int c = 0; c < len; c++) {
			char ch = line.charAt(c);
			if (ch == ',') {
				if (!REQUIRED[i]) {
					i++;
					continue;
				}
				switch (TYPES[i]) {
//					case "Integer":
//						values[j] = new Integer(((int)longVal)	); break;
					case "Long":
						values[j] = sign * new Long(longVal); break;
					case "Float":
						values[j] = sign * new Float(longVal + floatVal / floating); break;
					case "String":
						values[j] = fields[i]; break;
				}
				
				longVal = 0L;
				floatVal = 0f;
				floating = 0;
				sign = 1;
				i++;
				j++;

				continue;
			}
			if (!REQUIRED[i] || TYPES[i].compareTo("String") == 0) continue;
			if (ch == '.') {
				floating = 1;
				continue;
			}
			if (ch == '-') {
				sign = -1;
				continue;
			}

			if (floating > 0) {
				floatVal *= 10;
				floatVal += (ch - '0');
				floating *= 10;
			}
			else {
				longVal *= 10;
				longVal += (ch - '0');
			}

			if (c == max) {
				switch (TYPES[i]) {
//					case "Integer":
//						values[j] = new Integer(((int)longVal)	); break;
					case "Long":
						values[j] = sign * new Long(longVal); break;
					case "Float":
						values[j] = sign * new Float(longVal + floatVal / floating); break;
					case "String":
						values[j] = fields[i]; break;
				}
				DataTuple output = data.newTuple(values);
				return output;
			}
		}
		DataTuple output = data.newTuple(values);
		return output;
	}

	@Override
	public void processData(List<DataTuple> arg0) { }
	
	@Override
	public void setUp() {
		String filePath = null; 
		int opId = api.getOperatorId();
		if(opId == 0){
			filePath = "file:///houses0-4.csv";
		}
		else if(opId == 11){
			filePath = "file:///houses5-9.csv";
		}
		else if(opId == 12){
			filePath = "file:///houses10-14.csv";
		}
		else if(opId == 13){
			filePath = "file:///houses15-19.csv";
		}
		filePath = "file:///Users/arunmathew/Documents/debs2015/out/sorted_data.csv";
		URL url;
		try {
			url = new URL(filePath);
			setUp(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Object hardCodedCast(int i, String value) {
		switch (TYPES[i]) {
		case "Integer":
			return new Integer(value);
		case "Long":
			return new Long(value);
		case "Float":
			return new Float(value);
		default:
			return value;
		}
	}

	public void setUp(URL url) throws IOException {
		this.reader = new BufferedReader(new InputStreamReader(url.openStream()));
		if (blockSize > 0) {
			this.block = new ArrayList<DataTuple>(blockSize);
		}
		
		Map<String, Integer> mapper = api.getDataMapper();
		this.dataTupleStructure = new DataTuple(mapper, new TuplePayload());
	}

	public int getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	
	class BufferedMemoryMapInputStreamAdapter extends BufferedInputStream{
		public BufferedMemoryMapInputStreamAdapter(InputStream in) {
			super(in);
		}
	}
	
	class MemoryMapInputStreamAdapter extends InputStream{
		MappedByteBuffer b = null;
		public MemoryMapInputStreamAdapter(MappedByteBuffer b){
			this.b = b;
		}

		@Override
		public int read() throws IOException {
			return b.get();
		}
	}
}