import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.Set;

class Profitability implements Comparable<Profitability> {
  public Mc profit;
  public int num_empty_taxis;
  Timestamp ts;

  public Profitability() {
    profit = new Mc();
    num_empty_taxis = 0;
    ts = null;
  }

  public float getProfitability() {
    return this.profit.getMedian()/this.num_empty_taxis;
  }

  @Override
  public int compareTo(Profitability ptb) {
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

  public TenMaxProfitability() {
    key_val_map = new ArrayMap(AREA_LIMIT, AREA_LIMIT);
  }

  @Override
  public Profitability addTwoVals(Profitability v1, Profitability v2) {
    Profitability p = new Profitability();
    p.num_empty_taxis = v1.num_empty_taxis + v2.num_empty_taxis;
    p.ts = v2.ts;
    p.profit = v1.profit;
    return p;
  }

  @Override
  public boolean isZeroVal(Profitability v) {
    return v.ts==null;
  }
}
