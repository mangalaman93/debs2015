import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.io.PrintStream;

public class TenMaxFrequency {
  class Freq implements Comparable<Freq> {
    public int frequency;
    public long ts;

    public Freq(int f, long ts) {
      this.frequency = f;
      this.ts = ts;
    }

    public Freq(Freq freq) {
      this.frequency = freq.frequency;
      this.ts = freq.ts;
    }

    @Override
    public boolean equals(Object obj) {
      if(!(obj instanceof Freq))
        return false;

      if(obj == this)
        return true;

      Freq v = (Freq) obj;
      if(v.frequency == this.frequency && v.ts == this.ts)
        return true;

      return false;
    }

    @Override
    public int compareTo(Freq v) {
      if(this.frequency < v.frequency) {
        return -1;
      } else if(this.frequency > v.frequency) {
        return 1;
      } else if(this.ts < v.ts) {
        return -1;
      } else if(this.ts > v.ts) {
        return 1;
      } else {
        return 0;
      }
    }
  }

  final class PairQ1 {
    public Route route;
    public Freq freq;

    public PairQ1(Route r, Freq f) {
      this.route = r;
      this.freq = f;
    }

    @Override
    public boolean equals(Object obj) {
      if(!(obj instanceof PairQ1))
        return false;

      if(obj == this)
        return true;

      PairQ1 pair = (PairQ1) obj;
      if(this.route.equals(pair.route) && this.freq.equals(pair.freq))
        return true;

      return false;
    }
  }

  // Note that same object is stored in both* the data structures
  private HashMap<Route, PairQ1> route_freq_map;
  private ArrayList<ArrayList<TreeMap<Route, PairQ1>>> freq_array;
  private ArrayList<Long> latest_ts;

  // Max frequency
  private int max_frequency;
  private int tenth_frequency;

  // Number of routes for each frequency
  private ArrayList<Integer> route_count;

  // temporary data so that new need not be called
  private ArrayList<Route> old_max_ten_routes;

  public TenMaxFrequency() {
    route_freq_map = new HashMap<Route, PairQ1>(Constants.HM_INIT_SIZE, Constants.HM_LOAD_FACTOR);
    freq_array = new ArrayList<ArrayList<TreeMap<Route, PairQ1>>>(Constants.FREQ_ARRAY_SIZE);
    route_count = new ArrayList<Integer>(Constants.FREQ_ARRAY_SIZE);
    latest_ts = new ArrayList<Long>(Constants.FREQ_ARRAY_SIZE);

    // Initialize frequency array
    for(int i=0; i<Constants.FREQ_ARRAY_SIZE; i++) {
      freq_array.add(new ArrayList<TreeMap<Route, PairQ1>>(Constants.MAX_NUM_TS));
      ArrayList<TreeMap<Route, PairQ1>> array = freq_array.get(i);
      for(int j=0; j<Constants.MAX_NUM_TS; j++) {
        array.add(new TreeMap<Route, PairQ1>());
      }
      route_count.add(0);
      latest_ts.add((long)-1);
    }

    max_frequency = 0;
    tenth_frequency = 0;

    // temporary data initialization
    old_max_ten_routes = new ArrayList<Route>(10);
    for(int i=0; i<10; i++) {
      old_max_ten_routes.add(null);
    }
  }

