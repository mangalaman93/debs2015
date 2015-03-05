import java.util.HashMap;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
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
  private ArrayList<ArrayList<Set<PairQ1>>> freq_array;
  private Vector<Long> latest_ts;

  // Max frequency
  private int max_frequency;

  // Number of routes for each frequency
  private Vector<Integer> route_count;

  // temporary data so that new need not be called
  private Vector<Route> old_max_ten_routes;

  public TenMaxFrequency() {
    route_freq_map = new HashMap<Route, PairQ1>(Constants.HM_INIT_SIZE,
        Constants.HM_LOAD_FACTOR);
    freq_array = new ArrayList<ArrayList<Set<PairQ1>>>(Constants.FREQ_ARRAY_SIZE);
    route_count = new Vector<Integer>(Constants.FREQ_ARRAY_SIZE);
    latest_ts = new Vector<Long>(Constants.FREQ_ARRAY_SIZE);

    // Initialize frequency array
    for(int i=0; i<Constants.FREQ_ARRAY_SIZE; i++) {
      freq_array.add(new ArrayList<Set<PairQ1>>(Constants.MAX_NUM_TS));
      ArrayList<Set<PairQ1>> array = freq_array.get(i);
      for(int j=0; j<Constants.MAX_NUM_TS; j++) {
        array.add(new HashSet<PairQ1>());
      }
      route_count.add(0);
      latest_ts.add((long)-1);
    }

    max_frequency = 0;

    // temporary data initialization
    old_max_ten_routes = new Vector<Route>(10);
    for(int i=0; i<10; i++) {
      old_max_ten_routes.add(null);
    }
  }

  public void printMaxTen(PrintStream print_stream) {
    int temp_frequency = max_frequency;
    int temp_route_count = route_count.get(temp_frequency);
    int ts = (int)(latest_ts.get(temp_frequency)/1000)%1800;
    int count = 0;
    while(count < 10 && temp_frequency > 0) {
      // Number of routes remaining for that frequency = 0
    	// => means take next one
      if(temp_route_count == 0) {
        temp_frequency = temp_frequency - 1;
        temp_route_count = route_count.get(temp_frequency);
        ts = (int)(latest_ts.get(temp_frequency)/1000)%1800;
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
          Iterator<PairQ1> iter;
          iter = freq_array.get(temp_frequency).get(ts).iterator();
          while(iter.hasNext()) {
            PairQ1 p = (PairQ1)iter.next();
            print_stream.print(p.route.fromArea.x + 1);
            print_stream.print(".");
            print_stream.print(p.route.fromArea.y + 1);
            print_stream.print(",");
            print_stream.print(p.route.toArea.x + 1);
            print_stream.print(".");
            print_stream.print(p.route.toArea.y + 1);
            print_stream.print(",");

            temp_route_count = temp_route_count - 1;
            count = count + 1;
            if(count >= 10) {
              break;
            }
          }
          ts = ts - 1;
          if(ts == -1) {
          	ts = 1799;
          }
        }
      }
    }

    while(count < 10) {
      print_stream.print("NULL");
      print_stream.print(",");
      count = count + 1;
    }
  }

  public void storeMaxTenCopy() {
    int temp_frequency = max_frequency;
    int temp_route_count = route_count.get(temp_frequency);
    int ts = (int)(latest_ts.get(temp_frequency)/1000)%1800;
    int count = 0;
    while(count < 10 && temp_frequency > 0) {
      // Number of routes remaining for that frequency = 0
    	// => means take next one
      if(temp_route_count == 0) {
        temp_frequency = temp_frequency - 1;
        temp_route_count = route_count.get(temp_frequency);
        ts = (int)(latest_ts.get(temp_frequency)/1000)%1800;
      } else {
        // No routes in the current timestamp
        if(freq_array.get(temp_frequency).get(ts).size() == 0) {
          ts = ts - 1;
          if(ts == -1) ts = 1799;
        }

        // Iterate for routes in the current timestamp
        else {
          Iterator<PairQ1> iter;
          iter = freq_array.get(temp_frequency).get(ts).iterator();
          while (iter.hasNext()) {
            PairQ1 p = (PairQ1)iter.next();
            old_max_ten_routes.set(count, p.route);
            temp_route_count = temp_route_count - 1;
            count = count + 1;
            if(count >= 10) {
              break;
            }
          }
          ts = ts - 1;
          if(ts == -1) ts = 1799;
        }
      }
    }
    while(count < 10) {
      old_max_ten_routes.set(count, null);
      count = count + 1;
    }
  }

  public boolean isSameMaxTenKey() {
    Vector<Route> new_max_ten = new Vector<Route>(10);
    int temp_frequency = max_frequency;
    int temp_route_count = route_count.get(temp_frequency);
    int ts = (int)(latest_ts.get(temp_frequency)/1000)%1800;
    int count = 0;
    for(int  i = 0; i < 10; i++) {
      new_max_ten.add(null);
    }
    while(count < 10 && temp_frequency > 0) {
      // Number of routes remaining for that frequency = 0
      // =>  means take next one
      if(temp_route_count == 0) {
        temp_frequency = temp_frequency - 1;
        temp_route_count = route_count.get(temp_frequency);
        ts = (int)(latest_ts.get(temp_frequency)/1000)%1800;
      } else {
        // No routes in the current timestamp
        if(freq_array.get(temp_frequency).get(ts).size() == 0) {
          ts = ts - 1;
          if(ts == -1) ts = 1799;
        }

        // Iterate for routes in the current timestamp
        else {
          Iterator<PairQ1> iter;
          iter = freq_array.get(temp_frequency).get(ts).iterator();
          while (iter.hasNext()) {
            PairQ1 p = (PairQ1)iter.next();
            new_max_ten.set(count, p.route);
            temp_route_count = temp_route_count - 1;
            count = count + 1;
            if(count >= 10) {
              break;
            }
          }
          ts = ts - 1;
          if(ts == -1) ts = 1799;
        }
      }
    }
    // Compare new ten max and old ten max
    for(int i=0; i<10; i++) {
      if(new_max_ten.get(i) == null && old_max_ten_routes.get(i) == null) {
        return true;
      } else if((new_max_ten.get(i) == null &&
      		old_max_ten_routes.get(i) != null) ||
          (old_max_ten_routes.get(i)== null &&
          new_max_ten.get(i) != null) ||
          (!new_max_ten.get(i).equals(old_max_ten_routes.get(i)))) {
        return false;
      }
    }
    return true;

  }

  public boolean increaseFrequency(Route r, long ts) {
  	// Get the pair from hashmap
    PairQ1 p = route_freq_map.get(r);

    if(p != null) {
      // Remove from current frequency
      freq_array.get(p.freq.frequency).get((int)(p.freq.ts/1000)%1800).remove(p);
      int new_count = route_count.get(p.freq.frequency) - 1;
      route_count.set(p.freq.frequency, new_count);
      // Add to next frequency
      p.freq.frequency = p.freq.frequency + 1;
      p.freq.ts = ts;
      freq_array.get(p.freq.frequency).get((int)(ts/1000)%1800).add(p);
      if(latest_ts.get(p.freq.frequency) < ts) {
        latest_ts.set(p.freq.frequency, ts);
      }
      new_count = route_count.get(p.freq.frequency) + 1;
      route_count.set(p.freq.frequency, new_count);
      // Increment max frequency if necessary
      if(max_frequency < p.freq.frequency) {
        max_frequency = p.freq.frequency;
      }
    }
    else {
      // Create new objects
      Freq f = new Freq(1, ts);
      p = new PairQ1(r, f);
      // Insert in the hashmap
      route_freq_map.put(r, p);
      // Frequency = 1
      freq_array.get(1).get((int)(ts/1000)%1800).add(p);
      if(latest_ts.get(1) < ts) {
        latest_ts.set(p.freq.frequency, ts);
      }
      int new_count = route_count.get(1) + 1;
      route_count.set(1,new_count);
      // Increment max frequency if necessary
      if(max_frequency == 0) {
        max_frequency = 1;
      }
    }
    return true;
  }

  public boolean decreaseFrequency(Route r, long ts) {
    // Get the pair from hashmap
    PairQ1 p = route_freq_map.get(r);
    // Remove from current frequency
    freq_array.get(p.freq.frequency).get((int)(p.freq.ts/1000)%1800).remove(p);
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
    }
    else {
    // Add to lower frequency
      freq_array.get(p.freq.frequency).get((int)(p.freq.ts/1000)%1800).add(p);
      if(latest_ts.get(p.freq.frequency) < p.freq.ts) {
        latest_ts.set(p.freq.frequency, p.freq.ts);
      }
      new_count = route_count.get(p.freq.frequency) + 1;
      route_count.set(p.freq.frequency, new_count);
    }
    return true;
  }
}
