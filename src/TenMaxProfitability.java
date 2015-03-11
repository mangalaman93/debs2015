import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TenMaxProfitability {
  class TaxiInfo implements Comparable<TaxiInfo> {
    public Area area;
    public long ts;

    public TaxiInfo(Area a, long ts) {
      this.area = a;
      this.ts = ts;
    }

    @Override
    public int compareTo(TaxiInfo taxi) {
      throw new UnsupportedOperationException();
    }

    public boolean equals(TaxiInfo taxi) {
      throw new UnsupportedOperationException();
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
    public Mc mprofit;
    public int num_empty_taxis;
    public float profitability;
    public long ts;

    public SetElem(Area a, int n, long t) {
      this.area = a;
      this.mprofit = new Mc();
      this.num_empty_taxis = n;
      this.profitability = 0;
      this.ts = t;
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

    /* reverse compareTO */
    @Override
    public int compareTo(SetElem elem) {
      if(this == elem || this.area.equals(elem.area)) {
        return 0;
      } else if(this.num_empty_taxis == 0 && elem.num_empty_taxis != 0) {
        return -1;
      } else if(this.num_empty_taxis != 0 && elem.num_empty_taxis == 0) {
        return 1;
      } else if(this.profitability < elem.profitability) {
        return 1;
      } else if(this.profitability > elem.profitability) {
        return -1;
      } else if(this.ts < elem.ts) {
        return 1;
      } else if(this.ts > elem.ts) {
        return -1;
      } else {
        return 0;
      }
    }

    @Override
    public boolean equals(Object obj) {
      if(!(obj instanceof SetElem)) {
        return false;
      }

      if(obj == this) {
        return true;
      }

      SetElem elem = (SetElem) obj;
      if(elem.area.equals(this.area)) {
        return true;
      }

      return false;
    }
  }

  class ArrayMap {
    private int xSize;
    private int ySize;
    private SetElem[][] data;

    public ArrayMap(int xLimit, int yLimit) {
      this.xSize = xLimit;
      this.ySize = yLimit;
      this.data = new SetElem[this.xSize][this.ySize];

      for(int i=0; i<this.xSize; i++) {
        for(int j=0; j<this.ySize; j++) {
          data[i][j] = new SetElem(null, 0, -1);
        }
      }
    }

    public boolean containsKey(Area a) {
      return (data[a.x][a.y].area != null);
    }

    public void put(Area a, SetElem elem) {
      data[a.x][a.y] = elem;
    }

    public SetElem get(Area a) {
      return data[a.x][a.y];
    }

    public void remove(Area a) {
      data[a.x][a.y].area = null;
    }
  }

  // Maps taxi identifier to (taxi area + ts). Used in the empty taxi algo
  private HashMap<String, TaxiInfo> grid_present;
  // Maps area to profitability
  private ArrayMap area_elem_map;
  // the array DS
  private List<Set<SetElem>> sorted_ptb_list;
  // Stores previous top 10 areas
  private Area[] top10_area;
  // Stores previous top 10 median profit values
  private float[] top10_medianpft;
  // Stores previous top 10 empty taxi values
  private float[] top10_empty_taxi;

  public TenMaxProfitability() {
    area_elem_map = new ArrayMap(Constants.AREA_LIMIT, Constants.AREA_LIMIT);
    grid_present = new HashMap<String, TaxiInfo>();
    sorted_ptb_list = new ArrayList<Set<SetElem>>(Constants.NUM_EMPTY_BUCKETS);
    for(int i=0; i<Constants.NUM_EMPTY_BUCKETS; i++) {
      sorted_ptb_list.add(i, new TreeSet<SetElem>());
    }

    top10_area = new Area[10];
    top10_medianpft = new float[10];
    top10_empty_taxi = new float[10];
    for(int i=0; i<10; i++) {
      top10_area[i] = null;
      top10_medianpft[i] = -1;
      top10_empty_taxi[i] = -1;
    }
  }

  public void enterTaxiSlidingWindow(String medallion_hack_license,
      Area a, long ts) {
    TaxiInfo taxi = grid_present.get(medallion_hack_license);
    // This taxi was in consideration earlier
    // => has reached a new place within 30 mins
    if(taxi != null) {
      /*
       * Remove this taxi from previous grid ->
       * Change profitability to decrease empty taxi number corresponding to
       * Area grid_present[medallion_hack_license].a
       */
      this.updateEmptyTaxi(taxi.area, -1, -1);

      /*
       * Add this taxi to the new destination grid ->
       * Change profitability to increase empty taxi number
       * corresponding to Area a
       */
      this.updateEmptyTaxi(a, 1, ts);

      // Update the area - TaxiInfo map
      taxi.area = a;
      taxi.ts = ts;
    }

    // This taxi was not in consideration earlier
    // => has reached a new place > 30 mins
    else {
      /*
       * Add this taxi to the new destination grid ->
       * Change profitability to increase empty taxi number
       * corresponding to Area a
       */
      this.updateEmptyTaxi(a, 1, ts);
      grid_present.put(medallion_hack_license, new TaxiInfo(a, ts));
    }
  }

  public void leaveTaxiSlidingWindow(String medallion_hack_license,
      long ts) {
    // Check if the event leaving corresponds to the event present in the area
    TaxiInfo taxi = grid_present.get(medallion_hack_license);
    if(taxi != null && ts == taxi.ts) {
      // If present, then undo the effects of this event
      this.updateEmptyTaxi(taxi.area, -1, -1);
      grid_present.remove(medallion_hack_license);
    } else if(taxi == null) {
      System.out.println("What the heck happpened to this cab!");
      System.exit(0);
    }
  }

  /*
   * Add diffTaxiNumber to Area a
   * If ts==-1, means the old timestamp has to be preserved.
   * Else, update to timestamp ts
   */
  private void updateEmptyTaxi(Area a, int diffTaxiNumber, long ts) {
    if(area_elem_map.containsKey(a)) {
      SetElem old_elem = area_elem_map.get(a);
      int old_index = (int) (old_elem.profitability/Constants.BUCKET_SIZE);
      old_elem.num_empty_taxis += diffTaxiNumber;
      if(ts != -1) {
        old_elem.ts = ts;
      }

      if(old_elem.mprofit.size()==0 &&
          old_elem.num_empty_taxis==0) {
        sorted_ptb_list.get(old_index).remove(old_elem);
        area_elem_map.remove(a);
      } else {
        old_elem.resetProfitability();

        // Next change the array DS
        int new_index = (int) (old_elem.profitability/Constants.BUCKET_SIZE);

        if(old_index != new_index) {
          sorted_ptb_list.get(old_index).remove(old_elem);
          sorted_ptb_list.get(new_index).add(old_elem);
        }
      }
    } else if(diffTaxiNumber < 0) {
      System.out.println("What the heck happpened to this cab!");
      System.exit(0);
    } else {
      SetElem old_elem = area_elem_map.get(a);
      old_elem.area = a;
      old_elem.num_empty_taxis = diffTaxiNumber;
      old_elem.ts = ts;
      old_elem.resetProfitability();

      // Next change the array DS
      int new_index = (int) (old_elem.profitability/Constants.BUCKET_SIZE);
      sorted_ptb_list.get(new_index).add(old_elem);
    }
  }

  public void leaveProfitSlidingWindow(Area a, float profit) {
    if(area_elem_map.containsKey(a)) {
      // First update the area-ptb map
      SetElem old_elem = area_elem_map.get(a);
      int old_index = (int) (old_elem.profitability/Constants.BUCKET_SIZE);
      old_elem.mprofit.delete(profit);

      if(old_elem.mprofit.size()==0 &&
          old_elem.num_empty_taxis==0) {
        sorted_ptb_list.get(old_index).remove(old_elem);
        area_elem_map.remove(a);
      } else {
        old_elem.resetProfitability();

        // Next change the array DS
        int new_index = (int) (old_elem.profitability/Constants.BUCKET_SIZE);

        if(old_index != new_index) {
          sorted_ptb_list.get(old_index).remove(old_elem);
          sorted_ptb_list.get(new_index).add(old_elem);
        }
      }
    } else {
      System.out.println("What the heck happpened to this cab!");
      System.exit(0);
    }
  }

  public void enterProfitSlidingWindow(Area a, float profit, long ts) {
    if(area_elem_map.containsKey(a)) {
      // First update the area-ptb map
      SetElem old_elem = area_elem_map.get(a);
      int old_index = (int) (old_elem.profitability/Constants.BUCKET_SIZE);
      old_elem.mprofit.insert(profit);
      old_elem.ts = ts;
      old_elem.resetProfitability();

      // Next change the array DS
      int new_index = (int) (old_elem.profitability/Constants.BUCKET_SIZE);

      if(old_index != new_index) {
        sorted_ptb_list.get(old_index).remove(old_elem);
        sorted_ptb_list.get(new_index).add(old_elem);
      }
    } else {
      SetElem old_elem = area_elem_map.get(a);
      old_elem.area = a;
      old_elem.num_empty_taxis = 0;
      old_elem.mprofit.insert(profit);
      old_elem.ts = ts;
      old_elem.resetProfitability();

      // Next change the array DS
      int new_index = (int) (old_elem.profitability/Constants.BUCKET_SIZE);
      sorted_ptb_list.get(new_index).add(old_elem);
    }
  }

  public void printMaxTen(PrintStream print_stream) {
    int numPrinted = 0;
    int currentIndex = Constants.NUM_EMPTY_BUCKETS-1;
    while(numPrinted<10 && currentIndex>=0) {
      Iterator<SetElem> i = sorted_ptb_list.get(currentIndex).iterator();
      while(i.hasNext() && numPrinted<10) {
        SetElem s = i.next();
        SetElem elem = area_elem_map.get(s.area);
        if(elem.num_empty_taxis == 0) {
          continue;
        }
        print_stream.print((s.area.x+1) + "." + (s.area.y+1) + "," +
            elem.num_empty_taxis + "," +  elem.mprofit.getMedian() + "," +
            elem.profitability + ",");
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
        SetElem elem = area_elem_map.get(s.area);
        if(elem.num_empty_taxis == 0) {
          continue;
        }
        top10_area[numPrinted] = s.area;
        top10_medianpft[numPrinted] = elem.mprofit.getMedian();
        top10_empty_taxi[numPrinted] = elem.num_empty_taxis;
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
        if(s == null || s.area == null) {
          System.out.println("aman");
        }
        SetElem elem = area_elem_map.get(s.area);
        if(elem.num_empty_taxis == 0) {
          continue;
        }
        if(top10_area[numPrinted] == null ||
            !top10_area[numPrinted].equals(s.area) ||
            top10_medianpft[numPrinted] != elem.mprofit.getMedian() ||
            top10_empty_taxi[numPrinted] != elem.num_empty_taxis) {
          return false;
        }
        numPrinted++;
      }
      currentIndex--;
    }

    if(numPrinted<10 && top10_area[numPrinted] != null) {
      return false;
    }

    return true;
  }
}
