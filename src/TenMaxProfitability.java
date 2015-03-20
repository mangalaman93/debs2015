import java.io.PrintStream;
import java.util.*;

/*
 * This class is used only for the unit tests
 */
class PairQ2 {
  Area a;
  float pft;
  int num_empty_taxi;

  PairQ2(Area a, float pft, int num_empty_taxi) {
    this.a = a;
    this.pft = pft;
    this.num_empty_taxi = num_empty_taxi;
  }

  public boolean equals(Object obj) {
    PairQ2 e = (PairQ2) obj;

    if(this == e) {
      return true;
    }

    if(a.equals(e.a) && pft==e.pft && num_empty_taxi==e.num_empty_taxi) {
      return true;
    }

    return false;
  }
}

public class TenMaxProfitability {
  class TaxiInfo {
    public Area area;
    public long id;

    public TaxiInfo(Area a, int id) {
      this.area = a;
      this.id = id;
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
    public int aindex;

    public SetElem(Area a, int n, long t) {
      this.area = a;
      this.mprofit = new Mc();
      this.num_empty_taxis = n;
      this.profitability = 0;
      this.ts = t;
      this.aindex = -1;
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
    public int compareTo(SetElem elem) {
      if(this == elem || this.area.equals(elem.area)) {
        return 0;
      }
      else if(this.profitability < elem.profitability) {
        return -1;
      } else if(this.profitability > elem.profitability) {
        return 1;
      } else if(this.ts < elem.ts) {
        return -1;
      } else if(this.ts > elem.ts) {
        return 1;
      } else if(this.area.x < elem.area.x) {
        return -1;
      } else if(this.area.x > elem.area.x) {
        return 1;
      } else if(this.area.y < elem.area.y) {
        return -1;
      } else if(this.area.y > elem.area.y) {
        return 1;
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

    @Override
    public int hashCode() {
      return this.area.hashCode();
    }
  }

  class SetElemMap {
    private int xSize;
    private int ySize;
    private SetElem[][] data;

    public SetElemMap(int xLimit, int yLimit) {
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
  private SetElemMap area_elem_map;
  // the array DS
  private List<Set<SetElem>> sorted_ptb_list;

  private float last_10th_pft_val;
  private boolean has_top_10_changed;

  public TenMaxProfitability() {
    area_elem_map = new SetElemMap(Constants.AREA_LIMIT, Constants.AREA_LIMIT);
    grid_present = new HashMap<String, TaxiInfo>();
    sorted_ptb_list = new ArrayList<Set<SetElem>>(Constants.NUM_EMPTY_BUCKETS);
    for(int i=0; i<Constants.NUM_EMPTY_BUCKETS; i++) {
      sorted_ptb_list.add(i, new TreeSet<SetElem>(Collections.reverseOrder()));
    }
    last_10th_pft_val = 0.0f;
    has_top_10_changed = false;
  }

  public void enterTaxiSlidingWindow(String medallion_hack_license,
      Area a, int id) {
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
      this.updateEmptyTaxi(a, 1, id);

      // Update the area - TaxiInfo map
      taxi.area = a;
      taxi.id = id;
    }

    // This taxi was not in consideration earlier
    // => has reached a new place > 30 mins
    else {
      /*
       * Add this taxi to the new destination grid ->
       * Change profitability to increase empty taxi number
       * corresponding to Area a
       */
      this.updateEmptyTaxi(a, 1, id);
      grid_present.put(medallion_hack_license, new TaxiInfo(a, id));
    }
  }

  public void leaveTaxiSlidingWindow(String medallion_hack_license,
      int id) {
    // Check if the event leaving corresponds to the event present in the area
    TaxiInfo taxi = grid_present.get(medallion_hack_license);
    if(taxi != null && id == taxi.id) {
      // If present, then undo the effects of this event
      this.updateEmptyTaxi(taxi.area, -1, -1);
      grid_present.remove(medallion_hack_license);
    }
  }

  /*
   * Add diffTaxiNumber to Area a
   * If ts==-1, means the old timestamp has to be preserved.
   * Else, update to timestamp ts
   */
  private void updateEmptyTaxi(Area a, int diffTaxiNumber, long ts) {
    if(area_elem_map.containsKey(a)) {
      // Delete the old entry corresponding to the area
      SetElem elem = area_elem_map.get(a);
      int old_index = elem.aindex;
      sorted_ptb_list.get(old_index).remove(elem);

      if(!has_top_10_changed && elem.num_empty_taxis>0 &&
          elem.profitability>=last_10th_pft_val) {
        has_top_10_changed = true;
      }

      // Update the area
      elem.num_empty_taxis += diffTaxiNumber;
      if(ts != -1) {
        elem.ts = ts;
      }

      if(elem.mprofit.size()==0 && elem.num_empty_taxis==0) {
        area_elem_map.remove(a);
      } else {
        elem.resetProfitability();
        if(!has_top_10_changed && elem.num_empty_taxis>0 &&
            elem.profitability>last_10th_pft_val) {
          has_top_10_changed = true;
        }

        // Add the updated area
        int new_index = (int) (elem.profitability*Constants.INV_BUCKET_SIZE);
        if(new_index > (Constants.MAX_PFT_SIZE*Constants.INV_BUCKET_SIZE -1)) {
          new_index = (int) (Constants.MAX_PFT_SIZE*Constants.INV_BUCKET_SIZE -1);
        }

        elem.aindex = new_index;
        sorted_ptb_list.get(new_index).add(elem);
      }
    } else if(diffTaxiNumber > 0) {
      // This area is not present
      // create new entry corresponding to this area
      SetElem elem = area_elem_map.get(a);
      elem.area = a;
      elem.num_empty_taxis = diffTaxiNumber;
      elem.ts = ts;
      elem.resetProfitability();
      if(!has_top_10_changed && elem.profitability>=last_10th_pft_val) {
        has_top_10_changed = true;
      }

      // Add the area
      int new_index = (int) (elem.profitability*Constants.INV_BUCKET_SIZE);
      if(new_index > (Constants.MAX_PFT_SIZE*Constants.INV_BUCKET_SIZE -1)) {
        new_index = (int) (Constants.MAX_PFT_SIZE*Constants.INV_BUCKET_SIZE -1);
      }
      elem.aindex = new_index;
      sorted_ptb_list.get(new_index).add(elem);
    }
  }

  public void leaveProfitSlidingWindow(Area a, int id, float profit) {
    // This area is should be already present as it is in the sliding window
    if(area_elem_map.containsKey(a)) {

      // delete old data
      SetElem elem = area_elem_map.get(a);
      int old_index = elem.aindex;
      sorted_ptb_list.get(old_index).remove(elem);

      if(!has_top_10_changed && elem.num_empty_taxis>0 &&
          elem.profitability>=last_10th_pft_val) {
        has_top_10_changed = true;
      }

      // update the data
      elem.mprofit.delete(id,profit);

      if(elem.mprofit.size()==0 && elem.num_empty_taxis==0) {
        area_elem_map.remove(a);
      } else {
        elem.resetProfitability();
        if(!has_top_10_changed && elem.num_empty_taxis>0 &&
            elem.profitability>last_10th_pft_val) {
          has_top_10_changed = true;
        }

        // Add the updated data
        int new_index = (int) (elem.profitability*Constants.INV_BUCKET_SIZE);
        if(new_index > (Constants.MAX_PFT_SIZE*Constants.INV_BUCKET_SIZE -1)) {
          new_index = (int) (Constants.MAX_PFT_SIZE*Constants.INV_BUCKET_SIZE -1);
        }

        elem.aindex = new_index;
        sorted_ptb_list.get(new_index).add(elem);
      }
    }
  }

  public void enterProfitSlidingWindow(Area a, int id, float profit, long ts) {
    // This area is already present
    if(area_elem_map.containsKey(a)) {
      // delete old data
      SetElem elem = area_elem_map.get(a);
      int old_index = elem.aindex;
      sorted_ptb_list.get(old_index).remove(elem);

      if(!has_top_10_changed && elem.num_empty_taxis>0 &&
          elem.profitability>=last_10th_pft_val) {
        has_top_10_changed = true;
      }

      // update the data
      elem.mprofit.insert(id,profit);
      elem.ts = ts;
      elem.resetProfitability();

      if(!has_top_10_changed && elem.num_empty_taxis>0 &&
          elem.profitability>=last_10th_pft_val) {
        has_top_10_changed = true;
      }

      // Add the updated data
      int new_index = (int) (elem.profitability*Constants.INV_BUCKET_SIZE);
      if(new_index > (Constants.MAX_PFT_SIZE*Constants.INV_BUCKET_SIZE -1)) {
        new_index = (int) (Constants.MAX_PFT_SIZE*Constants.INV_BUCKET_SIZE -1);
      }
      elem.aindex = new_index;
      sorted_ptb_list.get(new_index).add(elem);
    } else {
      // This area is not present
      // create new entry corresponding to this area
      SetElem elem = area_elem_map.get(a);
      elem.area = a;
      elem.num_empty_taxis = 0;
      elem.mprofit.insert(id,profit);
      elem.ts = ts;
      elem.resetProfitability();

      // Add this area
      int new_index = (int) (elem.profitability*Constants.INV_BUCKET_SIZE);
      if(new_index > (Constants.MAX_PFT_SIZE*Constants.INV_BUCKET_SIZE -1)) {
        new_index = (int) (Constants.MAX_PFT_SIZE*Constants.INV_BUCKET_SIZE -1);
      }
      sorted_ptb_list.get(new_index).contains(elem);
      sorted_ptb_list.get(new_index).add(elem);
      elem.aindex = new_index;
    }
  }

  public String printMaxTen() {
    String print_string = "";
    int num_printed = 0;
    int current_index = Constants.NUM_EMPTY_BUCKETS-1;
    last_10th_pft_val = 0.0f;
    while(num_printed<10 && current_index>=0) {
      Iterator<SetElem> i = sorted_ptb_list.get(current_index).iterator();
      while(i.hasNext() && num_printed<10) {
        SetElem s = i.next();
        SetElem elem = area_elem_map.get(s.area);
        if(elem.num_empty_taxis == 0) {
          continue;
        }

        print_string = print_string + String.valueOf(s.area.x+1) + "." + String.valueOf(s.area.y+1) + "," + String.valueOf(elem.num_empty_taxis) + "," +  String.valueOf(elem.mprofit.getMedian()) + "," + String.valueOf(elem.profitability) + ",";
        last_10th_pft_val = elem.profitability;
        num_printed++;
      }

      current_index--;
    }

    has_top_10_changed = false;

    while(num_printed < 10) {
      last_10th_pft_val = 0.0f;
      print_string = print_string + "NULL,";
      num_printed++;
    }

    return print_string;
  }

  public boolean isSameMaxTenKey() {
    return !has_top_10_changed;
  }

  /*
   * This function is used only for the unit tests
   */
  public Vector<PairQ2> getMaxTenCopy() {
    Vector<PairQ2> ret_val = new Vector<PairQ2>(10);

    int num_printed = 0;
    int current_index = Constants.NUM_EMPTY_BUCKETS-1;
    while(num_printed<10 && current_index>=0) {
      Iterator<SetElem> i = sorted_ptb_list.get(current_index).iterator();
      while(i.hasNext() && num_printed<10) {
        SetElem s = i.next();
        SetElem elem = area_elem_map.get(s.area);
        if(elem.num_empty_taxis == 0) {
          continue;
        }

        ret_val.add(new PairQ2(s.area,elem.profitability,
            elem.num_empty_taxis));
        num_printed++;
      }

      current_index--;
    }

    while(num_printed < 10) {
      ret_val.add(null);
      num_printed++;
    }

    return ret_val;
  }
}
