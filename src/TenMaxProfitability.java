import java.rmi.UnexpectedException;
import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Set;

class Profitability implements Comparable<Profitability> {
  public Mc mprofit;
  public int num_empty_taxis;
  public Timestamp ts;

  public Profitability() {
    mprofit = new Mc();
    num_empty_taxis = 0;
    ts = null;
  }

  public float getProfitability() {
    return this.mprofit.getMedian()/this.num_empty_taxis;
  }

  @Override
  public int compareTo(Profitability ptb) {
    if(this.num_empty_taxis == 0 && ptb.num_empty_taxis == 0) {
      if(this.mprofit.getMedian() > ptb.mprofit.getMedian()) {
        return 1;
      } else if(this.mprofit.getMedian() < ptb.mprofit.getMedian()) {
        return -1;
      } else {
        return 0;
      }
    } else if(this.num_empty_taxis == 0) {
      return 1;
    } else if(ptb.num_empty_taxis == 0) {
      return -1;
    } else {
      float p1 = this.getProfitability();
      float p2 = ptb.getProfitability();

      if(p1 < p2) {
        return -1;
      } else if(p1 > p2) {
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
}

class DiffProfitability {
  public float profit;
  public int num_empty_taxis;
  public Timestamp ts;

  public DiffProfitability(float p, int taxis, Timestamp t) {
    profit = p;
    num_empty_taxis = taxis;
    ts = t;
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
    data = new Profitability[xSize][ySize];
    size = 0;

    for(int i=0; i<xSize; i++)
      for(int j=0; j<ySize; j++)
        data[i][j] = null;
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

public class TenMaxProfitability extends TenMax<Area, Profitability, DiffProfitability> {
  // constants and parameters
  private final int AREA_LIMIT = 600;

  public TenMaxProfitability() {
    key_val_map = new ArrayMap(AREA_LIMIT, AREA_LIMIT);
  }

  public void leaveProfitSlidingWindow(Area a, float profit, Timestamp ts) {
    key_val_map.get(a).profit.delete(profit);
    Profitability diff = new Profitability();
    diff.num_empty_taxis = 0;
    diff.profit = null;
    diff.ts = ts;
    this.update(a, diff);
  }

  public boolean enterProfitSlidingWindow(Q2Elem event) {
    return true;
  }
  
  public boolean leaveTaxiSlidingWindow() {
    return false;
  }

  public boolean enterTaxiSlidingWindow() {
    return true;
  }

  @Override
  public boolean isZeroVal(Profitability v) {
    return v.mprofit.size()==0;
  }

  @Override
  public Profitability addDiffToVal(Profitability v1, DiffProfitability diff) {
    Profitability p = new Profitability();
    p.num_empty_taxis = v1.num_empty_taxis + diff.num_empty_taxis;
    p.ts = diff.ts;
    if(diff.profit < 0) {
      
    }

    return p;
  }
}
