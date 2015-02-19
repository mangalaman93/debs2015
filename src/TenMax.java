import java.util.AbstractMap;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Vector;

// implements key-val struct
class KeyVal<Key, Val extends Comparable<Val>>
implements Comparable<KeyVal<Key, Val>> {
  public Key key;
  public Val val;

  public KeyVal(Key k, Val v) {
    this.key = k;
    this.val = v;
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof KeyVal<?, ?>))
      return false;

    if(obj == this)
      return true;

    KeyVal<Key, Val> kv = (KeyVal<Key, Val>) obj;
    if(this.key.equals(kv.key) && this.val.equals(kv.val))
      return true;

    return false;
  }

  @Override
  public int compareTo(KeyVal<Key, Val> kv) {
    return this.val.compareTo(kv.val);
  }
}

/* INVARIANTS
 *  *Length of vector is 10
 *  *Vector is always sorted with null inserted if required
 *  *map doesn't contain any element with frequency 0
 *  *Element is always present exactly at two places:
 *    -in map
 *    -in either vector or PQ (not both)
 */
public abstract class TenMax<Key, Val extends Comparable<Val>> {
  // constant parameters
  private final int INIT_PQ_SIZE = 100;

  // abstract functions
  public abstract Val addDiffToVal(Val v, Val diff);
  public abstract boolean isZeroVal(Val v);

  protected AbstractMap<Key, Val> key_val_map;
  private Vector<KeyVal<Key, Val>> max_ten;
  private PriorityQueue<KeyVal<Key, Val>> val_pq;

  public TenMax() {
    max_ten = new Vector<KeyVal<Key, Val>>(10);
    val_pq = new PriorityQueue<KeyVal<Key, Val>>(INIT_PQ_SIZE,
        Collections.reverseOrder());

    for(int i=0; i<10; i++) {
      max_ten.add(null);
    }
  }

  public Vector<KeyVal<Key, Val>> getMaxTen() {
    return max_ten;
  }

  public Vector<Key> getMaxTenCopy() {
    Vector<Key> key_list = new Vector<Key>(10);
    for(int i=0; i<10; i++) {
      if(max_ten.get(i) == null) {
        key_list.add(null);
      } else {
        key_list.add(max_ten.get(i).key);
      }
    }

    return key_list;
  }

  public boolean isSameMaxTenKey(Vector<Key> old_max_ten) {
    for(int i=0; i<10; i++) {
      if(max_ten.get(i) == null && old_max_ten.get(i) == null) {
        return true;
      } else if((max_ten.get(i) == null && old_max_ten.get(i) != null) ||
          (old_max_ten.get(i)== null && max_ten.get(i) != null) ||
          (!max_ten.get(i).key.equals(old_max_ten.get(i)))) {
        return false;
      }
    }

    return true;
  }

