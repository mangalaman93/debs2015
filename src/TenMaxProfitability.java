import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

/*
 * If number of empty taxies are zero,
 * profitability is equal to profit
 */
class Profitability implements Comparable<Profitability> {
  public Mc mprofit;
  public float profitability;
  public int num_empty_taxis;
  public Timestamp ts;

  public Profitability() {
    mprofit = null;
    profitability = 0;
    num_empty_taxis = 0;
    ts = null;
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
    } else if(this.ts.compareTo(ptb.ts) < 0) {
      return -1;
    } else if(this.ts.compareTo(ptb.ts) > 0) {
      return 1;
    } else {
      return 0;
    }
  }
}

class ArrayMap extends AbstractMap<Area, Profitability> {
  private int xSize;
  private int ySize;
  private Profitability[][] data;
  int size;

  public ArrayMap(int xLimit, int yLimit) {
    this.xSize = xLimit;
    this.ySize = yLimit;
    data = new Profitability[this.xSize][this.ySize];
    size = 0;

    for(int i=0; i<xSize; i++) {
      for(int j=0; j<ySize; j++) {
        data[i][j] = null;
      }
    }
  }

  public boolean containsKey(Area a) {
    return (data[a.x][a.y] != null);
  }

  @Override
  public Profitability put(Area a, Profitability p) {
    Profitability return_value;
    if(data[a.x][a.y] != null) {
      return_value = data[a.x][a.y];
    } else {
      this.size++;
      return_value = null;
    }

    data[a.x][a.y] = p;
    return return_value;
  }

  @Override
  public Profitability get(Object obj) {
    if(!(obj instanceof Area))
      return null;

    Area a = (Area) obj;
    return data[a.x][a.y];
  }

  public Profitability remove(Area a) {
    Profitability return_value = data[a.x][a.y];
    data[a.x][a.y] = null;
    this.size--;
    return return_value;
  }

  @Override
  public int size() {
    return this.size;
  }

  @Override
  public Set<java.util.Map.Entry<Area, Profitability>> entrySet() {
    throw new UnsupportedOperationException("NOT CALLED ANYWHERE");
  }
}

public class TenMaxProfitability extends TenMax<Area, Profitability> {
  // constants and parameters
  private final int AREA_LIMIT = 600;
  private Map<String, taxiInfo> grid_present;

  public TenMaxProfitability() {
    key_val_map = new ArrayMap(AREA_LIMIT, AREA_LIMIT);
    Map grid_present = new HashMap();
  }

  public void leaveProfitSlidingWindow(Area a, float profit, Timestamp ts) {
  }

  public boolean enterProfitSlidingWindow(Q2Elem event) {
    return true;
  }

  public boolean leaveTaxiSlidingWindow(String medallion, String hack_license, Timestamp ts) {
	  String searchKey = medallion + hack_license;
	  
	  // Check if the event leaving corresponds to the event present in the area - taxiInfo hashmap
	  if(ts.equals(grid_present.get(searchKey).ts)) {
		  // If present, then undo the effects of this event
		  Profitability diff = new Profitability();
		  diff.num_empty_taxis = -1;
		  diff.ts = ts;
		  update(grid_present.get(searchKey).a,diff);
		  grid_present.remove(searchKey);
	  }
	  // If not present, nothing to do
	  return false;
  }

  public boolean enterTaxiSlidingWindow(String medallion, String hack_license, Area a, Timestamp ts) {
	String searchKey = medallion + hack_license;
	
	// This taxi was in consideration earlier -> has reached a new place within 30 mins
	if(grid_present.containsKey(searchKey)) {
		/*
		 * Remove this taxi from previous grid -> 
		 * Change profitability to decrease empty taxi number corresponding to Area grid_present[searchKey].a
		*/
		Profitability diff1 = new Profitability();
		diff1.num_empty_taxis = -1;
		diff1.ts = ts;
		update(grid_present.get(searchKey).a,diff1);
		/*
		 * Add this taxi to the new destination grid -> 
		 * Change profitability to increase empty taxi number corresponding to Area a
		*/
		Profitability diff2 = new Profitability();
		diff2.num_empty_taxis = 1;
		diff2.ts = ts;
		update(a,diff2);
		// Update the area - taxiInfo map
		grid_present.get(searchKey).a = a;
		grid_present.get(searchKey).ts = ts;		
	}
	
	// This taxi was not in consideration earlier -> has reached a new place > 30 mins
	else {
		/*
		 * Add this taxi to the new destination grid -> 
		 * Change profitability to increase empty taxi number corresponding to Area a
		*/
		grid_present.put(searchKey, new taxiInfo(a,ts));
		Profitability diff = new Profitability();
		diff.num_empty_taxis = 1;
		diff.ts = ts;
		update(a,diff);
	}
    return true;
  }

  @Override
  public boolean isZeroVal(Profitability v) {
    return v.profitability==0;
  }

  @Override
  public Profitability addDiffToVal(Profitability v1, Profitability diff) {
    Profitability p = new Profitability();
    p.mprofit = v1.mprofit;
    p.num_empty_taxis = v1.num_empty_taxis + diff.num_empty_taxis;
    p.ts = diff.ts;
    if(diff.profitability < 0) {
      v1.mprofit.delete(-diff.profitability);
      p.profitability = p.mprofit.getMedian();
    } else {
      v1.mprofit.insert(diff.profitability);
      p.profitability = p.mprofit.getMedian();
    }

    return p;
  }
}

class taxiInfo implements Comparable<taxiInfo> {
  public Area a;
  public Timestamp ts;

  public taxiInfo(Area a, Timestamp ts) {
    this.a = a;
    this.ts = ts;
  }

  @Override
  public int compareTo(taxiInfo tax) {
    if(tax == this) {
      return 0;
    } if(this.ts == tax.ts) {
      return 0;
    } else {
      return 1;
    }
  }
}