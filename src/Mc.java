import java.util.ArrayList;
import java.util.HashMap;

public class Mc {
  class PQElem {
    public int id;
    public float val;

    public PQElem(int id, float val) {
      this.id = id;
      this.val = val;
    }
  }

  int minheap_size;
  int maxheap_size;
  ArrayList<PQElem> minheap; /* Contains the max 1/2 elements */
  ArrayList<PQElem> maxheap; /* Contains the least 1/2 elements */
  HashMap<Integer,Integer> id_whichHeap_map;
  HashMap<Integer,Integer> id_heapIndex_map;

  public Mc() {
    minheap_size = 0;
    maxheap_size = 0;
    minheap = new ArrayList<PQElem>();
    maxheap = new ArrayList<PQElem>();
    id_whichHeap_map = new HashMap<Integer,Integer>();
    id_heapIndex_map = new HashMap<Integer,Integer>();
  }

  public void insert(int id, float val) {
    if(maxheap_size == 0) {
      id_whichHeap_map.put(id, 0);
      id_heapIndex_map.put(id, 0);
      maxheap.add(new PQElem(id, val));
      maxheap_size++;
    }

    // Even number of elements i.e equally distributed
    else if(maxheap_size == minheap_size) {
      if(val <= minheap.get(0).val) {
        // Insert to maxheap
        id_whichHeap_map.put(id, 0);
        id_heapIndex_map.put(id,maxheap_size);
        maxheap.add(new PQElem(id, val));
        maxheap_size++;
        maxheap_percolateUp(maxheap_size-1);
      } else {
        PQElem elem_to_replace = minheap.get(0);

        // Insert to minheap
        minheap.set(0,new PQElem(id,val));
        id_whichHeap_map.put(id,1);
        id_heapIndex_map.put(id,0);
        minheap_percolateDown(0);

        // Insert to maxheap
        id_whichHeap_map.put(elem_to_replace.id, 0);
        id_heapIndex_map.put(elem_to_replace.id, maxheap_size);
        maxheap.add(elem_to_replace);
        maxheap_size++;
        maxheap_percolateUp(maxheap_size-1);
      }
    }

    // Odd number of elements i.e inequally distributed
    else {
      if(val >= maxheap.get(0).val) {
        // Insert to minheap
        id_whichHeap_map.put(id, 1);
        id_heapIndex_map.put(id, minheap_size);
        minheap.add(new PQElem(id, val));
        minheap_size++;
        minheap_percolateUp(minheap_size-1);
      } else {
        PQElem elem_to_replace = maxheap.get(0);

        // Insert to minheap
        id_whichHeap_map.put(elem_to_replace.id, 1);
        id_heapIndex_map.put(elem_to_replace.id, minheap_size);
        minheap.add(elem_to_replace);
        minheap_size++;
        minheap_percolateUp(minheap_size-1);

        // Insert to maxheap
        maxheap.set(0,new PQElem(id, val));
        id_whichHeap_map.put(id,0);
        id_heapIndex_map.put(id,0);
        maxheap_percolateDown(0);
      }
    }
  }

  public void delete(int id, float val) {
    if(id_whichHeap_map.get(id).equals(0)) {
      if(minheap_size == maxheap_size) {
        int index = id_heapIndex_map.get(id);

        // Replace to maxheap from minheap
        maxheap.set(index,minheap.get(0));
        id_whichHeap_map.put(maxheap.get(index).id, 0);
        id_heapIndex_map.put(maxheap.get(index).id, index);
        maxheap_percolateUp(index);

        // Delete from minheap
        minheap.set(0,minheap.get(minheap_size-1));
        id_heapIndex_map.put(minheap.get(0).id, 0);
        minheap.remove(minheap_size-1);
        minheap_size--;
        minheap_percolateDown(0);
      } else {
        int index = id_heapIndex_map.get(id);

        // Delete from maxheap
        maxheap.set(index,maxheap.get(maxheap_size-1));
        id_heapIndex_map.put(maxheap.get(index).id, index);
        maxheap.remove(maxheap_size-1);
        maxheap_size--;
        maxheap_percolateDown(index);
      }
    } else if(id_whichHeap_map.get(id).equals(1)) {
      if(minheap_size == maxheap_size) {
        int index = id_heapIndex_map.get(id);

        // Delete from minheap
        minheap.set(index,minheap.get(minheap_size-1));
        id_heapIndex_map.put(minheap.get(index).id,index);
        minheap.remove(minheap_size-1);
        minheap_size--;
        minheap_percolateDown(index);
      } else {
        int index = id_heapIndex_map.get(id);

        // Replace to minheap from maxheap
        minheap.set(index,maxheap.get(0));
        id_whichHeap_map.put(minheap.get(index).id, 1);
        id_heapIndex_map.put(minheap.get(index).id, index);
        minheap_percolateUp(index);

        // Delete from maxheap
        maxheap.set(0,maxheap.get(maxheap_size-1));
        id_heapIndex_map.put(maxheap.get(0).id, 0);
        maxheap.remove(maxheap_size-1);
        maxheap_size--;
        maxheap_percolateDown(0);
      }
    }

    id_heapIndex_map.remove(id);
    id_whichHeap_map.remove(id);
  }

