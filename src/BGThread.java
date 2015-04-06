import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BGThread extends Thread {
  private BlockingQueue<Runnable> task_queue = null;
  private boolean is_stopped = false;

  public BGThread() {
    task_queue = new ArrayBlockingQueue(Constants.TASK_QUEUE_SIZE, false);
  }

  @Override
  public void run(){
    while(!is_stopped){
      try {
        Runnable runnable = task_queue.take();
        runnable.run();
      } catch(Exception e){
        System.out.println("Error in BGThread!");
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public void put(Runnable task) throws InterruptedException {
    task_queue.put(task);
  }

  public void doStop() {
    is_stopped = true;
  }

  public void sync() {
    while(task_queue.size() != 0) {
      Thread.yield();
    }
  }
}
