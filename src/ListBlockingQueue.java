import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ListBlockingQueue<E> implements BlockingQueue<E> {
  private int blocksize;
  private ArrayBlockingQueue<ArrayList<E>> bqueue;
  private int take_index;
  private int put_index;
  private ArrayList<E> take_current;
  private ArrayList<E> put_current;

  public ListBlockingQueue(int capacity, int block_size) {
    bqueue = new ArrayBlockingQueue<ArrayList<E>>(capacity/block_size, false);
    blocksize = block_size;
    take_current = null;
    put_current = new ArrayList<E>(blocksize);
    take_index = blocksize;
    put_index = 0;
  }

  public E take() throws InterruptedException {
    if(take_index == blocksize) {
      take_current = bqueue.take();
      take_index = 0;
    }

    take_index++;
    return take_current.get(take_index-1);
  }

  public void put(E e) throws InterruptedException {
    put_index++;
    put_current.add(e);

    if(put_index == blocksize) {
      bqueue.put(put_current);
      put_current = new ArrayList<E>(blocksize);
      put_index = 0;
    }
  }

  public void putForce(E e) throws InterruptedException {
    put_index++;
    put_current.add(e);

    bqueue.put(put_current);
    put_current = new ArrayList<E>(blocksize);
    put_index = 0;
  }

  @Override
  public E element() {
    throw new UnsupportedOperationException();
  }

  @Override
  public E peek() {
    throw new UnsupportedOperationException();
  }

  @Override
  public E poll() {
    throw new UnsupportedOperationException();
  }

  @Override
  public E remove() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(Collection<? extends E> arg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();

  }

  @Override
  public boolean containsAll(Collection<?> arg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isEmpty() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator<E> iterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> arg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> arg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int size() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object[] toArray() {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T[] toArray(T[] arg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean add(E e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean contains(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int drainTo(Collection<? super E> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int drainTo(Collection<? super E> c, int maxElements) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean offer(E e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean offer(E e, long timeout, TimeUnit unit)
      throws InterruptedException {
    throw new UnsupportedOperationException();
  }

  @Override
  public E poll(long timeout, TimeUnit unit) throws InterruptedException {
    throw new UnsupportedOperationException();
  }

  @Override
  public int remainingCapacity() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }
}
