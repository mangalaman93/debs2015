import static org.junit.Assert.*;
import java.sql.Timestamp;
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
    System.out.print(tmp.getMaxTen().get(i).val.profitability);
    System.out.print(tmp.getMaxTen().get(i).val.ts);
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
    medallion = new String[20];
    hack_license = new String[20];

    for(int i=0; i<5; i++) {
      for(int j=0; j<5; j++) {
        a[i*5+j] = new Area(i, j);
      }
    }

    for(int i=0; i<100; i++) {
      ts[i] = UniqueTimestamp.getTimestamp();
    }

    for(int i=0; i<20; i++) {
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
      tmp.enterProfitSlidingWindow(a[i], 100.45f, ts[i]);
    }

    for(int i=9; i>=0; i--) {
      temp.add(new KeyVal<Area, Profitability>(a[i],
          new Profitability(100.45f, 0, ts[i])));
    }

    assertTrue(tmp.getMaxTen().equals(temp));
  }
}
