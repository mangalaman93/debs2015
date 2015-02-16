import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.Set;
import java.util.HashMap;

/*
 * If number of empty taxies are zero,
 * profitability is equal to profit
 */
class Profitability implements Comparable<Profitability> {
  public float profitability;
  public Mc mprofit;
  public int num_empty_taxis;
  public Timestamp ts;

  public Profitability() {
    profitability = 0;
    mprofit = null;
    num_empty_taxis = 0;
    ts = null;
  }

  public Profitability(float p, int n, Timestamp t) {
    profitability = p;
    mprofit = null;
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
    } else if(this.ts.compareTo(ptb.ts) < 0) {
      return -1;
    } else if(this.ts.compareTo(ptb.ts) > 0) {
      return 1;
    } else {
      return 0;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof Profitability))
      return false;

    if(obj == this)
      return true;

    Profitability p = (Profitability) obj;
    if(p.profitability==this.profitability &&
        p.num_empty_taxis==this.num_empty_taxis &&
        p.ts==this.ts) {
      return true;
    }

    return false;
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

  @Override
  public boolean containsKey(Object obj) {
    Area a = (Area) obj;
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

class TaxiInfo implements Comparable<TaxiInfo> {
  public Area area;
  public Timestamp ts;

  public TaxiInfo(Area a, Timestamp ts) {
    this.area = a;
    this.ts = ts;
  }

  @Override
  public int compareTo(TaxiInfo taxi) {
    return this.ts.compareTo(taxi.ts);
  }
}

public class TenMaxProfitability extends TenMax<Area, Profitability> {
  // constants and parameters
  private final int AREA_LIMIT = 600;
  private HashMap<String, TaxiInfo> grid_present;

  public TenMaxProfitability() {
    key_val_map = new ArrayMap(AREA_LIMIT, AREA_LIMIT);
    grid_present = new HashMap<String, TaxiInfo>();
  }

  @Override
  public boolean isZeroVal(Profitability v) {
    return v.mprofit.size()==0;
  }

  @Override
  public Profitability addDiffToVal(Profitability v1, Profitability diff) {
    Profitability p = new Profitability();

    if(v1 == null) {
      p.mprofit = new Mc();
      p.num_empty_taxis = diff.num_empty_taxis;
      p.profitability = diff.profitability;
      p.ts = diff.ts;
    } else {
      p.mprofit = v1.mprofit;
      p.num_empty_taxis = v1.num_empty_taxis + diff.num_empty_taxis;

      if(diff.profitability > 0) {
        v1.mprofit.insert(diff.profitability);
        p.ts = diff.ts;
        p.resetProfitability();
      } else {
        if(diff.num_empty_taxis > 0) {
          p.ts = diff.ts;
        } else {
          p.ts = v1.ts;
        }

        if(diff.profitability < 0) {
          v1.mprofit.delete(-diff.profitability);
        }

        p.resetProfitability();
      }
    }

    return p;
  }

  public void leaveTaxiSlidingWindow(String medallion, String hack_license, Timestamp ts) {
    String searchKey = medallion + hack_license;

    // Check if the event leaving corresponds to the event present in the area - taxiInfo hashmap
    if(ts.equals(grid_present.get(searchKey).ts)) {
      // If present, then undo the effects of this event
      Profitability diff = new Profitability();
      diff.num_empty_taxis = -1;
      diff.ts = ts;
      update(grid_present.get(searchKey).area, diff);
      grid_present.remove(searchKey);
    }
  }

  public void enterTaxiSlidingWindow(String medallion, String hack_license, Area a, Timestamp ts) {
    String search_key = medallion + hack_license;

    // This taxi was in consideration earlier -> has reached a new place within 30 mins
    if(grid_present.containsKey(search_key)) {
      /*
       * Remove this taxi from previous grid ->
       * Change profitability to decrease empty taxi number corresponding to Area grid_present[searchKey].a
       */
      Profitability diff1 = new Profitability();
      diff1.num_empty_taxis = -1;
      this.update(grid_present.get(search_key).area, diff1);

      /*
       * Add this taxi to the new destination grid ->
       * Change profitability to increase empty taxi number corresponding to Area a
       */
      Profitability diff2 = new Profitability();
      diff2.num_empty_taxis = 1;
      diff2.ts = ts;
      this.update(a, diff2);

      // Update the area - TaxiInfo map
      grid_present.get(search_key).area = a;
      grid_present.get(search_key).ts = ts;
    }

    // This taxi was not in consideration earlier -> has reached a new place > 30 mins
    else {
      /*
       * Add this taxi to the new destination grid ->
       * Change profitability to increase empty taxi number corresponding to Area a
       */
      grid_present.put(search_key, new TaxiInfo(a, ts));
      Profitability diff = new Profitability();
      diff.num_empty_taxis = 1;
      diff.ts = ts;
      this.update(a, diff);
    }
  }

  public void leaveProfitSlidingWindow(Area a, float profit) {
    Profitability ptb = new Profitability();
    ptb.profitability = profit;
    this.update(a, ptb);
  }

  public void enterProfitSlidingWindow(Area a, float profit, Timestamp ts) {
    Profitability ptb = new Profitability();
    ptb.profitability = profit;
    ptb.ts = ts;
    this.update(a, ptb);
  }
}

