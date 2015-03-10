import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TenMaxProfitability {
	/*
	 * If number of empty taxies are zero,
	 * profitability is equal to profit
	 */
	class Profitability implements Comparable<Profitability> {
		public float profitability;
		public Mc mprofit;
		public int num_empty_taxis;
		public long ts;

		public Profitability() {
			mprofit = new Mc();
			num_empty_taxis = 0;
			profitability = 0.0f;
		}

		public Profitability(float p, int n, long t) {
			profitability = p;
			mprofit = new Mc();
			num_empty_taxis = n;
			ts = t;
		}

		public void resetProfitability() {
			if(this.mprofit.size() == 0) {
				this.profitability = 0;
			} else if(this.num_empty_taxis == 0) {
				this.profitability = this.mprofit.getMedian();
			} else {
				this.profitability = this.mprofit.getMedian()/this.num_empty_taxis;
			}
		}

		@Override
		public int compareTo(Profitability ptb) {
			if(ptb == this) {
				return 0;
			} if(this.num_empty_taxis == 0 && ptb.num_empty_taxis != 0) {
				return 1;
			} else if(this.num_empty_taxis != 0 && ptb.num_empty_taxis == 0) {
				return -1;
			} else if(this.profitability < ptb.profitability) {
				return -1;
			} else if(this.profitability > ptb.profitability) {
				return 1;
			} else if(this.ts < ptb.ts) {
				return -1;
			} else if(this.ts > ptb.ts) {
				return 1;
			} else {
				return 0;
			}
		}

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof Profitability))
				return false;

			if(obj == this) {
				return true;
			}

			Profitability p = (Profitability) obj;
			if(p.profitability == this.profitability &&
					p.num_empty_taxis == this.num_empty_taxis &&
					p.ts == this.ts) {
				return true;
			}

			return false;
		}
	}

	class ArrayMap {
		private int xSize;
		private int ySize;
		private Profitability[][] data;
		private int size;

		public ArrayMap(int xLimit, int yLimit) {
			this.xSize = xLimit;
			this.ySize = yLimit;
			this.data = new Profitability[this.xSize][this.ySize];
			this.size = 0;

			for(int i=0; i<this.xSize; i++) {
				for(int j=0; j<this.ySize; j++) {
					data[i][j] = new Profitability();
				}
			}
		}

		public boolean containsKey(Area a) {
			return (data[a.x][a.y] != null);
		}

		public void put(Area a, Profitability p) {
			if(data[a.x][a.y] == null) {
				this.size++;
			}

			data[a.x][a.y] = p;
		}

		public Profitability get(Area a) {
			return data[a.x][a.y];
		}

		public Profitability remove(Area a) {
			Profitability return_value = data[a.x][a.y];
			data[a.x][a.y] = null;
			this.size--;
			return return_value;
		}

		public int size() {
			return this.size;
		}
	}

	class TaxiInfo implements Comparable<TaxiInfo> {
		public Area area;
		public long ts;

		public TaxiInfo(Area a, long ts) {
			this.area = a;
			this.ts = ts;
		}

		@Override
		public int compareTo(TaxiInfo taxi) {
			if(this.ts < taxi.ts) {
				return -1;
			} else if(this.ts > taxi.ts) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	/*
	 * The comparison operator written the opposite,
	 * i.e if actual s1<s2 means s1>s2.
	 * Done so that Treeset can print in descending order
	 * Also note that 2 setElems are equal if the area is same
	 */
	class SetElem implements Comparable<SetElem> {
		public Area area;
		public long ts;
		public float profitability;

		public SetElem(Area a, long ts, float profitability) {
			this.area = a;
			this.ts = ts;
			this.profitability = profitability;
		}

		@Override
		public int compareTo(SetElem s) {
			if(this.area.equals(s.area)) {
				return 0;
			}else if(this.profitability < s.profitability) {
				return 1;
			} else if(this.profitability > s.profitability) {
				return -1;
			} else if(this.ts < s.ts) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	// Maps taxi identifier to (taxi area + ts). Used in the empty taxi algo
	private HashMap<String, TaxiInfo> grid_present;

	// Maps area to profitability
	private ArrayMap area_ptb_map;

	// the array DS
	private List<Set<SetElem>> sorted_ptb_list;

	// Stores previous top 10 areas
	private Area[] top10Area;

	// Stores previous top 10 median profit values
	private float[] top10medianpft;

	// Stores previous top 10 empty taxi values
	private float[] top10emptytaxi;

	public TenMaxProfitability() {
		area_ptb_map = new ArrayMap(Constants.AREA_LIMIT, Constants.AREA_LIMIT);
		grid_present = new HashMap<String, TaxiInfo>();
		sorted_ptb_list = new ArrayList<Set<SetElem>>(Constants.NUM_EMPTY_BUCKETS);
		for(int i=0; i<Constants.NUM_EMPTY_BUCKETS; i++) {
			sorted_ptb_list.add(i, new TreeSet<SetElem>());
		}
		top10Area = new Area[10];
		top10medianpft = new float[10];
		top10emptytaxi = new float[10];
		for(int i=0; i<10; i++) {
			top10Area[i] = null;
			top10medianpft[i] = -1;
			top10emptytaxi[i] = -1;
		}
	}

	public void printMaxTen(PrintStream print_stream) {
		int numPrinted = 0;
		int currentIndex = Constants.NUM_EMPTY_BUCKETS-1;
		while(numPrinted<10 && currentIndex>=0) {
			Iterator<SetElem> i = sorted_ptb_list.get(currentIndex).iterator();
			while(i.hasNext() && numPrinted<10) {
				SetElem s = i.next();
				Profitability p = area_ptb_map.get(s.area);
				if(p.num_empty_taxis == 0) continue;
				print_stream.print((s.area.x+1) + "." + (s.area.y+1) + "," +
						p.num_empty_taxis + "," +	p.mprofit.getMedian() + "," +
						p.profitability + ",");
				numPrinted++;
			}
			currentIndex--;
		}

		while(numPrinted < 10) {
			print_stream.print("NULL,");
			numPrinted++;
		}
	}

	public void storeMaxTenCopy() {
		int numPrinted = 0;
		int currentIndex = Constants.NUM_EMPTY_BUCKETS-1;
		while(numPrinted < 10 && currentIndex >= 0) {
			Iterator<SetElem> i = sorted_ptb_list.get(currentIndex).iterator();
			while(i.hasNext() && numPrinted < 10) {
				SetElem s = i.next();
				Profitability p = area_ptb_map.get(s.area);
				if(p.num_empty_taxis == 0) continue;
				top10Area[numPrinted] = s.area;
				top10medianpft[numPrinted] = p.mprofit.getMedian();
				top10emptytaxi[numPrinted] = p.num_empty_taxis;
				numPrinted++;
			}
			currentIndex--;
		}
	}

	public boolean isSameMaxTenKey() {
		int numPrinted = 0;
		int currentIndex = Constants.NUM_EMPTY_BUCKETS-1;
		while(numPrinted<10 && currentIndex>=0) {
			Iterator<SetElem> i = sorted_ptb_list.get(currentIndex).iterator();
			while(i.hasNext() && numPrinted<10) {
				SetElem s = i.next();
				Profitability p = area_ptb_map.get(s.area);
				if(p.num_empty_taxis == 0) continue;
				if(top10Area[numPrinted] == null ||
					!top10Area[numPrinted].equals(s.area) ||
					top10medianpft[numPrinted] != p.mprofit.getMedian() ||
					top10emptytaxi[numPrinted] != p.num_empty_taxis) {
					return false;
				}
				numPrinted++;
			}
			currentIndex--;
		}

		if(numPrinted<10 && top10Area[numPrinted] != null) {
			return false;
		}
		return true;
	}

	public void leaveTaxiSlidingWindow(String medallion_hack_license,
			long ts) {
		// Check if the event leaving corresponds to the event present in the area
		if(grid_present.containsKey(medallion_hack_license) &&
		    ts == grid_present.get(medallion_hack_license).ts) {
			// If present, then undo the effects of this event
			this.updateEmptyTaxi(grid_present.get(medallion_hack_license).area,-1,-1);
			grid_present.remove(medallion_hack_license);
		} else if(!grid_present.containsKey(medallion_hack_license)) {
			System.out.println("What the heck happpened to this cab!");
		}
	}

	public void enterTaxiSlidingWindow(String medallion_hack_license,
			Area a, long ts) {
		// This taxi was in consideration earlier
		// => has reached a new place within 30 mins
		if(grid_present.containsKey(medallion_hack_license)) {
			/*
			 * Remove this taxi from previous grid ->
			 * Change profitability to decrease empty taxi number corresponding to
			 * Area grid_present[medallion_hack_license].a
			 */
			this.updateEmptyTaxi(grid_present.get(medallion_hack_license).area,-1,-1);

			/*
			 * Add this taxi to the new destination grid ->
			 * Change profitability to increase empty taxi number
			 * corresponding to Area a
			 */
			this.updateEmptyTaxi(a,1,ts);

			// Update the area - TaxiInfo map
			grid_present.get(medallion_hack_license).area = a;
			grid_present.get(medallion_hack_license).ts = ts;
		}

		// This taxi was not in consideration earlier
		// => has reached a new place > 30 mins
		else {
			/*
			 * Add this taxi to the new destination grid ->
			 * Change profitability to increase empty taxi number
			 * corresponding to Area a
			 */
			this.updateEmptyTaxi(a,1,ts);
			grid_present.put(medallion_hack_license, new TaxiInfo(a,ts));
		}
	}

	/*
	 * Add diffTaxiNumber to Area a
	 * If ts==1, means the old timestamp has to be preserved.
	 * Else, update to timestamp ts
	 */
	public void updateEmptyTaxi(Area a, int diffTaxiNumber, long ts) {
		Profitability old_ptb_val = area_ptb_map.get(a);
		int old_index = (int) (old_ptb_val.profitability/Constants.BUCKET_SIZE);
		if(!sorted_ptb_list.get(old_index).remove(new SetElem(a,old_ptb_val.ts,old_ptb_val.profitability))) {
			//System.out.println("PAIN1");
		}
		old_ptb_val.num_empty_taxis += diffTaxiNumber;

		if(ts != -1) {
			old_ptb_val.ts = ts;
		}

		old_ptb_val.resetProfitability();

		// Next change the array DS
		int new_index = (int) (old_ptb_val.profitability/Constants.BUCKET_SIZE);
		sorted_ptb_list.get(new_index).add(new SetElem(a,old_ptb_val.ts,old_ptb_val.profitability));
	}

	public void leaveProfitSlidingWindow(Area a, float profit) {
		// First update the area-ptb map
		Profitability old_ptb_val = area_ptb_map.get(a);
		int old_index = (int) (old_ptb_val.profitability/Constants.BUCKET_SIZE);
		if(!sorted_ptb_list.get(old_index).remove(new SetElem(a,old_ptb_val.ts,old_ptb_val.profitability))) {
			//System.out.println("PAIN2");
		}
		old_ptb_val.mprofit.delete(profit);
		old_ptb_val.resetProfitability();

		// Next change the array DS
		int new_index = (int) (old_ptb_val.profitability/Constants.BUCKET_SIZE);
		sorted_ptb_list.get(new_index).add(new SetElem(a,old_ptb_val.ts,old_ptb_val.profitability));
	}

	public void enterProfitSlidingWindow(Area a, float profit, long ts) {
		// First update the area-ptb map
		Profitability old_ptb_val = area_ptb_map.get(a);
		int old_index = (int) (old_ptb_val.profitability/Constants.BUCKET_SIZE);
		if (!sorted_ptb_list.get(old_index).remove(new SetElem(a,old_ptb_val.ts,old_ptb_val.profitability))) {
			//System.out.println("PAIN3");
		}
		old_ptb_val.mprofit.insert(profit);
		old_ptb_val.ts = ts;
		old_ptb_val.resetProfitability();

		// Next change the array DS
		int new_index = (int) (old_ptb_val.profitability/Constants.BUCKET_SIZE);
		sorted_ptb_list.get(new_index).add(new SetElem(a,old_ptb_val.ts,old_ptb_val.profitability));
	}
}
