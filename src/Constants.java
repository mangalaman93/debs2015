// parameters and constants
public class Constants {
	// tuning parameter for HashMap
  public static final int HM_INIT_SIZE = 100;
  public static final float HM_LOAD_FACTOR = 0.75f;

	// capacity of queue between IO process and Q* processes
  public static final int QUEUE1_CAPACITY = 10000;
  public static final int QUEUE2_CAPACITY = 10000;

  // number of IO processes
  public static final boolean TWO_IO_PROCESS = true;

  // Q1 constants
  public static final int FREQ_ARRAY_SIZE = 500;
  public static final int MAX_NUM_TS = 2048;

  // Max profitability value possible
  public final static int MAX_PFT_SIZE = 1000;
  // Q2 constants and parameters
  public final static int AREA_LIMIT = 600;
  public final static int NUM_EMPTY_BUCKETS = 2000;
  public final static float BUCKET_SIZE = (float) MAX_PFT_SIZE/ (float) NUM_EMPTY_BUCKETS;
  public final static float INV_BUCKET_SIZE = (float) NUM_EMPTY_BUCKETS / (float) MAX_PFT_SIZE;


  // input and output file
  public static final String DEFAULT_INPUT_FILE = "out/sorted_data_full.csv";
  public static final String Q1_FILE = "out/q1_out.csv";
  public static final String Q2_FILE = "out/q2_out.csv";
}