  public boolean update(Key key, Val diff) {
    // if the key is present
    if(key_val_map.containsKey(key)) {
      // key is present and total elements are less than 10
      if(key_val_map.size() <= 10) {
        // deleting existing element
        KeyVal<Key, Val> current = null;
        for(int i=0; i<key_val_map.size(); i++) {
          if(max_ten.get(i).key.equals(key)) {
            current = max_ten.remove(i);
            break;
          }
        }

        // if frequency is 0, delete the element
        Val newval = addDiffToVal(current.val, diff);
        if(isZeroVal(newval)) {
          max_ten.add(null);
          key_val_map.remove(key);
          return true;
        } else {
          // inserting new frequency if non zero
          KeyVal<Key, Val> newkval = new KeyVal<Key, Val>(key, newval);
          int index = max_ten.size();
          for(int i=0; i<max_ten.size(); i++) {
            if(max_ten.get(i) == null ||
                newkval.compareTo(max_ten.get(i)) > 0) {
              index = i;
              break;
            }
          }
          max_ten.add(index, newkval);
          key_val_map.put(key, newkval.val);
          return true;
        }
      } else { // key is present but number of total elements are more than 10
        KeyVal<Key, Val> tree_max = val_pq.element();
        KeyVal<Key, Val> vec_min = max_ten.lastElement();
        KeyVal<Key, Val> oldkval = new KeyVal<Key, Val>(key,
            key_val_map.get(key));
        KeyVal<Key, Val> newkval = new KeyVal<Key, Val>(key,
            addDiffToVal(oldkval.val, diff));

        // key present, total elements more than 10, frequency is zero
        if(isZeroVal(newkval.val)) {
          key_val_map.remove(key);

          // key present, total elements more than 10, frequency is zero,
          // and delete from vector
          if(oldkval.compareTo(vec_min) >= 0) {
            for(int i=0; i<10; i++) {
              if(oldkval.compareTo(max_ten.get(i)) == 0) {
                max_ten.remove(i);
                break;
              }
            }
            max_ten.add(9, val_pq.remove());
            return true;
          } else { // key present, total elements more than 10,
            // frequency is zero, and delete from PQ
            val_pq.remove(oldkval);
            return false;
          }
        } else { // key present, total elements more than 10,
          // frequency is nonzero
          /* FINALLY
           *  see if new value goes into a different data
           *  structure than it was originally in
           */
          // key present, total elements more than 10, frequency is nonzero,
          // old and new both in PQ
          if(oldkval.compareTo(vec_min) < 0 && newkval.compareTo(vec_min) < 0) {
            val_pq.remove(oldkval);
            val_pq.add(newkval);
            key_val_map.put(key, newkval.val);
            return false;
          } else if(oldkval.compareTo(tree_max) > 0 &&
              newkval.compareTo(tree_max) > 0) {
            // key present, total elements more than 10, frequency is nonzero,
            //  old and new both in vector

            // deleting existing element
            for(int i=0; i<10; i++) {
              if(max_ten.get(i).key.equals(key)) {
                max_ten.remove(i);
                break;
              }
            }

            // insert element
            int index = max_ten.size();
            for(int i=0; i<max_ten.size(); i++) {
              if(newkval.compareTo(max_ten.get(i)) > 0) {
                index = i;
                break;
              }
            }

            max_ten.add(index, newkval);
            key_val_map.put(key, newkval.val);
            return true;
          } else if(oldkval.compareTo(vec_min) < 0) {
            // key present, total elements more than 10, frequency is nonzero,
            // old in PQ and new in vector

            // old val is in PQ
            val_pq.remove(oldkval);

            // inserting vec_min in val_pq
            val_pq.add(vec_min);
            max_ten.remove(9);

            // new value will be inserted into Vector
            int index = 9;
            for(int i=0; i<9; i++) {
              if(newkval.compareTo(max_ten.get(i)) > 0) {
                index = i;
                break;
              }
            }

            max_ten.add(index, newkval);
            key_val_map.put(key, newkval.val);
            return true;
          } else {
            // key present, total elements more than 10, frequency is nonzero,
            // old in vector and new in PQ
            // old val is in vector
            // deleting existing element
            for(int i=0; i<10; i++) {
              if(max_ten.get(i).key.equals(key)) {
                max_ten.remove(i);
                break;
              }
            }
            max_ten.add(val_pq.remove());

            // new val will be inserted into PQ
            val_pq.add(newkval);
            key_val_map.put(key, newkval.val);
            return true;
          }
        }
      }
    } else { // key is not present
      Val newval = addDiffToVal(null, diff);

      // key is not present, number of total elements is less than 10
      if(key_val_map.size() < 10) {
        int index = key_val_map.size();
        for(int i=0; i<key_val_map.size(); i++) {
          if(newval.compareTo(max_ten.get(i).val) > 0) {
            index = i;
            break;
          }
        }

        max_ten.remove(9);
        max_ten.add(index, new KeyVal<Key, Val>(key, newval));
        key_val_map.put(key, newval);
        return true;
      } else { // key not present, number of total elements is greater than 10
        if(max_ten.lastElement().val.compareTo(newval) > 0) {
          val_pq.add(new KeyVal<Key, Val>(key, newval));
          key_val_map.put(key, newval);
          return false;
        } else {
          val_pq.add(max_ten.lastElement());
          max_ten.remove(9);
          int index = 9;
          for(int i=0; i<9; i++) {
            if(newval.compareTo(max_ten.get(i).val) > 0) {
              index = i;
              break;
            }
          }

          max_ten.add(index, new KeyVal<Key, Val>(key, newval));
          key_val_map.put(key, newval);
          return true;
        }
      }
    }
  }
}
