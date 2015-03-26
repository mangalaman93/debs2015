import java.io.PrintStream;
import java.util.*;

public class TenMaxFrequency {
	
	class PQ_elem implements Comparable<PQ_elem> {
		Route r;
		int key;
		int freq;
		public long ts;
		
		PQ_elem(Route r, int freq, long ts) {
			this.r = r;
			this.key  = (r.toArea.x*LENGTH+r.toArea.y)*NUMBER_OF_AREAS + (r.fromArea.x*LENGTH+r.fromArea.y);
			this.freq = freq;
			this.ts = ts;
		}

		@Override
		public int compareTo(PQ_elem e) {
			if(freq != e.freq) {
				return freq-e.freq;
			}
			else if(ts > e.ts) {
				return 1;
			}
			else {
				return -1;
			}
		}
	}
	
	// Reverse comparator class defined
	class customComparator implements Comparator<Integer> {
	    @Override
	    public int compare(Integer i1, Integer i2) {
			if(heap[i1].freq > heap[i2].freq) {
				return -1;
			}
			else if(heap[i1].freq < heap[i2].freq) {
				return 1;
			}
			else if(heap[i1].ts > heap[i2].ts) {
				return -1;
			}
			else {
				return 1;
			}
		}
	}
	
	private final int LENGTH = 300;
	private final int PQ_SIZE = 100000;
	private final int NUMBER_OF_AREAS = LENGTH*LENGTH;
	PQ_elem[] heap;
	private HashMap<Integer,Integer> heap_index;
	int size;
	private Route[] top_10_routes;
	
	TenMaxFrequency() {
		heap = new PQ_elem[PQ_SIZE];
		for(int i=0; i<PQ_SIZE; i++) heap[i] = null;
		heap_index = new HashMap<Integer,Integer>();
		size = 0;
		top_10_routes = new Route[10];
		for(int i=0;i<10;i++) {
			top_10_routes[i] = null;
		}
	}
	
	private void swap(int index_1, int index_2) {
		PQ_elem elem_1 = heap[index_1];
		PQ_elem elem_2 = heap[index_2];
		heap_index.put(elem_1.key,index_2);
		heap_index.put(elem_2.key,index_1);
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
		while(index<size) {
			if(left_child<size && heap[index].compareTo(heap[left_child]) < 0) {
				swap(index,left_child);
				index = left_child;
				left_child = index*2+1;
			}
			else if(right_child<size && heap[index].compareTo(heap[right_child]) < 0) {
				swap(index,right_child);
				index = right_child;
				right_child = index*2+2;
			}
			else {
				break;
			}
		}
	}
	
	private int getHeapIndexForRoute(Route r) {
		return heap_index.get( (r.toArea.x*LENGTH+r.toArea.y)*NUMBER_OF_AREAS + (r.fromArea.x*LENGTH+r.fromArea.y) );
	}
	
	private void setHeapIndexForRoute(Route r, int index) {
		if(index==-1) {
			heap_index.remove( (r.toArea.x*LENGTH+r.toArea.y)*NUMBER_OF_AREAS + (r.fromArea.x*LENGTH+r.fromArea.y) );
		}
		else {
			heap_index.put( (r.toArea.x*LENGTH+r.toArea.y)*NUMBER_OF_AREAS + (r.fromArea.x*LENGTH+r.fromArea.y), index );
		}
	}
	
	private boolean contains(Route r) {
		return heap_index.containsKey( (r.toArea.x*LENGTH+r.toArea.y)*NUMBER_OF_AREAS + (r.fromArea.x*LENGTH+r.fromArea.y) );
	}

	private void add(Route r, int freq, long ts) {
		setHeapIndexForRoute(r,size);
		heap[size] = new PQ_elem(r,freq,ts);
		size++;
		percolateUp(size-1);
	}
	
