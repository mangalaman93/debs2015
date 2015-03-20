import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

class MyUniqueTimestamp {
  static long last_ts = 0;

  public static long getTimestamp() {
    if(last_ts == 0) {
      last_ts = (new java.util.Date()).getTime();
      return last_ts;
    }

    long ts;
    do {
      ts = (new java.util.Date()).getTime();
    }  while(ts <= last_ts);

    last_ts = ts;
    return ts;
  }
}

public class TenMaxFrequencyTest {
  Route[] r;
  Area[] a;
  long[] ts;
  TenMaxFrequency tmf;

  @Before
  public void setUp() throws Exception {
    r = new Route[14];
    a = new Area[9];
    ts = new long[1000];

    a[0] = new Area(1, 1);
    a[1] = new Area(0, 1);
    a[2] = new Area(1, 0);
    a[3] = new Area(2, 0);
    a[4] = new Area(0, 0);
    a[5] = new Area(4, 5);
    a[6] = new Area(3, 3);
    a[7] = new Area(3, 2);
    a[8] = new Area(2, 2);

    r[0] = new Route(a[7], a[0]);
    r[1] = new Route(a[1], a[2]);
    r[2] = new Route(a[1], a[3]);
    r[3] = new Route(a[4], a[7]);
    r[4] = new Route(a[2], a[8]);
    r[5] = new Route(a[6], a[0]);
    r[6] = new Route(a[5], a[3]);
    r[7] = new Route(a[2], a[6]);
    r[8] = new Route(a[3], a[5]);
    r[9] = new Route(a[2], a[5]);
    r[10] = new Route(a[1], a[0]);
    r[11] = new Route(a[7], a[5]);
    r[12] = new Route(a[8], a[3]);
    r[13] = new Route(a[2], a[4]);

    for(int i=0; i<1000; i++) {
      ts[i] = MyUniqueTimestamp.getTimestamp();
    }

    tmf = new TenMaxFrequency();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testGetMaxTen1() throws Exception{
    tmf.increaseFrequency(r[0], ts[0]);
    String s = tmf.printMaxTen();
    assertTrue(!tmf.isSameMaxTenKey());
    assertEquals("4.3,2.2,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,", s);
  }

  @Test
  public void testGetMaxTen2() {
    for(int i=0; i<6; i++){
      tmf.increaseFrequency(r[i], ts[i] + i*1000);
    }
    String s = tmf.printMaxTen();
    assertTrue(!tmf.isSameMaxTenKey());
    assertEquals("4.4,2.2,2.1,3.3,1.1,4.3,1.2,3.1,1.2,2.1,4.3,2.2,NULL,NULL,NULL,NULL,", s);
  }

  @Test
  public void testGetMaxTen3() {
    for(int i=0; i<11; i++){
      tmf.increaseFrequency(r[i], ts[i] + i*1000);
    }
    String s = tmf.printMaxTen();
    assertTrue(!tmf.isSameMaxTenKey());
    assertEquals("1.2,2.2,2.1,5.6,3.1,5.6,2.1,4.4,5.6,3.1,4.4,2.2,2.1,3.3,1.1,4.3,1.2,3.1,1.2,2.1,", s);
  }

  @Test
  public void testGetMaxTen4() {
    for(int i=0; i<8; i++){
      tmf.increaseFrequency(r[i], ts[i] + i*1000);
    }
    tmf.increaseFrequency(r[0], ts[8] + 8*1000);
    String s = tmf.printMaxTen();
    assertTrue(!tmf.isSameMaxTenKey());
    assertEquals("4.3,2.2,2.1,4.4,5.6,3.1,4.4,2.2,2.1,3.3,1.1,4.3,1.2,3.1,1.2,2.1,NULL,NULL,", s);
  }

  @Test
  public void testGetMaxTen5() {
    for(int i=0; i<11; i++){
      tmf.increaseFrequency(r[i], ts[i] + i*1000);
    }
    tmf.increaseFrequency(r[0], ts[11] + 11*1000);
    String s = tmf.printMaxTen();
    assertTrue(!tmf.isSameMaxTenKey());
    assertEquals("4.3,2.2,1.2,2.2,2.1,5.6,3.1,5.6,2.1,4.4,5.6,3.1,4.4,2.2,2.1,3.3,1.1,4.3,1.2,3.1,", s);
  }

  @Test
  public void tesGetMaxTen6() {
    for(int i=0; i<8; i++){
      tmf.increaseFrequency(r[i], ts[i] + i*1000);
    }
    tmf.decreaseFrequency(r[0], ts[8] + 8*1000);
    String s = tmf.printMaxTen();
    assertTrue(!tmf.isSameMaxTenKey());
    assertEquals("2.1,4.4,5.6,3.1,4.4,2.2,2.1,3.3,1.1,4.3,1.2,3.1,1.2,2.1,NULL,NULL,NULL,", s);
  }

  @Test
  public void testGetMaxTen7() {
    for(int i=0; i<11; i++){
      tmf.storeMaxTenCopy();
      tmf.increaseFrequency(r[i], ts[i] + i*1000);
      assertTrue(!tmf.isSameMaxTenKey());
    }
    tmf.storeMaxTenCopy();
    tmf.decreaseFrequency(r[0], ts[11] + 11*1000);
    assertTrue(tmf.isSameMaxTenKey());
    String s = tmf.printMaxTen();
    assertEquals("1.2,2.2,2.1,5.6,3.1,5.6,2.1,4.4,5.6,3.1,4.4,2.2,2.1,3.3,1.1,4.3,1.2,3.1,1.2,2.1,", s);
  }

  @Test
  public void testGetMaxTen8() {
    for(int i=0; i<11; i++){
      tmf.increaseFrequency(r[i], ts[i] + i*1000);
    }
    tmf.increaseFrequency(r[1], ts[11] + 11*1000);
    String s = tmf.printMaxTen();

    assertTrue(!tmf.isSameMaxTenKey());
    assertEquals("1.2,2.1,1.2,2.2,2.1,5.6,3.1,5.6,2.1,4.4,5.6,3.1,4.4,2.2,2.1,3.3,1.1,4.3,1.2,3.1,", s);
  }

  @Test
  public void testGetMaxTen9() {
    for(int i=0; i<11; i++){
      tmf.increaseFrequency(r[i], ts[i] + i*1000);
    }
    tmf.increaseFrequency(r[2], ts[11] + 11*1000);
    String s = tmf.printMaxTen();

    assertTrue(!tmf.isSameMaxTenKey());
    assertEquals("1.2,3.1,1.2,2.2,2.1,5.6,3.1,5.6,2.1,4.4,5.6,3.1,4.4,2.2,2.1,3.3,1.1,4.3,1.2,2.1,", s);
  }

  @Test
  public void testGetMaxTen10() {
    for(int i=0; i<14; i++){
      tmf.increaseFrequency(r[i], ts[i] + i*1000);
    }
    tmf.decreaseFrequency(r[8], ts[14] + 14*1000);
    String s = tmf.printMaxTen();

    assertTrue(!tmf.isSameMaxTenKey());
    assertEquals("2.1,1.1,3.3,3.1,4.3,5.6,1.2,2.2,2.1,5.6,2.1,4.4,5.6,3.1,4.4,2.2,2.1,3.3,1.1,4.3,", s);
  }
}
