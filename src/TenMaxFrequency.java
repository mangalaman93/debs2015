import java.sql.Timestamp;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;

/* INVARIANTS
 *  *Length of vector is 10
 *  *Vector is always sorted with null inserted if required
 *  *map doesn't contain any element with frequency 0
 *  *Element is always present exactly at two places:
 *    -in map
 *    -in either vector or PQ (not both)
 */

// implements pair class to store frequency and timestamp
class Val implements Comparable<Val>
{
  public int frequency;
  public Timestamp ts;

  public Val(int f, Timestamp ts)
  {
    this.frequency = f;
    this.ts = ts;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(!(obj instanceof Val))
      return false;

    if(obj == this)
      return true;

    Val v = (Val) obj;
    if(v.frequency == this.frequency && v.ts.equals(this.ts))
      return true;

    return false;
  }

  @Override
  public int compareTo(Val v)
  {
    if(this.frequency < v.frequency)
    {
      return -1;
    } else if(this.frequency > v.frequency)
    {
      return 1;
    } else if(this.ts.compareTo(v.ts) < 0)
    {
      return -1;
    } else if(this.ts.compareTo(v.ts) > 0)
    {
      return 1;
    } else
    {
      return 0;
    }
  }
}

// implements key-val struct
class KeyVal
{
  public Route route;
  public Val val;

  public KeyVal(Route r, Val v)
  {
    this.route = r;
    this.val = v;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(!(obj instanceof KeyVal))
      return false;

    if(obj == this)
      return true;

    KeyVal kv = (KeyVal) obj;
    if(this.route.equals(kv.route) && this.val.equals(kv.val))
      return true;

    return false;
  }
}

public class TenMaxFrequency
{
  private HashMap<Route, Val> route_freq_map;
  private Vector<KeyVal> max_ten;
  private PriorityQueue<Val> freq_pq;

  public TenMaxFrequency()
  {
    route_freq_map = new HashMap<Route, Val>();
    max_ten = new Vector<KeyVal>(10);
    freq_pq = new PriorityQueue<Val>();

    for(int i=0; i<10; i++)
    {
      max_ten.add(null);
    }
  }

  public Vector<KeyVal> getMaxTen()
  {
    return max_ten;
  }

