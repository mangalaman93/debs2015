import java.util.HashMap;

public class Mc {

  class pqElem {
    public int id;
    public float val;
    
    public pqElem(int id, float val) {
      this.id = id;
      this.val = val;
    }
  }

  private static final int MAX_HEAP_SIZE = 100;
  
  pqElem[] minheap; /* Contains the max 1/2 elements */
  pqElem[] maxheap; /* Contains the least 1/2 elements */
  int minheap_size;
  int maxheap_size;
  HashMap<Integer,Integer> id_whichHeap_map;
  HashMap<Integer,Integer> id_heapIndex_map;

  public Mc() {
    minheap_size = 0;
    maxheap_size = 0;
    minheap = new pqElem[MAX_HEAP_SIZE];
    maxheap = new pqElem[MAX_HEAP_SIZE];
    id_whichHeap_map = new HashMap<Integer,Integer>();
    id_heapIndex_map = new HashMap<Integer,Integer>();
  }

  public void insert(int id, float val) {
    if(maxheap_size == 0) {
      id_whichHeap_map.put(id,0);
      id_heapIndex_map.put(id,0);
      maxheap[0] = new pqElem(id,val);
      maxheap_size++;
    }
    
    // Even number of elements i.e equally distributed
    else if(maxheap_size == minheap_size) {
      if(val <= minheap[0].val) {
        // Insert to maxheap
        id_whichHeap_map.put(id,0);
          id_heapIndex_map.put(id,maxheap_size);
          maxheap[maxheap_size] = new pqElem(id,val);
          maxheap_size++;
          maxheap_percolateUp(maxheap_size-1);
      }
      else {
        pqElem ElemtoReplace = minheap[0];
        
        // Insert to minheap
          minheap[0] = new pqElem(id,val);
        id_whichHeap_map.put(id,1);
          id_heapIndex_map.put(id,0);
          minheap_percolateDown(0);
          
          // Insert to maxheap
        id_whichHeap_map.put(ElemtoReplace.id,0);
          id_heapIndex_map.put(ElemtoReplace.id,maxheap_size);
          maxheap[maxheap_size] = ElemtoReplace;
          maxheap_size++;
          maxheap_percolateUp(maxheap_size-1);
      }
    }
    
 // Odd number of elements i.e inequally distributed
    else {
      if(val >= maxheap[0].val) {
        // Insert to minheap
        id_whichHeap_map.put(id,1);
          id_heapIndex_map.put(id,minheap_size);
          minheap[minheap_size] = new pqElem(id,val);
          minheap_size++;
          minheap_percolateUp(minheap_size-1);
      }
      else {
        pqElem ElemtoReplace = maxheap[0];
        
        // Insert to minheap
        id_whichHeap_map.put(ElemtoReplace.id,1);
          id_heapIndex_map.put(ElemtoReplace.id,minheap_size);
          minheap[minheap_size] = ElemtoReplace;
          minheap_size++;
          minheap_percolateUp(minheap_size-1);
          
          // Insert to maxheap
          maxheap[0] = new pqElem(id,val);
        id_whichHeap_map.put(id,0);
          id_heapIndex_map.put(id,0);
          maxheap_percolateDown(0);
      }
    }
  }

