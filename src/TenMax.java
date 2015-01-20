import java.util.Vector;

/*
 * This class keeps track of 10 keys corresponding to 10 max values.
 * It keeps all the data indexed with key including the value which
 * is used to compare 2 keys. The function getValue retrieves the
 * value from the data give the key. We may want to store data in
 * the vector of 10 max keys as well.
 */
public abstract class TenMax<T> {
  // should we keep Tdata with T in maxElems?
  private Vector<T> maxElems;
  protected abstract Number getValue(T key);

  public TenMax() {
    maxElems = new Vector<T>(10);
  }

  public boolean incrFreqency(T key) {
    // TODO
    return false;
  }

  public boolean decrFrequency(T key) {
    // TODO
    return false;
  }

  // unsafe
  public Vector<T> getTenMax() {
    return maxElems;
  }
}
