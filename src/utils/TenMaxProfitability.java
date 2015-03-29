package utils;
import java.io.PrintStream;
import java.util.*;

public class TenMaxProfitability {

	class TaxiInfo {
		public Area area;
	    public long id;

	    public TaxiInfo(Area a, int id) {
	      this.area = a;
	      this.id = id;
	    }
	}
	
	class PQ_elem implements Comparable<PQ_elem> {
		Area a;
		int key;
		Mc mprofit;
		int num_empty_taxi;
		float ptb;
		int id;
		
		PQ_elem(Area a) {
			this.a = a;
			this.key  = a.x*LENGTH+a.y;
			this.id = 0;
			mprofit = new Mc();
			num_empty_taxi = 0;
			this.ptb = 0.0f;
		}

		public void resetProfitability() {
	      if(this.mprofit.size() == 0) {
	        this.ptb = 0;
	      } else if(this.num_empty_taxi == 0) {
	        this.ptb = this.mprofit.getMedian();
	      } else {
	        this.ptb = this.mprofit.getMedian()/this.num_empty_taxi;
	      }
	    }

		@Override
		public int compareTo(PQ_elem e) {
			if(num_empty_taxi == 0) {
				return -1;
			}
			else if(e.num_empty_taxi == 0) {
				return 1;
			}
			else if(ptb > e.ptb) {
				return 1;
			}
			else if(ptb < e.ptb) {
				return -1;
			}
			else if(num_empty_taxi > e.num_empty_taxi) {
				return 1;
			}
			else if(num_empty_taxi < e.num_empty_taxi) {
				return -1;
			}
			else if(id > e.id) {
				return 1;
			}
			else if(id < e.id) {
				return -1;
			}
			else {
				return 0;
			}
		}
	}
	
	// Reverse comparator class defined
	class customComparator implements Comparator<Integer> {
	    @Override
	    public int compare(Integer i1, Integer i2) {
			if(heap[i1].ptb > heap[i2].ptb) {
				return -1;
			}
			else if(heap[i1].ptb < heap[i2].ptb) {
				return 1;
			}
			else if(heap[i1].num_empty_taxi > heap[i2].num_empty_taxi) {
				return -1;
			}
			else if(heap[i1].num_empty_taxi < heap[i2].num_empty_taxi) {
				return 1;
			}
			else if(heap[i1].id > heap[i2].id) {
				return -1;
			}
			else {
				return -1;
			}
		}
	}
	
	private final int LENGTH = 600;
	private final int NUMBER_OF_AREAS = LENGTH*LENGTH;
	private final float PRECISION_ERROR_CORRECTION = 0.000001f;

	PQ_elem[] heap;
	int[] heap_index;
  	HashMap<String, TaxiInfo> grid_present;
  	boolean has_top_10_changed;
  	float last_ptb_val;
	
	public TenMaxProfitability() {
		heap = new PQ_elem[NUMBER_OF_AREAS];
		heap_index = new int[NUMBER_OF_AREAS];
		for(int i=0; i<LENGTH; i++) {
			for(int j=0; j<LENGTH; j++) {
				heap[i*LENGTH+j] = new PQ_elem(new Area(i,j));
				heap_index[i*LENGTH+j] = i*LENGTH+j;
			}
		}
    	grid_present = new HashMap<String, TaxiInfo>();
    	has_top_10_changed = false;
    	last_ptb_val = -1.0f;
	}
	
	private void swap(int index_1, int index_2) {
		PQ_elem elem_1 = heap[index_1];
		PQ_elem elem_2 = heap[index_2];
		heap_index[elem_1.key] = index_2;
		heap_index[elem_2.key] = index_1;
		heap[index_2] = elem_1;
		heap[index_1] = elem_2;
	}
	
	private void percolateUp(int index) {
		int parent = (index-1)/2;
		while(parent>=0 && heap[parent].compareTo(heap[index]) < 0) {
			swap(index,parent);
			index = parent;
			if(index==0) break;
			parent = (index-1)/2;
		}
	}
	
