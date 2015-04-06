// parameters and constants
public class Constants {
  // Buffer Reader parameter
  public static final int BUFFER_SIZE = 8192;
  public static final int MAX_LINE_SIZE = 256;

  // tuning parameter for HashMap
  public static final int HM_INIT_SIZE = 10000;
  public static final float HM_LOAD_FACTOR = 0.75f;

  // capacity of queue between IO process and Q* processes
  public static final int QUEUE1_CAPACITY = 10000;
  public static final int QUEUE2_CAPACITY = 10000;
  public static final int BLOCK_SIZE = 100;

  // capacity of queue between printing process and Q* processes
  public static final int QUEUE_OUTPUT_CAPACITY = 50000;

  // Q1 constants
  public static final int FREQ_ARRAY_SIZE = 150;
  // should be some power of 2
  public static final int MAX_NUM_TS = 2048;
  public static final int MAX_NUM_TS_MINUS_1 = MAX_NUM_TS-1;

  // Q2 constants and parameters
  public final static int AREA_LIMIT = 600;
  // Max profitability value possible
  public final static int MAX_PFT_SIZE = 1024;
  public final static int NUM_EMPTY_BUCKETS = 5120;
  // MAX_PFT_SIZE/ NUM_EMPTY_BUCKETS
  public final static float BUCKET_SIZE = 1/5;
  public final static float INV_BUCKET_SIZE = 5;
  public final static int SIZE_INIT_HASHSET = 100;
  public final static int LF_INIT_HASHSET = 1;

  // window sizes
  public final static int WINDOW30_SIZE = 1800000;
  public final static int WINDOW15_SIZE = 900000;

  // input and output file
  public static final String DEFAULT_INPUT_FILE = "out/sorted_data_full.csv";
  public static final String Q1_FILE = "out/q1_out.csv";
  public static final String Q2_FILE = "out/q2_out.csv";

  public static final long parseDate(String date) {
    long timestamp = 1356998400;
    int temp=0;
    int i=1;
    int[] cdays = { 0,
        31,
        31+28,
        31+28+31,
        31+28+31+30,
        31+28+31+30+31,
        31+28+31+30+31+30,
        31+28+31+30+31+30+31,
        31+28+31+30+31+30+31+31,
        31+28+31+30+31+30+31+31+30,
        31+28+31+30+31+30+31+31+30+31,
        31+28+31+30+31+30+31+31+30+31+30
    };

    for(int c = 0; c < date.length(); c++) {
      char ch = date.charAt(c);
      if(ch == '-' || ch == ':' || ch == ' ') {
        switch (i) {
        case 1 : timestamp += (temp-2013)*365 *24*60*60; break;
        case 2 : timestamp += cdays[temp-1]   *24*60*60; break;
        case 3 : timestamp += (temp-1)      *24*60*60; break;
        case 4 : timestamp += temp           *60*60; break;
        case 5 : timestamp += temp              *60; break;
        }

        temp = 0;
        i++;
      } else {
        temp *= 10;
        temp += ch -'0';
      }
    }

    timestamp += temp;
    return timestamp*1000;
  }

  public static final long parseDate(char[] date, int start, int end) {
    long timestamp = 1356998400;
    int temp=0;
    int i=1;
    int[] cdays = { 0,
        31,
        31+28,
        31+28+31,
        31+28+31+30,
        31+28+31+30+31,
        31+28+31+30+31+30,
        31+28+31+30+31+30+31,
        31+28+31+30+31+30+31+31,
        31+28+31+30+31+30+31+31+30,
        31+28+31+30+31+30+31+31+30+31,
        31+28+31+30+31+30+31+31+30+31+30
    };

    for(int c = start; c < end; c++) {
      char ch = date[c];
      if(ch == '-' || ch == ':' || ch == ' ') {
        switch (i) {
        case 1 : timestamp += (temp-2013)*365 *24*60*60; break;
        case 2 : timestamp += cdays[temp-1]   *24*60*60; break;
        case 3 : timestamp += (temp-1)      *24*60*60; break;
        case 4 : timestamp += temp           *60*60; break;
        case 5 : timestamp += temp              *60; break;
        }

        temp = 0;
        i++;
      } else {
        temp *= 10;
        temp += ch -'0';
      }
    }

    timestamp += temp;
    return timestamp*1000;
  }

  public static float parseFloat(char[] f, int pos, int len) {
    float     ret   = 0f;         // return value
    int       part  = 0;          // the current part (int, float and sci parts of the number)
    boolean   neg   = false;      // true if part is a negative number

    // find start
    while(pos < len && (f[pos] < '0' || f[pos] > '9') && f[pos] != '-' && f[pos] != '.')
      pos++;

    if(pos == len) {
      return Float.POSITIVE_INFINITY;
    }
    
    // sign
    if (f[pos] == '-') {
      neg = true;
      pos++;
    }

    // integer part
    while (pos < len && !(f[pos] > '9' || f[pos] < '0'))
      part = part*10 + (f[pos++] - '0');
    ret = neg ? (float)(part*-1) : (float)part;

    // float part
    if (pos < len && f[pos] == '.') {
      pos++;
      int mul = 1;
      part = 0;
      while (pos < len && !(f[pos] > '9' || f[pos] < '0')) {
        part = part*10 + (f[pos] - '0');
        mul*=10; pos++;
      }
      ret = neg ? ret - (float)part / (float)mul : ret + (float)part / (float)mul;
    }

    // scientific part
    if (pos < len && (f[pos] == 'e' || f[pos] == 'E')) {
      pos++;
      neg = (f[pos] == '-'); pos++;
      part = 0;
      while (pos < len && !(f[pos] > '9' || f[pos] < '0')) {
        part = part*10 + (f[pos++] - '0');
      }
      if (neg)
        ret = ret / (float)Math.pow(10, part);
      else
        ret = ret * (float)Math.pow(10, part);
    }
    return ret;
  }
}
