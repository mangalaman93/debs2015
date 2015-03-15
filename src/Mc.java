import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.*;

public class Mc {
  class maxComparator implements Comparator<McElem> {
    public int compare(McElem x, McElem y) {
        return -1*Double.compare(x.val,y.val);
    }
  }

  class McElem implements Comparable<McElem> {
    public int id;
    public float val;

    public McElem(int id, float val) {
      this.id = id;
      this.val = val;
    }

    @Override
    public int compareTo(McElem e) {
      if(id==e.id) return 0;
      else if(id>e.id) return 1;
      else if(id<e.id) return -1;
      else return id-e.id;
    }

    public boolean equals(McElem e) {
      return id==e.id;
    }
  }


  Comparator<McElem> comparator = new maxComparator();
  PriorityQueue<McElem> minheap; /* Contains the max 1/2 elements */
  PriorityQueue<McElem> maxheap; /* Contains the least 1/2 elements */
  int minheap_size;
  int maxheap_size;

  /*
  If even elements, then both heaps are of same size.
  Else, maxheap has the extra element if there are odd number of entries.
  */

  public Mc() {
    minheap_size = 0;
    maxheap_size = 0;
    minheap = new PriorityQueue<McElem>();
    maxheap = new PriorityQueue<McElem>(10, comparator);
  }

  public void insert(int id, float val) {
    /* Empty PQ case */
    if(maxheap_size == 0) {
      maxheap_size = 1;
      maxheap.add(new McElem(id,val));
    }

    /* Even number case */
    else if(minheap_size == maxheap_size) {
      if(val > minheap.peek().val) {
        /* Remove from minheap and then add to maxheap */
        maxheap.add(minheap.poll());
        minheap.add(new McElem(id,val));
      } else {
        maxheap.add(new McElem(id,val));
      }
      maxheap_size++;
    }

    /* Odd number case */
    else {
      if(val < maxheap.peek().val) {
        /* Remove from maxheap and then add to minheap */
        minheap.add(maxheap.poll());
        maxheap.add(new McElem(id,val));
      } else {
        minheap.add(new McElem(id,val));
      }
      minheap_size++;
    }
  }

  public void delete(int id, float val) {
    Iterator it = minheap.iterator();
    while (it.hasNext()){
      McElem curr = (McElem) it.next();
      if(curr.id==id) return;
    }
    it = maxheap.iterator();
    while (it.hasNext()){
      McElem curr = (McElem) it.next();
      if(curr.id==id) return;
    }
    System.out.println("PAIN");
  }


  /*
  public void delete(int id, float val) {
    // Empty PQ case
    if(maxheap_size == 0);

    // Even number case 
    else if(minheap_size == maxheap_size) {
      if(maxheap.remove(new McElem(id,val))) {
        // Remove from minheap and then add to maxheap  
        maxheap.add(minheap.poll());
      } else if(maxheap.remove(new McElem(id,val))) {
      } else {
        if(id==5870) System.out.println(id + " not deleted");
      }
      minheap_size--;
    }

    // Odd number case 
    else {
      if(minheap.remove(new McElem(id,val))) {
        // Remove from maxheap and then add to minheap  
        minheap.add(maxheap.poll());
      } else if(maxheap.remove(new McElem(id,val))) {
      } else {
        if(id==5870) System.out.println(id + " not deleted");
      }
      maxheap_size--;
    }
  }
  */

  public float getMedian() {
    /* Empty PQ case */
    if(maxheap_size == 0) {
      return 0.0f;
    }

    /* Even number case */
    else if(maxheap_size == minheap_size) {
      return (maxheap.peek().val + minheap.peek().val) / 2;
    }

    /* Odd number case */
    else {
      return maxheap.peek().val;
    }
  }

  public int size() {
    return maxheap_size+minheap_size;
  }
}
