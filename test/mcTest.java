import static org.junit.Assert.*;
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

  @Test
  public void testGetMedian() {
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
}
