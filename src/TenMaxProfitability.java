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
  }

  @Override
  public int compareTo(Profitability ptb) {
    float f1 = this.profit.getMedian()/this.num_empty_taxis;
    float f2 = ptb.profit.getMedian()/ptb.num_empty_taxis;
    if(f1 < f2) {
      return -1;
    } else if(f1 > f2) {
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
  private double[][] data;
  int size;

  public ArrayMap(int xLimit, int yLimit) {
    this.xSize = xLimit;
    this.ySize = yLimit;
    data = new double[xSize][ySize];
    size = 0;

    for(int i=0; i<xSize; i++)
      for(int j=0; j<ySize; j++)
        data[i][j] = 0;
  }

  public boolean containsKey(Area a) {
    return (data[a.x][a.y] != 0);
  }

  @Override
  public Profitability put(Area a, Profitability t) {
    return null;
  }

  public Profitability remove(Area a) {
    return null;
  }

  @Override
  public int size() {
    return 0;
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
    return null;
  }

  @Override
  public boolean isZeroVal(Profitability v) {
    return false;
  }
}
