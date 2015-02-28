import java.util.HashMap;
import java.util.Vector;

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

  // temporary data so that new need not be called
  Vector<Route> old_max_ten_routes;

  public TenMaxFrequency() {
    route_freq_map = new HashMap<Route, PairQ1>(Constants.HM_INIT_SIZE,
        Constants.HM_LOAD_FACTOR);


    // temporary data initialization
    old_max_ten_routes = new Vector<Route>(10);
    for(int i=0; i<10; i++) {
      old_max_ten_routes.add(null);
    }
  }

  public void printMaxTen() {
    // TODO
  }

  public void storeMaxTenCopy() {
    // TODO
  }

  public boolean isSameMaxTenKey() {
    // TODO
    return false;
  }

  // route should not be reused by the callee program
  public void update(Route route, Freq diff) {
    // TODO
  }
}
