import static org.junit.Assert.*;
import java.util.Random;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class McTest {
  public Mc mc;

  @Before
  public void setUp() throws Exception {
    mc = new Mc();
  }

  @After
  public void tearDown() throws Exception {
  }

  public static int randInt(int min, int max) {
    Random rand = new Random();
    int randomNum = rand.nextInt((max - min) + 1) + min;
    return randomNum;
  }

  @Test
  public void test1() {
    mc.insert(1, 0.4f);
    mc.insert(2, 0.4f);
    mc.insert(3, 3.5f);
    mc.insert(4, 4.6f);
    mc.insert(5, 0.5f);
    mc.insert(6, 0.6f);
    mc.insert(7, 0.4f);
    mc.insert(8, 2.4f);
    assertTrue(mc.getMedian() == 0.55f);
  }

  @Test
  public void test2() {
    for(int i=1; i<4000; i++) {
      mc.insert(i, i);
    }
    assertEquals(2000,mc.getMedian(),0.001);
  }

  @Test
  public void test3() {
    for(int i=1; i<80000; i++) {
      mc.insert(i, i);
    }
    assertEquals(40000,mc.getMedian(),0.001);
  }

  @Test
  public void test4() {
    for(int i=1; i<2; i++) {
      mc.insert(i, i);
    }
    assertEquals(1,mc.getMedian(),0.001);
  }

  @Test
  public void test5() {
    for(int i=1; i<24*3600+1; i++) {
      mc.insert(i, i);
    }
    mc.insert(24*3600+2, 100);
    assertEquals(12*3600,mc.getMedian(),0.001);
  }

  @Test
  public void test6() {
    for(int i=1; i<1000; i++) {
      mc.insert(i, randInt(0,99));
    }
    for(int i=1; i<1000; i++) {
      mc.insert(i+1000, randInt(101,200));
    }
    mc.insert(2000, 100);
    assertEquals(100,mc.getMedian(),0.001);
  }

  @Test
  public void test7() {
    for(int i=1; i<1001; i++) {
      mc.insert(i, i);
    }
    for(int i=1; i<1000; i++) {
      mc.delete(i, i);
    }
    mc.insert(1001, 2000);
    assertEquals(1500,mc.getMedian(),0.001);
  }

  @Test
  public void test8() {
    for(int i=1; i<1001; i++) {
      mc.insert(i, i);
    }
    for(int i=1; i<1001; i+=2) {
      mc.delete(i, i);
    }
    assertEquals(501,mc.getMedian(),0.001);
  }

  @Test
  public void test9() {
    for(int i=1; i<1001; i++) {
      mc.insert(i, i);
    }
    for(int i=1; i<1001; i+=2) {
      mc.delete(i, i);
    }
    mc.insert(1001, 101);
    for(int i=2; i<1001; i+=2) {
      mc.delete(i, i);
    }
    assertEquals(101,mc.getMedian(),0.001);
  }
}
