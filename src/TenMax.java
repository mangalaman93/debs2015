import java.util.Vector;

public class TenMax<T> {
  private Vector<T> maxElems;
  
  public TenMax() {
    maxElems = new Vector<T>(10);
  }

  public boolean incrFreqency(T key) {
    return false;
  }

  public boolean decrFrequency(T key) {
    return false;
  }

  // unsafe
  public Vector<T> getTenMax() {
    return maxElems;
  }
}