  public String printMaxTen() {
    String print_string = "";
    int temp_frequency = max_frequency;
    int temp_route_count = route_count.get(temp_frequency);
    int ts = (int)(long)(latest_ts.get(temp_frequency)) & Constants.MAX_NUM_TS_MINUS_1;
    int count = 0;
    while(count < 10 && temp_frequency > 0) {
      // Number of routes remaining for that frequency = 0
      // => means take next one
      if(temp_route_count == 0) {
        temp_frequency = temp_frequency - 1;
        temp_route_count = route_count.get(temp_frequency);
        ts = (int)(long)(latest_ts.get(temp_frequency)) & Constants.MAX_NUM_TS_MINUS_1;
      } else {
        // No routes in the current timestamp
        if(freq_array.get(temp_frequency).get(ts).size() == 0) {
          ts = ts - 1;
          if(ts == -1) {
            ts = Constants.MAX_NUM_TS-1;
          }
        }

        // Iterate for routes in the current timestamp
        else {
          Iterator<Route> iter;
          iter = freq_array.get(temp_frequency).get(ts).keySet().iterator();
          while(iter.hasNext()) {
            PairQ1 p = freq_array.get(temp_frequency).get(ts).get(iter.next());
            print_string = print_string + Integer.toString(p.route.fromArea.x + 1) + "." + Integer.toString(p.route.fromArea.y + 1) + "," + Integer.toString(p.route.toArea.x + 1) + "." + Integer.toString(p.route.toArea.y + 1) + ",";
            temp_route_count = temp_route_count - 1;
            count = count + 1;
            if(count >= 10) {
              break;
            }
          }

          ts = ts - 1;
          if(ts == -1) {
            ts = Constants.MAX_NUM_TS_MINUS_1;
          }
        }
      }
    }

    tenth_frequency = temp_frequency;

    while(count < 10) {
      print_string = print_string + "NULL" + ",";
      count = count + 1;
    }
    return print_string;
  }

  public void storeMaxTenCopy() {
    int temp_frequency = max_frequency;
    int temp_route_count = route_count.get(temp_frequency);
    int ts = (int)(long)(latest_ts.get(temp_frequency)) & Constants.MAX_NUM_TS_MINUS_1;;
    int count = 0;

    while(count < 10 && temp_frequency > 0) {
      // Number of routes remaining for that frequency = 0
      // => means take next one
      if(temp_route_count == 0) {
        temp_frequency = temp_frequency - 1;
        temp_route_count = route_count.get(temp_frequency);
        ts = (int)(long)(latest_ts.get(temp_frequency)) & Constants.MAX_NUM_TS_MINUS_1;
      } else {
        // No routes in the current timestamp
        if(freq_array.get(temp_frequency).get(ts).size() == 0) {
          ts = ts - 1;
          if(ts == -1) {
            ts = Constants.MAX_NUM_TS_MINUS_1;
          }
        }

        // Iterate for routes in the current timestamp
        else {
          Iterator<Route> iter;
          iter = freq_array.get(temp_frequency).get(ts).keySet().iterator();
          while(iter.hasNext()) {
            PairQ1 p = freq_array.get(temp_frequency).get(ts).get(iter.next());
            old_max_ten_routes.set(count, p.route);
            temp_route_count = temp_route_count - 1;
            count = count + 1;

            if(count >= 10) {
              break;
            }
          }

          ts = ts - 1;
          if(ts == -1) {
            ts = Constants.MAX_NUM_TS_MINUS_1;
          }
        }
      }
    }

    while(count < 10) {
      old_max_ten_routes.set(count, null);
      count = count + 1;
    }
  }

  public boolean isSameMaxTenKey() {
    int temp_frequency = max_frequency;
    int temp_route_count = route_count.get(temp_frequency);
    int ts = (int)(long)(latest_ts.get(temp_frequency)) & Constants.MAX_NUM_TS_MINUS_1;
    int count = 0;
    while(count < 10 && temp_frequency > 0) {
      // Number of routes remaining for that frequency = 0
      // =>  means take next one
      if(temp_route_count == 0) {
        temp_frequency = temp_frequency - 1;
        temp_route_count = route_count.get(temp_frequency);
        ts = (int)(long)(latest_ts.get(temp_frequency)) & Constants.MAX_NUM_TS_MINUS_1;
      } else {
        // No routes in the current timestamp
        if(freq_array.get(temp_frequency).get(ts).size() == 0) {
          ts = ts - 1;
          if(ts == -1) {
            ts = Constants.MAX_NUM_TS_MINUS_1;
          }
        }

        // Iterate for routes in the current timestamp
        else {
          Iterator<Route> iter;
          iter = freq_array.get(temp_frequency).get(ts).keySet().iterator();
          while(iter.hasNext()) {
            PairQ1 p = freq_array.get(temp_frequency).get(ts).get(iter.next());
            if((old_max_ten_routes.get(count) == null) ||
                (!p.route.equals(old_max_ten_routes.get(count)))) {
              return false;
            }

            temp_route_count = temp_route_count - 1;
            count = count + 1;

            if(count >= 10) {
              break;
            }
          }

          ts = ts - 1;
          if(ts == -1) {
            ts = Constants.MAX_NUM_TS_MINUS_1;
          }
        }
      }
    }

    if(count >= 10) {
      return true;
    }

    while(count < 10) {
      if(old_max_ten_routes.get(count) != null) {
        return false;
      }

      count = count + 1;
    }

    return true;
  }

