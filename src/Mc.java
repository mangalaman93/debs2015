import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.PriorityQueue;

class maxComparator implements Comparator<Double> {
  public int compare(Double x, Double y) {
      return -1*Double.compare(x,y);
  }
}

public class Mc {
  Comparator<Double> comparator = new maxComparator();
  PriorityQueue<Double> minheap; /* Contains the max 1/2 elements */
  PriorityQueue<Double> maxheap; /* Contains the least 1/2 elements */
  int minheap_size;
  int maxheap_size;

  /*
  If even elements, then both heaps are of same size.
  Else, maxheap has the extra element if there are odd number of entries.
  */

  public Mc() {
    minheap_size = 0;
    maxheap_size = 0;
    minheap = new PriorityQueue<Double>();
    maxheap = new PriorityQueue<Double>(10, comparator);
  }

  public void insert(float val) {
    /* Empty PQ case */
    if(maxheap_size == 0) {
      maxheap_size = 1;
      maxheap.add(new Double(val));
    }

    /* Even number case */
    else if(minheap_size == maxheap_size) {
      if(val > minheap.peek().floatValue()) {
        maxheap.add(minheap.poll()); /* Remove from minheap and then add to maxheap */
        minheap.add(new Double(val));
      } else {
        maxheap.add(new Double(val));
      }
      maxheap_size++;
    }

    /* Odd number case */
    else {
      if(val < maxheap.peek().floatValue()) {
        minheap.add(maxheap.poll()); /* Remove from maxheap and then add to minheap */
        maxheap.add(new Double(val));
      } else {
        minheap.add(new Double(val));
      }
      minheap_size++;
    }
  }

  public void delete(float val) {
    /* Empty PQ case */
    if(maxheap_size == 0);

    /* Even number case */
    else if(minheap_size == maxheap_size) {
      if(maxheap.remove(new Double(val))) {
        maxheap.add(minheap.poll()); /* Remove from minheap and then add to maxheap  */
      } else {
        maxheap.remove(new Double(val));
      }
      minheap_size--;
    }

    /* Odd number case */
    else {
      if(minheap.remove(new Double(val))) {
        minheap.add(maxheap.poll()); /* Remove from maxheap and then add to minheap  */
      } else {
        maxheap.remove(new Double(val));
      }
      maxheap_size--;
    }
  }

  public float getMedian() {
    /* Empty PQ case */
    if(maxheap_size == 0) {
      return 0.0f;
    }

    /* Even number case */
    else if(maxheap_size == minheap_size) {
      return (maxheap.peek().floatValue() + minheap.peek().floatValue()) / 2;
    }

    /* Odd number case */
    else {
      return maxheap.peek().floatValue();
    }
  }

  public int size() {
    return maxheap_size+minheap_size;
  }
}
