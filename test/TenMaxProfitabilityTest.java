import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.UUID;
import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/* PRINT FUNCTION
 for(int i=0; i<10; i++) {
    System.out.print(tmp.getMaxTen().get(i).key.x);
    System.out.print(",");
    System.out.print(tmp.getMaxTen().get(i).key.y);
    System.out.print("->");
    System.out.print(tmp.getMaxTen().get(i).val.num_empty_taxis);
    System.out.print(",");
    System.out.print(tmp.getMaxTen().get(i).val.profitability);
    System.out.print(",");
    System.out.print(tmp.getMaxTen().get(i).val.ts);
    System.out.print("\t");

    System.out.print(temp.get(i).key.x);
    System.out.print(",");
    System.out.print(temp.get(i).key.y);
    System.out.print("->");
    System.out.print(temp.get(i).val.num_empty_taxis);
    System.out.print(",");
    System.out.print(temp.get(i).val.profitability);
    System.out.print(",");
    System.out.print(temp.get(i).val.ts);
    System.out.print("\n");
  }
 */

public class TenMaxProfitabilityTest {
  TenMaxProfitability tmp;
  Area[] a;
  Timestamp[] ts;
  String[] medallion;
  String[] hack_license;

  @Before
  public void setUp() throws Exception {
    tmp = new TenMaxProfitability();
    a = new Area[25];
    ts = new Timestamp[100];
    medallion = new String[100];
    hack_license = new String[100];

    for(int i=0; i<5; i++) {
      for(int j=0; j<5; j++) {
        a[i*5+j] = new Area(i, j);
      }
    }

    for(int i=0; i<100; i++) {
      ts[i] = UniqueTimestamp.getTimestamp();
    }

    for(int i=0; i<100; i++) {
      medallion[i] = UUID.randomUUID().toString();
      hack_license[i] = UUID.randomUUID().toString();
    }
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testTenMaxProfitability1() {
    Vector<KeyVal<Area, Profitability>> temp = new Vector<KeyVal<Area, Profitability>>();

    for(int i=0; i<10; i++) {
      temp.add(null);
    }

    assertTrue(tmp.getMaxTen().equals(temp));
  }

  @Test
  public void testTenMaxProfitability2() {
    Vector<KeyVal<Area, Profitability>> temp = new Vector<KeyVal<Area, Profitability>>();

    for(int i=0; i<10; i++) {
      tmp.enterProfitSlidingWindow(a[i], (float)(0.45+i*7.6), ts[i]);
    }

    for(int i=9; i>=0; i--) {
      temp.add(new KeyVal<Area, Profitability>(a[i],
          new Profitability((float)(0.45+i*7.6), 0, ts[i])));
    }

    assertTrue(tmp.getMaxTen().equals(temp));
  }

  @Test
  public void testTenMaxProfitability3() {
    Vector<KeyVal<Area, Profitability>> temp = new Vector<KeyVal<Area, Profitability>>();

    for(int i=0; i<10; i++) {
      tmp.enterProfitSlidingWindow(a[i], 100.45f, ts[9-i]);
    }

    for(int i=0; i<10; i++) {
      temp.add(new KeyVal<Area, Profitability>(a[i],
          new Profitability(100.45f, 0, ts[9-i])));
    }

    assertTrue(tmp.getMaxTen().equals(temp));
  }

  @Test
  public void testTenMaxProfitability4() {
    Vector<KeyVal<Area, Profitability>> temp = new Vector<KeyVal<Area, Profitability>>();

    for(int i=0; i<10; i++) {
      tmp.enterProfitSlidingWindow(a[i], 100.45f, ts[i]);
    }

    for(int i=10; i<20; i++) {
      tmp.leaveProfitSlidingWindow(a[i-10], -100.45f);
    }

    for(int i=0; i<10; i++) {
      temp.add(null);
    }

    assertTrue(tmp.getMaxTen().equals(temp));
  }

  @Test
  public void testTenMaxProfitability5() {
    Vector<KeyVal<Area, Profitability>> temp = new Vector<KeyVal<Area, Profitability>>();

    for(int i=0; i<10; i++) {
      tmp.enterProfitSlidingWindow(a[i], 100.45f, ts[i]);
    }

    for(int i=10; i<20; i++) {
      tmp.enterProfitSlidingWindow(a[i-10], 100.465f, ts[29-i]);
    }

    for(int i=0; i<10; i++) {
      temp.add(new KeyVal<Area, Profitability>(a[i],
          new Profitability((100.45f+100.465f)/2, 0, ts[19-i])));
    }

    assertTrue(tmp.getMaxTen().equals(temp));
  }

  @Test
  public void testTenMaxProfitability6() {
    Vector<KeyVal<Area, Profitability>> temp = new Vector<KeyVal<Area, Profitability>>();
    int count = 0;

    for(int i=0; i<10; i++) {
      tmp.enterProfitSlidingWindow(a[i], 100.45f, ts[count]);
      count++;
    }

    int taxi_count = 0;
    for(int i=0; i<10; i++) {
      for(int j=0; j<i+1; j++) {
        tmp.enterTaxiSlidingWindow(medallion[taxi_count], hack_license[taxi_count], a[i], ts[count]);
        taxi_count++;
        count++;
      }
      temp.add(new KeyVal<Area, Profitability>(a[i],
          new Profitability(100.45f/(i+1), i+1, ts[count-1])));
    }

    assertTrue(tmp.getMaxTen().equals(temp));
  }

  @Test
  public void testTenMaxProfitability7() {
    Vector<KeyVal<Area, Profitability>> temp = new Vector<KeyVal<Area, Profitability>>();
    int count = 0;

    for(int i=0; i<10; i++) {
      tmp.enterProfitSlidingWindow(a[i], 100.45f, ts[count]);
      count++;
    }

    for(int i=0; i<10; i++) {
      for(int j=0; j<i+1; j++) {
        tmp.enterTaxiSlidingWindow(medallion[i], hack_license[i], a[i], ts[count]);
        count++;
      }
      temp.add(new KeyVal<Area, Profitability>(a[i],
          new Profitability(100.45f, 1, ts[count-1])));
    }

    Collections.reverse(temp);
    assertTrue(tmp.getMaxTen().equals(temp));
  }
}