	private void percolateDown(int index) {
		int left_child = index*2+1;
		int right_child = index*2+2;
		while(index<NUMBER_OF_AREAS) {
			if(left_child<NUMBER_OF_AREAS && heap[index].compareTo(heap[left_child]) < 0) {
				swap(index,left_child);
				index = left_child;
				left_child = index*2+1;
			}
			else if(right_child<NUMBER_OF_AREAS && heap[index].compareTo(heap[right_child]) < 0) {
				swap(index,right_child);
				index = right_child;
				right_child = index*2+2;
			}
			else {
				break;
			}
		}
	}
	
	private int getHeapIndexForRoute(Area a) {
		return heap_index[a.x*LENGTH+a.y];
	}
	
	private void setHeapIndexForRoute(Area a, int index) {
		heap_index[a.x*LENGTH+a.y] = index;
	}

	public void enterTaxiSlidingWindow(String medallion_hack_license, Area a, int id) {
		TaxiInfo taxi = grid_present.get(medallion_hack_license);
		if(taxi!=null && !a.equals(taxi.area)) {
			int curr_index = getHeapIndexForRoute(a);
			if(!has_top_10_changed && heap[curr_index].num_empty_taxi>0 && heap[curr_index].ptb >= last_ptb_val) {
				has_top_10_changed = true;
			}
			heap[curr_index].num_empty_taxi = heap[curr_index].num_empty_taxi + 1;
			heap[curr_index].id = id;
			heap[curr_index].resetProfitability();
			if(!has_top_10_changed && heap[curr_index].num_empty_taxi>0 && heap[curr_index].ptb >= last_ptb_val) {
				has_top_10_changed = true;
			}
			if(heap[curr_index].num_empty_taxi==1) percolateUp(curr_index);
			else percolateDown(curr_index);

			curr_index = getHeapIndexForRoute(taxi.area);
			if(!has_top_10_changed && heap[curr_index].num_empty_taxi>0 && heap[curr_index].ptb >= last_ptb_val) {
				has_top_10_changed = true;
			}
			heap[curr_index].num_empty_taxi = heap[curr_index].num_empty_taxi - 1;
			heap[curr_index].resetProfitability();
			if(!has_top_10_changed && heap[curr_index].num_empty_taxi>0 && heap[curr_index].ptb >= last_ptb_val) {
				has_top_10_changed = true;
			}
			if(heap[curr_index].num_empty_taxi==0) percolateDown(curr_index);
			else percolateUp(curr_index);
			taxi.area = a;
      		taxi.id = id;
		}
		else if(taxi!=null) {
      		taxi.id = id;
		}
		else if(taxi==null) {
	    	int curr_index = getHeapIndexForRoute(a);
	    	if(!has_top_10_changed && heap[curr_index].num_empty_taxi>0 && heap[curr_index].ptb >= last_ptb_val) {
				has_top_10_changed = true;
			}
			heap[curr_index].num_empty_taxi = heap[curr_index].num_empty_taxi + 1;
			heap[curr_index].id = id;
			heap[curr_index].resetProfitability();
			if(!has_top_10_changed && heap[curr_index].num_empty_taxi>0 && heap[curr_index].ptb >= last_ptb_val) {
				has_top_10_changed = true;
			}
			if(heap[curr_index].num_empty_taxi==1) percolateUp(curr_index);
			else percolateDown(curr_index);
	    	grid_present.put(medallion_hack_license, new TaxiInfo(a, id));
	    }
	}

