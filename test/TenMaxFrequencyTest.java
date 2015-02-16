import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Vector;
import org.junit.After;
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
    }  while(ts.compareTo(last_ts) <= 0);

    last_ts = ts;
    return ts;
  }
}

public class TenMaxFrequencyTest {
  Route[] r;
  Area[] a;
  Timestamp[] ts;
  TenMaxFrequency tmf;

  @Before
  public void setUp() throws Exception {
    r = new Route[14];
    a = new Area[9];
    ts = new Timestamp[1000];

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
      ts[i] = UniqueTimestamp.getTimestamp();
    }

    tmf = new TenMaxFrequency();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testGetMaxTen1() {
    tmf.update(r[0], new Freq(1, ts[0]));
    Vector<KeyVal<Route, Freq>> temp = new Vector<KeyVal<Route, Freq>>();
    temp.add(new KeyVal<Route, Freq>(r[0], new Freq(1, ts[0])));
    for(int i=1; i<10; i++)
      temp.add(null);
    assertTrue(tmf.getMaxTen().equals(temp));
  }

  @Test
  public void tesGetMaxTen2() {
    Vector<KeyVal<Route, Freq>> temp = new Vector<KeyVal<Route, Freq>>();

    for(int i=0; i<6; i++)
      tmf.update(r[i], new Freq(1, ts[i]));

    for(int i=5; i>=0; i--)
      temp.add(new KeyVal<Route, Freq>(r[i], new Freq(1, ts[i])));

    for(int i=6; i<10; i++)
      temp.add(null);

    assertTrue(tmf.getMaxTen().equals(temp));
  }

  @Test
  public void tesGetMaxTen3() {
    Vector<KeyVal<Route, Freq>> temp = new Vector<KeyVal<Route, Freq>>();

    for(int i=0; i<11; i++)
      tmf.update(r[i], new Freq(1, ts[i]));

    for(int i=10; i>0; i--)
      temp.add(new KeyVal<Route, Freq>(r[i], new Freq(1, ts[i])));

    assertTrue(tmf.getMaxTen().equals(temp));
  }

  @Test
  public void tesGetMaxTen4() {
    Vector<KeyVal<Route, Freq>> temp = new Vector<KeyVal<Route, Freq>>();

    for(int i=0; i<8; i++)
      tmf.update(r[i], new Freq(1, ts[i]));
    tmf.update(r[0], new Freq(1, ts[8]));

    temp.add(new KeyVal<Route, Freq>(r[0], new Freq(2, ts[8])));
    for(int i=7; i>0; i--)
      temp.add(new KeyVal<Route, Freq>(r[i], new Freq(1, ts[i])));
    temp.add(null);
    temp.add(null);

    assertTrue(tmf.getMaxTen().equals(temp));
  }

  @Test
  public void testGetMaxTen5() {
    Vector<KeyVal<Route, Freq>> temp = new Vector<KeyVal<Route, Freq>>();

    for(int i=0; i<11; i++)
      tmf.update(r[i], new Freq(1, ts[i]));
    tmf.update(r[0], new Freq(1, ts[11]));

    temp.add(new KeyVal<Route, Freq>(r[0], new Freq(2, ts[11])));
    for(int i=10; i>1; i--)
      temp.add(new KeyVal<Route, Freq>(r[i], new Freq(1, ts[i])));

    assertTrue(tmf.getMaxTen().equals(temp));
  }

  @Test
  public void tesGetMaxTen6() {
    Vector<KeyVal<Route, Freq>> temp = new Vector<KeyVal<Route, Freq>>();

    for(int i=0; i<8; i++)
      tmf.update(r[i], new Freq(1, ts[i]));
    tmf.update(r[0], new Freq(-1, ts[8]));

    for(int i=7; i>0; i--)
      temp.add(new KeyVal<Route, Freq>(r[i], new Freq(1, ts[i])));
    temp.add(null);
    temp.add(null);
    temp.add(null);

    assertTrue(tmf.getMaxTen().equals(temp));
  }

  @Test
  public void testGetMaxTen7() {
    Vector<KeyVal<Route, Freq>> temp = new Vector<KeyVal<Route, Freq>>();

    for(int i=0; i<11; i++)
      tmf.update(r[i], new Freq(1, ts[i]));
    tmf.update(r[0], new Freq(-1, ts[11]));

    for(int i=10; i>0; i--)
      temp.add(new KeyVal<Route, Freq>(r[i], new Freq(1, ts[i])));

    assertTrue(tmf.getMaxTen().equals(temp));
  }