  public boolean update(Route r, Timestamp ts, int diff)
  {
    if(route_freq_map.containsKey(r))
    {
      if(route_freq_map.size() <= 10)
      {
        // deleting existing element
        Val current_val = null;
        for(int i=0; i<route_freq_map.size(); i++)
        {
          if(max_ten.get(i).route == r)
          {
            current_val = max_ten.remove(i).val;
            break;
          }
        }
        assert current_val != null;
        assert max_ten.size() == 9;

        // if frequency is 0, delete the element
        if(current_val.frequency+diff == 0)
        {
          max_ten.add(null);
          route_freq_map.remove(r);
          return true;
        } else
        {
          // inserting new frequency if non zero
          Val new_val = new Val(current_val.frequency+diff, ts);
          int index = 9;
          for(int i=0; i<max_ten.size(); i++)
          {
            assert max_ten.get(i).val != new_val;
            if(new_val.compareTo(max_ten.get(i).val) > 0)
            {
              index = i;
              break;
            }
          }
          max_ten.add(index, new KeyVal(r, new_val));
          route_freq_map.put(r, new_val);
          return true;
        }
      } else
      {
        Val tree_max = freq_pq.element();
        Val vec_min = max_ten.lastElement().val;
        Val old_val = route_freq_map.get(r);
        Val new_val = new Val(old_val.frequency+diff, ts);

        if(old_val.frequency+diff == 0)
        {
          // frequency is zero
          route_freq_map.remove(r);
          if(old_val.compareTo(vec_min) >= 0)
          {
            // in vector
            assert max_ten.size() == 10;
            for(int i=0; i<10; i++)
            {
              if(old_val.compareTo(max_ten.get(i).val) == 0)
              {
                max_ten.remove(i);
                break;
              }
            }
            assert max_ten.size() == 9;

            Val val = freq_pq.remove();
            int index = 9;
            for(int i=0; i<9; i++)
            {
              if(val.compareTo(max_ten.get(i).val) > 0)
              {
                index = i;
                break;
              }
            }
            max_ten.add(index, new KeyVal(r, val));
            assert max_ten.size() == 10;
            return true;
          } else
          {
            freq_pq.remove(old_val);
            return false;
          }
        } else
        {
          /* FINALLY
           *  see if new value goes into a different data
           *  structure than it was originally in
           */
          if(old_val.compareTo(vec_min) < 0 && new_val.compareTo(vec_min) < 0)
          {
            assert old_val.compareTo(tree_max) <= 0;

            freq_pq.remove(old_val);
            freq_pq.add(new_val);
            route_freq_map.put(r, new_val);
            return false;
          } else if(old_val.compareTo(tree_max) > 0 && new_val.compareTo(tree_max) > 0)
          {
            assert old_val.compareTo(vec_min) >= 0;

            // deleting existing element
            for(int i=0; i<10; i++)
            {
              if(max_ten.get(i).route == r)
              {
                max_ten.remove(i);
                break;
              }
            }
            assert max_ten.size() == 9;

            // insert element
            int index = 9;
            for(int i=0; i<9; i++)
            {
              if(new_val.compareTo(max_ten.get(i).val) > 0)
              {
                index = i;
                break;
              }
            }

            max_ten.add(index, new KeyVal(r, new_val));
            route_freq_map.put(r, new_val);
            return true;
          } else if(old_val.compareTo(vec_min) < 0)
          {
            assert old_val.compareTo(tree_max) <= 0;

            // old val is in PQ
            freq_pq.remove(old_val);

            // inserting vec_min in freq_pq
            freq_pq.add(vec_min);
            max_ten.remove(9);

            // new value will be inserted into Vector
            int index = 9;
            for(int i=0; i<9; i++)
            {
              if(new_val.compareTo(max_ten.get(i).val) > 0)
              {
                index = i;
                break;
              }
            }

            max_ten.add(index, new KeyVal(r, new_val));
            route_freq_map.put(r, new_val);
            return true;
          } else
          {
            // old val is in vector
            // deleting existing element
            for(int i=0; i<10; i++)
            {
              if(max_ten.get(i).route == r)
              {
                max_ten.remove(i);
                break;
              }
            }
            assert max_ten.size() == 9;
            max_ten.add(new KeyVal(r, freq_pq.remove()));

            // new val will be inserted into PQ
            freq_pq.add(new_val);
            route_freq_map.put(r, new_val);
            return true;
          }
        }
      }
    } else
    {
      assert diff > 0;

      Val new_val = new Val(diff, ts);
      if(route_freq_map.size() < 10)
      {
        int index = route_freq_map.size();
        for(int i=0; i<route_freq_map.size(); i++)
        {
          if(new_val.compareTo(max_ten.get(i).val) > 0)
          {
            index = i;
            break;
          }
        }

        max_ten.remove(9);
        max_ten.add(index, new KeyVal(r, new_val));
        route_freq_map.put(r, new_val);
        return true;
      } else
      {
        if(max_ten.lastElement().val.compareTo(new_val) > 0)
        {
          freq_pq.add(new_val);
          route_freq_map.put(r, new_val);
          return false;
        } else
        {
          freq_pq.add(max_ten.lastElement().val);
          max_ten.remove(9);
          int index = 9;
          for(int i=0; i<9; i++)
          {
            if(new_val.compareTo(max_ten.get(i).val) > 0)
            {
              index = i;
              break;
            }
          }

          max_ten.add(index, new KeyVal(r, new_val));
          route_freq_map.put(r, new_val);
          return true;
        }
      }
    }
  }
}
