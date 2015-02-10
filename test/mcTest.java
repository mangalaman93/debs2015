import static org.junit.Assert.*;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class mcTest {
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
    mc.insert(0.4f);
    mc.insert(0.4f);
    mc.insert(3.5f);
    mc.insert(4.6f);
    mc.insert(0.5f);
    mc.insert(0.6f);
    mc.insert(0.4f);
    mc.insert(2.4f);
    assertTrue(mc.getMedian() == 0.55f);
  }

  @Test
  public void test2() {
    for(float i=1; i<4000; i++) {
      mc.insert(i);
    }
    assertEquals(2000,mc.getMedian(),0.001);
  }

  @Test
  public void test3() {
    for(float i=1; i<80000; i++) {
      mc.insert(i);
    }
    assertEquals(40000,mc.getMedian(),0.001);
  }

  @Test
  public void test4() {
    for(float i=1; i<2; i++) {
      mc.insert(i);
    }
    assertEquals(1,mc.getMedian(),0.001);
  }

  @Test
  public void test5() {
    for(float i=1; i<24*3600+1; i++) {
      mc.insert(i);
    }
    mc.insert(100);
    assertEquals(12*3600,mc.getMedian(),0.001);
  }

  @Test
  public void test6() {
    for(int i=1; i<1000; i++) {
      mc.insert(randInt(0,99));
    }
    for(int i=1; i<1000; i++) {
      mc.insert(randInt(101,200));
    }
    mc.insert(100);
    assertEquals(100,mc.getMedian(),0.001);
  }

  @Test
  public void test7() {
    for(float i=1; i<1001; i++) {
      mc.insert(i);
    }
    for(float i=1; i<1000; i++) {
      mc.delete(i);
    }
    mc.insert(2000);
    assertEquals(1500,mc.getMedian(),0.001);
  }

  @Test
  public void test8() {
    for(float i=1; i<1001; i++) {
      mc.insert(i);
    }
    for(float i=1; i<1001; i+=2) {
      mc.delete(i);
    }
    assertEquals(501,mc.getMedian(),0.001);
  }

  @Test
  public void test9() {
    for(float i=1; i<1001; i++) {
      mc.insert(i);
    }
    for(float i=1; i<1001; i+=2) {
      mc.delete(i);
    }
    mc.insert(101);
    for(float i=2; i<1001; i+=2) {
      mc.delete(i);
    }
    assertEquals(101,mc.getMedian(),0.001);
  }
}