  public boolean increaseFrequency(Route r, long ts) {
    // Get the pair from hashmap
    PairQ1 p = route_freq_map.get(r);
    boolean ret_val = false;

    if(p != null) {
      // Remove from current frequency
      freq_array.get(p.freq.frequency).get((int)(long)(p.freq.ts*0.001) & Constants.MAX_NUM_TS_MINUS_1).remove(r);
      int new_count = route_count.get(p.freq.frequency) - 1;
      route_count.set(p.freq.frequency, new_count);
      if(p.freq.frequency >= tenth_frequency) {
        ret_val = true;
      }

      // Add to next frequency
      p.freq.frequency = p.freq.frequency + 1;
      p.freq.ts = ts;
      freq_array.get(p.freq.frequency).get((int)(long)(ts*0.001) & Constants.MAX_NUM_TS_MINUS_1).put(r, p);
      if(p.freq.frequency >= tenth_frequency) {
        ret_val = true;
      }

      if(latest_ts.get(p.freq.frequency) < (long)(ts*0.001)) {
        latest_ts.set(p.freq.frequency, (long)(ts*0.001));
      }

      new_count = route_count.get(p.freq.frequency) + 1;
      route_count.set(p.freq.frequency, new_count);

      // Increment max frequency if necessary
      if(max_frequency < p.freq.frequency) {
        max_frequency = p.freq.frequency;
      }
    } else {
      // Create new objects
      Freq f = new Freq(1, ts);
      p = new PairQ1(r, f);

      // Insert in the hashmap
      route_freq_map.put(r, p);

      // Frequency = 1
      freq_array.get(1).get((int)(long)(ts*0.001) & Constants.MAX_NUM_TS_MINUS_1).put(r, p);
      if(1 >= tenth_frequency) {
        ret_val = true;
      }

      if(latest_ts.get(1) < (long)(ts*0.001)) {
        latest_ts.set(p.freq.frequency, (long)(ts*0.001));
      }

      int new_count = route_count.get(1) + 1;
      route_count.set(1,new_count);

      // Increment max frequency if necessary
      if(max_frequency == 0) {
        max_frequency = 1;
      }
    }

    return ret_val;
  }

  public boolean decreaseFrequency(Route r, long ts) {
    // Get the pair from hashmap
    PairQ1 p = route_freq_map.get(r);
    boolean ret_val = false;

    // Remove from current frequency
    freq_array.get(p.freq.frequency).get((int)(long)(p.freq.ts*0.001) & Constants.MAX_NUM_TS_MINUS_1).remove(r);
    if(p.freq.frequency >= tenth_frequency) {
      ret_val = true;
    }

    int new_count = route_count.get(p.freq.frequency) - 1;
    route_count.set(p.freq.frequency, new_count);
    if(max_frequency == p.freq.frequency &&
        route_count.get(p.freq.frequency) == 0) {
      max_frequency = p.freq.frequency - 1;
    }

    // Decrement frequency
    p.freq.frequency = p.freq.frequency - 1;

    // Remove from hashmap if frequency hits zero
    if(p.freq.frequency == 0) {
      route_freq_map.remove(r);
    } else {
      // Add to lower frequency
      freq_array.get(p.freq.frequency).get((int)(long)(p.freq.ts*0.001) & Constants.MAX_NUM_TS_MINUS_1).put(r, p);
      if(p.freq.frequency >= tenth_frequency) {
        ret_val = true;
      }

      if(latest_ts.get(p.freq.frequency) < (long)(p.freq.ts*0.001)) {
        latest_ts.set(p.freq.frequency, (long)(p.freq.ts*0.001));
      }

      new_count = route_count.get(p.freq.frequency) + 1;
      route_count.set(p.freq.frequency, new_count);
    }

    return ret_val;
  }
}
