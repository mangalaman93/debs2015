// parameters and constants
public class Constants {
	// tuning parameter for HashMap
  public static final int HM_INIT_SIZE = 100;
  public static final float HM_LOAD_FACTOR = 0.75f;

  // sliding window length
	public final int WINDOW_CAPACITY = 1000;

	// capacity of queue between IO process and Q* processes
  public static final int QUEUE1_CAPACITY = 10000;
  public static final int QUEUE2_CAPACITY = 10000;

  // number of io processes
  public static final boolean TWO_IO_PROCESS = true;

  // input and output file
  public static final String DEFAULT_INPUT_FILE = "out/sorted_data.csv";
  public static final String Q1_FILE = "out/q1_out.csv";
  public static final String Q2_FILE = "out/q2_out.csv";
}