import static org.junit.Assert.*;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.UUID;
import java.util.Vector;
import org.junit.Before;
import org.junit.Test;

class UniqueTimestamp {
  static Timestamp last_ts = null;

  public static Timestamp getTimestamp() {
    if(last_ts == null) {
      last_ts = new Timestamp((new java.util.Date()).getTime());
      return last_ts;
    }

    Timestamp ts;
    do {
      ts = new Timestamp((new java.util.Date()).getTime());
    } while(ts.compareTo(last_ts) <= 0);

    last_ts = ts;
    return ts;
  }
}

public class TenMaxProfitabilityTest {
  TenMaxProfitability tmp;
  Area[] a;
  Timestamp[] ts;
  String[] medallion_hack_license;

  @Before
  public void setUp() throws Exception {
    tmp = new TenMaxProfitability();
    a = new Area[25];
    ts = new Timestamp[100];
    medallion_hack_license = new String[200];

    for(int i=0; i<5; i++) {
      for(int j=0; j<5; j++) {
        a[i*5+j] = new Area(i, j);
      }
    }

    for(int i=0; i<100; i++) {
      ts[i] = UniqueTimestamp.getTimestamp();
    }

    for(int i=0; i<200; i++) {
      medallion_hack_license[i] = UUID.randomUUID().toString();
    }
  }

  @Test
  public void testTenMaxProfitability1() {
    Vector<PairQ2> temp = new Vector<PairQ2>();
    for(int i=0; i<10; i++) {
      temp.add(null);
    }

    assertTrue(tmp.getMaxTenCopy().equals(temp));
  }

  @Test
  public void testTenMaxProfitability2() {
    Vector<PairQ2> temp = new Vector<PairQ2>();
    for(int i=0; i<10; i++) {
      tmp.enterProfitSlidingWindow(a[i], i, (float)(0.45+i*7.6), ts[i].getTime());
    }

    for(int i=0; i<10; i++) {
      tmp.enterTaxiSlidingWindow(medallion_hack_license[i], a[i], i);
    }

    for(int i=9; i>=0; i--) {
      temp.add(new PairQ2(a[i],(float)(0.45+i*7.6),1));
    }

    assertTrue(tmp.getMaxTenCopy().equals(temp));
  }

  @Test
  public void testTenMaxProfitability3() {
    Vector<PairQ2> temp = new Vector<PairQ2>();
    for(int i=0; i<10; i++) {
      tmp.enterProfitSlidingWindow(a[i], i, 100.45f, ts[9-i].getTime());
    }

    for(int i=0; i<10; i++) {
      temp.add(null);
    }

    assertTrue(tmp.getMaxTenCopy().equals(temp));
  }

  @Test
  public void testTenMaxProfitability4() {
    Vector<PairQ2> temp = new Vector<PairQ2>();
    for(int i=0; i<10; i++) {
      tmp.enterProfitSlidingWindow(a[i], i, 100.45f, ts[i].getTime());
    }

    for(int i=10; i<20; i++) {
      tmp.leaveProfitSlidingWindow(a[i-10], i-10, 100.45f);
    }

    for(int i=0; i<10; i++) {
      temp.add(null);
    }

    assertTrue(tmp.getMaxTenCopy().equals(temp));
  }

  @Test
  public void testTenMaxProfitability5() {
    Vector<PairQ2> temp = new Vector<PairQ2>();
    for(int i=0; i<10; i++) {
      tmp.enterProfitSlidingWindow(a[i], i, 100.45f, ts[i].getTime());
    }

    for(int i=10; i<20; i++) {
      tmp.enterProfitSlidingWindow(a[i-10], i, 100.465f, ts[29-i].getTime());
    }

    for(int i=0; i<10; i++) {
      tmp.enterTaxiSlidingWindow(medallion_hack_license[i], a[i], i+20);
    }

    for(int i=9; i>=0; i--) {
      temp.add(new PairQ2(a[i],(100.45f+100.465f)/2,1));
    }

    assertTrue(tmp.getMaxTenCopy().equals(temp));
  }

  @Test
  public void testTenMaxProfitability6() {
    int count = 0;
    Vector<PairQ2> temp = new Vector<PairQ2>();
    for(int i=0; i<10; i++) {
      tmp.enterProfitSlidingWindow(a[i], i, 100.45f, ts[count].getTime());
      count++;
    }

    int taxi_count = 0;
    for(int i=0; i<10; i++) {
      for(int j=0; j<i+1; j++) {
        tmp.enterTaxiSlidingWindow(medallion_hack_license[taxi_count], a[i], j);
        taxi_count++;
        count++;
      }

      temp.add(new PairQ2(a[i],100.45f/(i+1), i+1));
    }

    assertTrue(tmp.getMaxTenCopy().equals(temp));
  }

  @Test
  public void testTenMaxProfitability7() {
    int count = 0;
    Vector<PairQ2> temp = new Vector<PairQ2>();

    for(int i=0; i<10; i++) {
      tmp.enterProfitSlidingWindow(a[i], i, 100.45f, ts[count].getTime());
      count++;
    }

    for(int i=0; i<10; i++) {
      for(int j=0; j<i+1; j++) {
        tmp.enterTaxiSlidingWindow(medallion_hack_license[i], a[i], j);
        count++;
      }

      temp.add(new PairQ2(a[i],100.45f, 1));
    }

    Collections.reverse(temp);
    assertTrue(tmp.getMaxTenCopy().equals(temp));
  }
}