	public void leaveTaxiSlidingWindow(String medallion_hack_license, int id) {
		TaxiInfo taxi = grid_present.get(medallion_hack_license);
	    if(taxi != null && id == taxi.id) {
	    	int curr_index = getHeapIndexForRoute(taxi.area);
	    	if(!has_top_10_changed && heap[curr_index].num_empty_taxi>0 && heap[curr_index].ptb >= last_ptb_val) {
				has_top_10_changed = true;
			}
			heap[curr_index].num_empty_taxi = heap[curr_index].num_empty_taxi - 1;
			heap[curr_index].resetProfitability();
			if(!has_top_10_changed && heap[curr_index].num_empty_taxi>0 && heap[curr_index].ptb >= last_ptb_val) {
				has_top_10_changed = true;
			}
			if(heap[curr_index].num_empty_taxi==0) percolateDown(curr_index);
			else percolateUp(curr_index);
	    	grid_present.remove(medallion_hack_license);
	    }
	}

	public void leaveProfitSlidingWindow(Area a, int id, float profit) {
		int curr_index = getHeapIndexForRoute(a);
		float old_ptb_val = heap[curr_index].ptb;
		heap[curr_index].mprofit.delete(id,profit);
		heap[curr_index].resetProfitability();
		float new_ptb_val = heap[curr_index].ptb;
		if(heap[curr_index].num_empty_taxi == 0) {
			return;
		}
		else if(old_ptb_val<new_ptb_val) {
			percolateUp(curr_index);
		}
		else {
			percolateDown(curr_index);
		}
		curr_index = getHeapIndexForRoute(a);
		if(!has_top_10_changed && heap[curr_index].num_empty_taxi>0 && old_ptb_val >= last_ptb_val) {
			has_top_10_changed = true;
		}
		if(!has_top_10_changed && heap[curr_index].num_empty_taxi>0 && new_ptb_val >= last_ptb_val) {
			has_top_10_changed = true;
		}
	}

	public void enterProfitSlidingWindow(Area a, int id, float profit) {
		int curr_index = getHeapIndexForRoute(a);
		float old_ptb_val = heap[curr_index].ptb;
		heap[curr_index].mprofit.insert(id,profit);
		heap[curr_index].id = id;
		heap[curr_index].resetProfitability();
		float new_ptb_val = heap[curr_index].ptb;
		if(heap[curr_index].num_empty_taxi == 0) {
			return;
		}
		else if(old_ptb_val<=new_ptb_val) {
			percolateUp(curr_index);
		}
		else {
			percolateDown(curr_index);
		}
		curr_index = getHeapIndexForRoute(a);
		if(!has_top_10_changed && heap[curr_index].num_empty_taxi>0 && old_ptb_val >= last_ptb_val) {
			has_top_10_changed = true;
		}
		if(!has_top_10_changed && heap[curr_index].num_empty_taxi>0 && new_ptb_val >= last_ptb_val) {
			has_top_10_changed = true;
		}
	}
	
	public String printMaxTen() {
		String ret_string = "";
		Comparator<Integer> comparator = new customComparator();
		PriorityQueue<Integer> queue = new PriorityQueue<Integer>(12, comparator);
		if(heap[0].num_empty_taxi>0) queue.add(0);
		int numPrinted = 0;
		while(queue.size()>0 && numPrinted<10) {
			int top_index = queue.poll().intValue();
			ret_string += (heap[top_index].a.x+1) + "." + (heap[top_index].a.y+1) + ","
					+ heap[top_index].num_empty_taxi + "," + heap[top_index].mprofit.getMedian() + "," + heap[top_index].ptb + ",";
			last_ptb_val = heap[top_index].ptb - PRECISION_ERROR_CORRECTION;
			if(heap[top_index*2+1].num_empty_taxi>0) queue.add(top_index*2+1);
			if(heap[top_index*2+2].num_empty_taxi>0) queue.add(top_index*2+2);
			numPrinted++;
		}
		while(numPrinted<10) {
			ret_string += "NULL,";
			last_ptb_val = -1.0f;
			numPrinted++;
		}
		has_top_10_changed = false;
		return ret_string;
	}

	public boolean isSameMaxTenKey() {
		return !has_top_10_changed;
	}
}