	private void remove(Route r) {
		if(!contains(r)) {
			return;
		}
		int index = getHeapIndexForRoute(r);
		if(index == size-1) {
			setHeapIndexForRoute(r,-1);
			heap[size-1] = null;
			size--;
			return;
		}
		else {
			setHeapIndexForRoute(r,-1);
			heap[index] = heap[size-1];
			size--;
			setHeapIndexForRoute(heap[index].r,index);
			int parent = (index-1)/2;
			if(parent>=0 && heap[parent].compareTo(heap[index]) < 0) {
				percolateUp(index);
			}
			else {
				percolateDown(index);
			}
		}
	}
	
	private void update(Route r, int diff_freq, long ts) {
		if(diff_freq<0) {
			int index = getHeapIndexForRoute(r);
			heap[index].freq = heap[index].freq + diff_freq;
			if(heap[index].freq == 0) {
				remove(r);
			}
			else if(heap[index].freq > 0) {
				percolateDown(index);
			}
			else {
				System.out.println("ERROR : freq can't be less than 0");
			}
		}
		else if(diff_freq>0) {
			int index = getHeapIndexForRoute(r);
			heap[index].freq = heap[index].freq + diff_freq;
			percolateUp(index);
		}
	}
	
	public void  decreaseFrequency(Route r, long ts) {
		update(r,-1,ts);
	}
	
	
	public void increaseFrequency(Route r, long ts) {
		if(!contains(r)) {
			add(r,1,ts);
		}
		else {
			update(r,1,ts);
		}
	}
	
	public void printMaxTen(PrintStream print_stream) {
		Comparator<Integer> comparator = new customComparator();
		PriorityQueue<Integer> queue = new PriorityQueue<Integer>(12, comparator);
		if(size>0) queue.add(0);
		int numPrinted = 0;
		while(queue.size()>0 && numPrinted<10) {
			int top_index = queue.poll().intValue();
			print_stream.print((heap[top_index].r.fromArea.x+1) + "." + (heap[top_index].r.fromArea.y+1) + ",");
			print_stream.print((heap[top_index].r.toArea.x+1) + "." + (heap[top_index].r.toArea.y+1) + ",");
			if(top_index*2+1 < size) queue.add(top_index*2+1);
			if(top_index*2+2 < size) queue.add(top_index*2+2);
			numPrinted++;
		}
		while(numPrinted<10) {
			print_stream.print("NULL,");
			numPrinted++;
		}
	}

	public void storeMaxTenCopy() {
		Comparator<Integer> comparator = new customComparator();
		PriorityQueue<Integer> queue = new PriorityQueue<Integer>(12, comparator);
		if(size>0) queue.add(0);
		int numPrinted = 0;
		while(queue.size()>0 && numPrinted<10) {
			int top_index = queue.poll().intValue();
			top_10_routes[numPrinted] = heap[top_index].r;
			if(top_index*2+1 < size) queue.add(top_index*2+1);
			if(top_index*2+2 < size) queue.add(top_index*2+2);
			numPrinted++;
		}
		while(numPrinted<10) {
			top_10_routes[numPrinted] = null;
			numPrinted++;
		}
	}
	
	public boolean isSameMaxTenKey() {
		Comparator<Integer> comparator = new customComparator();
		PriorityQueue<Integer> queue = new PriorityQueue<Integer>(12, comparator);
		if(size>0) queue.add(0);
		int numPrinted = 0;
		while(queue.size()>0 && numPrinted<10) {
			int top_index = queue.poll().intValue();
			if(top_10_routes[numPrinted]==null || !top_10_routes[numPrinted].equals(heap[top_index].r)) {
				return false;
			}
			if(top_index*2+1 < size) queue.add(top_index*2+1);
			if(top_index*2+2 < size) queue.add(top_index*2+2);
			numPrinted++;
		}
		if(numPrinted<10 && top_10_routes[numPrinted]!=null) {
			return false;
		}
		return true;
	}
}