  public void delete(int id, float val) {
    if(!id_whichHeap_map.containsKey(id)) {
      System.out.println("ERROR");
    }
    
    else if(id_whichHeap_map.get(id).equals(0)) {
      if(minheap_size == maxheap_size) {
        int index = id_heapIndex_map.get(id);
        
        // Replace to maxheap from minheap
        maxheap[index] = minheap[0];
        id_whichHeap_map.put(maxheap[index].id,0);
        id_heapIndex_map.put(maxheap[index].id,index);
        maxheap_percolateUp(index);
        
        // Delete from minheap
        minheap[0] = minheap[minheap_size-1];
        //minheap[minheap_size-1] = null;
        id_heapIndex_map.put(minheap[0].id,0);
        minheap_size--;
        minheap_percolateDown(0);
      }
      else {
        int index = id_heapIndex_map.get(id);
        
        // Delete from maxheap
        maxheap[index] = maxheap[maxheap_size-1];
        //maxheap[maxheap_size-1] = null;
        id_heapIndex_map.put(maxheap[index].id,index);
        maxheap_size--;
        maxheap_percolateDown(index);
      }
    }
    
    else if(id_whichHeap_map.get(id).equals(1)) {
      if(minheap_size == maxheap_size) {
        int index = id_heapIndex_map.get(id);
        
        // Delete from minheap
        minheap[index] = minheap[minheap_size-1];
        //minheap[minheap_size-1] = null;
        id_heapIndex_map.put(minheap[index].id,index);
        minheap_size--;
        minheap_percolateDown(index);
      }
      else {
        int index = id_heapIndex_map.get(id);
        
        // Replace to minheap from maxheap
        minheap[index] = maxheap[0];
        id_whichHeap_map.put(minheap[index].id,1);
        id_heapIndex_map.put(minheap[index].id,index);
        minheap_percolateUp(index);
        
        // Delete from maxheap
        maxheap[0] = maxheap[maxheap_size-1];
        //maxheap[maxheap_size-1] = null;
        id_heapIndex_map.put(maxheap[0].id,0);
        maxheap_size--;
        maxheap_percolateDown(0);
      }
    }
    
    else {
      System.out.println("ERROR");
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
      return (maxheap[0].val + minheap[0].val) / 2;
    }

    /* Odd number case */
    else {
      return maxheap[0].val;
    }
  }

  public int size() {
    return maxheap_size+minheap_size;
  }
  
  private void maxheap_percolateUp(int child) {
    int parent = (child-1)/2;
    while(parent>=0 && maxheap[parent].val<maxheap[child].val) {
      maxheap_swap(parent,child);
      child = parent;
      parent = (child-1)/2;
    }
  }

  private void maxheap_percolateDown(int parent) {
    int child1 = parent*2+1;
    int child2 = parent*2+2;
    while(child2<maxheap_size) {
      if(maxheap[child1].val>maxheap[child2].val) {
        if(maxheap[child1].val>maxheap[parent].val) {
          maxheap_swap(parent,child1);
          parent = child1;
        }
        else {
          break;
        }
      }
      else {
        if(maxheap[child2].val>maxheap[parent].val) {
          maxheap_swap(parent,child2);
          parent = child2;
        }
        else {
          break;
        }
      }
      child1 = parent*2+1;
      child2 = parent*2+2;
    }
    if(child1<maxheap_size && maxheap[child1].val>maxheap[parent].val) {
      maxheap_swap(parent,child1);
    }
  }

  private void minheap_percolateUp(int child) {
    int parent = (child-1)/2;
    while(parent>=0 && minheap[parent].val>minheap[child].val) {
      minheap_swap(parent,child);
      child = parent;
      parent = (child-1)/2;
    }
  }

  private void minheap_percolateDown(int parent) {
    int child1 = parent*2+1;
    int child2 = parent*2+2;
    while(child2<minheap_size) {
      if(minheap[child1].val<minheap[child2].val) {
        if(minheap[child1].val<minheap[parent].val) {
          minheap_swap(parent,child1);
          parent = child1;
        }
        else {
          break;
        }
      }
      else {
        if(minheap[child2].val<minheap[parent].val) {
          minheap_swap(parent,child2);
          parent = child2;
        }
        else {
          break;
        }
      }
      child1 = parent*2+1;
      child2 = parent*2+2;
    }
    if(child1<minheap_size && minheap[child1].val<minheap[parent].val) {
      minheap_swap(parent,child1);
    }
  }
  
  private void maxheap_swap(int index1, int index2) {
    pqElem tmp = maxheap[index1];
    maxheap[index1] = maxheap[index2];
    maxheap[index2] = tmp;
    id_heapIndex_map.put(maxheap[index2].id,index2);
    id_heapIndex_map.put(maxheap[index1].id,index1);
  }
  
  private void minheap_swap(int index1, int index2) {
    pqElem tmp = minheap[index1];
    minheap[index1] = minheap[index2];
    minheap[index2] = tmp;
    id_heapIndex_map.put(minheap[index2].id,index2);
    id_heapIndex_map.put(minheap[index1].id,index1);
  }
}