  @Test
  public void testGetMaxTen8() {
    Vector<KeyVal<Route, Freq>> temp = new Vector<KeyVal<Route, Freq>>();

    for(int i=0; i<11; i++)
      tmf.update(r[i], new Freq(1, ts[i]));
    tmf.update(r[1], new Freq(1, ts[11]));

    temp.add(new KeyVal<Route, Freq>(r[1], new Freq(2, ts[11])));
    for(int i=10; i>1; i--)
      temp.add(new KeyVal<Route, Freq>(r[i], new Freq(1, ts[i])));

    assertTrue(tmf.getMaxTen().equals(temp));
  }

  @Test
  public void testGetMaxTen9() {
    Vector<KeyVal<Route, Freq>> temp = new Vector<KeyVal<Route, Freq>>();

    for(int i=0; i<11; i++)
      tmf.update(r[i], new Freq(1, ts[i]));
    tmf.update(r[2], new Freq(1, ts[11]));

    temp.add(new KeyVal<Route, Freq>(r[2], new Freq(2, ts[11])));
    for(int i=10; i>2; i--)
      temp.add(new KeyVal<Route, Freq>(r[i], new Freq(1, ts[i])));
    temp.add(new KeyVal<Route, Freq>(r[1], new Freq(1, ts[1])));

    assertTrue(tmf.getMaxTen().equals(temp));
  }

  @Test
  public void testGetMaxTen10() {
    Vector<KeyVal<Route, Freq>> temp = new Vector<KeyVal<Route, Freq>>();

    for(int i=0; i<14; i++)
      tmf.update(r[i], new Freq(1, ts[i]));
    tmf.update(r[8], new Freq(-1, ts[14]));

    for(int i=13; i>8; i--)
      temp.add(new KeyVal<Route, Freq>(r[i], new Freq(1, ts[i])));
    for(int i=7; i>2; i--)
      temp.add(new KeyVal<Route, Freq>(r[i], new Freq(1, ts[i])));

    assertTrue(tmf.getMaxTen().equals(temp));
  }

  @Test
  public void testGetMaxTen11() {
    Vector<KeyVal<Route, Freq>> temp = new Vector<KeyVal<Route, Freq>>();

    for(int i=0; i<6; i++)
      tmf.update(r[i], new Freq(1, ts[i]));
    for(int i=6; i<14; i++)
      tmf.update(r[i], new Freq(2, ts[i]));
    tmf.update(r[8], new Freq(-1, ts[14]));

    for(int i=13; i>8; i--)
      temp.add(new KeyVal<Route, Freq>(r[i], new Freq(2, ts[i])));
    temp.add(new KeyVal<Route, Freq>(r[7], new Freq(2, ts[7])));
    temp.add(new KeyVal<Route, Freq>(r[6], new Freq(2, ts[6])));
    temp.add(new KeyVal<Route, Freq>(r[8], new Freq(1, ts[8])));
    temp.add(new KeyVal<Route, Freq>(r[5], new Freq(1, ts[5])));
    temp.add(new KeyVal<Route, Freq>(r[4], new Freq(1, ts[4])));

    assertTrue(tmf.getMaxTen().equals(temp));
  }

  @Test
  public void testGetMaxTen12() {
    Vector<KeyVal<Route, Freq>> temp = new Vector<KeyVal<Route, Freq>>();

    for(int i=0; i<14; i++)
      tmf.update(r[i], new Freq(2, ts[i]));
    tmf.update(r[3], new Freq(-1, ts[14]));
    tmf.update(r[4], new Freq(-2, ts[15]));

    for(int i=13; i>4; i--)
      temp.add(new KeyVal<Route, Freq>(r[i], new Freq(2, ts[i])));
    temp.add(new KeyVal<Route, Freq>(r[2], new Freq(2, ts[2])));

    assertTrue(tmf.getMaxTen().equals(temp));
  }

  @Test
  public void testGetMaxTen13() {
    Vector<KeyVal<Route, Freq>> temp = new Vector<KeyVal<Route, Freq>>();

    for(int i=0; i<14; i++)
      tmf.update(r[i], new Freq(10, ts[i]));
    tmf.update(r[6], new Freq(-8, ts[14]));

    for(int i=13; i>6; i--)
      temp.add(new KeyVal<Route, Freq>(r[i], new Freq(10, ts[i])));
    for(int i=5; i>2; i--)
      temp.add(new KeyVal<Route, Freq>(r[i], new Freq(10, ts[i])));

    assertTrue(tmf.getMaxTen().equals(temp));
  }

  @Test
  public void testGetMaxTen14() {
    Vector<KeyVal<Route, Freq>> temp = new Vector<KeyVal<Route, Freq>>();

    for(int i=0; i<14; i++)
      tmf.update(r[i], new Freq(14-i, ts[i]));

    for(int i=0; i<10; i++)
      temp.add(new KeyVal<Route, Freq>(r[i], new Freq(14-i, ts[i])));

    assertTrue(tmf.getMaxTen().equals(temp));
  }
}
