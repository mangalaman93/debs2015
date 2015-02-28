import java.util.HashMap;

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

    public Profitability(float p, int n, long t) {
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

      if(obj == this)
        return true;

      Profitability p = (Profitability) obj;
      if(p.profitability == this.profitability &&
          p.num_empty_taxis == this.num_empty_taxis &&
          p.ts == this.ts) {
        return true;
      }

      return false;
    }
  }

  final class PairQ2 {
    public Area area;
    public Profitability pft;

    public PairQ2(Area a, Profitability p) {
      this.area = a;
      this.pft = p;
    }

    @Override
    public boolean equals(Object obj) {
      if(!(obj instanceof PairQ2))
        return false;

      if(obj == this)
        return true;

      PairQ2 pair = (PairQ2) obj;
      if(this.area.equals(pair.area) && this.pft.equals(pair.pft))
        return true;

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
          data[i][j] = null;
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

  private final int AREA_LIMIT = 600;
  private HashMap<String, TaxiInfo> grid_present;
  private ArrayMap area_pft_map;

  public TenMaxProfitability() {
    area_pft_map = new ArrayMap(AREA_LIMIT, AREA_LIMIT);
    grid_present = new HashMap<String, TaxiInfo>();
  }

  public void printMaxTen() {
    // TODO
  }

  public void storeMaxTenCopy() {
    // TODO
  }

  public boolean isSameMaxTenKey() {
    // TODO
  }

  public void leaveTaxiSlidingWindow(String medallion, String hack_license,
      long ts) {
    String searchKey = medallion + hack_license;

    // Check if the event leaving corresponds to the event present in the area
    if(ts == grid_present.get(searchKey).ts) {
      // TODO If present, then undo the effects of this event
    }
  }

  public void enterTaxiSlidingWindow(String medallion, String hack_license,
      Area a, long ts) {
    String search_key = medallion + hack_license;

    // This taxi was in consideration earlier
    // => has reached a new place within 30 mins
    if(grid_present.containsKey(search_key)) {
      /*
       * TODO Remove this taxi from previous grid ->
       * Change profitability to decrease empty taxi number corresponding to
       * Area grid_present[searchKey].a
       */

      /*
       * TODO Add this taxi to the new destination grid ->
       * Change profitability to increase empty taxi number
       * corresponding to Area a
       */

      // Update the area - TaxiInfo map
      grid_present.get(search_key).area = a;
      grid_present.get(search_key).ts = ts;
    }

    // This taxi was not in consideration earlier
    // => has reached a new place > 30 mins
    else {
      /*
       * TODO Add this taxi to the new destination grid ->
       * Change profitability to increase empty taxi number
       * corresponding to Area a
       */
      grid_present.put(search_key, new TaxiInfo(a, ts));
    }
  }

  public void leaveProfitSlidingWindow(Area a, float profit) {
    // TODO
  }

  public void enterProfitSlidingWindow(Area a, float profit, long ts) {
    // TODO
  }
}
