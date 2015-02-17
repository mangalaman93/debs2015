import java.sql.Timestamp;
import java.util.HashMap;

// implements pair class to store frequency and timestamp
class Freq implements Comparable<Freq> {
  public int frequency;
  public Timestamp ts;

  public Freq(int f, Timestamp ts) {
    this.frequency = f;
    this.ts = ts;
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof Freq))
      return false;

    if(obj == this)
      return true;

    Freq v = (Freq) obj;
    if(v.frequency == this.frequency && v.ts.equals(this.ts))
      return true;

    return false;
  }

  @Override
  public int compareTo(Freq v) {
    if(this.frequency < v.frequency) {
      return -1;
    } else if(this.frequency > v.frequency) {
      return 1;
    } else if(this.ts.compareTo(v.ts) < 0) {
      return -1;
    } else if(this.ts.compareTo(v.ts) > 0) {
      return 1;
    } else {
      return 0;
    }
  }
}

public class TenMaxFrequency extends TenMax<Route, Freq> {
  public TenMaxFrequency() {
    key_val_map = new HashMap<Route, Freq>();
  }

  @Override
  public Freq addDiffToVal(Freq v1, Freq v2) {
    if(v1 != null) {
      Timestamp newts;
      if(v2.frequency < 0) {
        newts = v1.ts;
      } else {
        newts = v2.ts;
      }

      return new Freq(v1.frequency+v2.frequency, newts);
    } else {
      return v2;
    }
  }

  @Override
  public boolean isZeroVal(Freq v) {
    return (v.frequency == 0);
  }
}