  public float getMedian() {
    /* Empty PQ case */
    if(maxheap_size == 0) {
      return 0.0f;
    }

    /* Even number case */
    else if(maxheap_size == minheap_size) {
      return (maxheap.get(0).val + minheap.get(0).val) / 2;
    }

    /* Odd number case */
    else {
      return maxheap.get(0).val;
    }
  }

  public int size() {
    return maxheap_size+minheap_size;
  }

  private void maxheap_percolateUp(int child) {
    int parent = (child-1)/2;
    while(parent>=0 &&
        maxheap.get(parent).val<maxheap.get(child).val) {
      maxheap_swap(parent, child);
      child = parent;
      parent = (child-1)/2;
    }
  }

  private void maxheap_percolateDown(int parent) {
    int child1 = parent*2+1;
    int child2 = parent*2+2;
    while(child2<maxheap_size) {
      if(maxheap.get(child1).val>maxheap.get(child2).val) {
        if(maxheap.get(child1).val>maxheap.get(parent).val) {
          maxheap_swap(parent, child1);
          parent = child1;
        } else {
          break;
        }
      } else {
        if(maxheap.get(child2).val>maxheap.get(parent).val) {
          maxheap_swap(parent, child2);
          parent = child2;
        } else {
          break;
        }
      }

      child1 = parent*2+1;
      child2 = parent*2+2;
    }

    if(child1<maxheap_size &&
        maxheap.get(child1).val>maxheap.get(parent).val) {
      maxheap_swap(parent, child1);
    }
  }

  private void minheap_percolateUp(int child) {
    int parent = (child-1)/2;
    while(parent>=0 &&
        minheap.get(parent).val>minheap.get(child).val) {
      minheap_swap(parent, child);
      child = parent;
      parent = (child-1)/2;
    }
  }

  private void minheap_percolateDown(int parent) {
    int child1 = parent*2+1;
    int child2 = parent*2+2;
    while(child2<minheap_size) {
      if(minheap.get(child1).val<minheap.get(child2).val) {
        if(minheap.get(child1).val<minheap.get(parent).val) {
          minheap_swap(parent, child1);
          parent = child1;
        } else {
          break;
        }
      } else {
        if(minheap.get(child2).val<minheap.get(parent).val) {
          minheap_swap(parent, child2);
          parent = child2;
        } else {
          break;
        }
      }

      child1 = parent*2+1;
      child2 = parent*2+2;
    }

    if(child1<minheap_size &&
        minheap.get(child1).val<minheap.get(parent).val) {
      minheap_swap(parent, child1);
    }
  }

  private void maxheap_swap(int index1, int index2) {
    PQElem tmp = maxheap.get(index1);
    maxheap.set(index1, maxheap.get(index2));
    maxheap.set(index2, tmp);
    id_heapIndex_map.put(maxheap.get(index2).id, index2);
    id_heapIndex_map.put(maxheap.get(index1).id, index1);
  }

  private void minheap_swap(int index1, int index2) {
    PQElem tmp = minheap.get(index1);
    minheap.set(index1,minheap.get(index2));
    minheap.set(index2,tmp);
    id_heapIndex_map.put(minheap.get(index2).id,index2);
    id_heapIndex_map.put(minheap.get(index1).id,index1);
  }
}
