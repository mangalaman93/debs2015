import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class GeoTest {
	
	Geo g1;

	@Before
	public void setUp() throws Exception {
		g1 = new Geo(-74.913585f,41.474937f,500,500,150000,150000);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1() {
		assertTrue(g1.translate(-74.913585f,41.474937f).equals(new Area(1,1)));
	}
	
	@Test
	public void test2() {
		assertTrue(g1.translate(-73.956528f,40.716976f).equals(new Area(162,168)));
	}
	
	@Test
	public void test3() {
		assertTrue(g1.translate(-73.956528f,41.474937f).equals(new Area(162,1)));
	}
	
	@Test
	public void test4() {
		assertTrue(g1.translate(-74.913585f,40.716976f).equals(new Area(1,168)));
	}
	
	@Test
	public void test5() {
		assertTrue(g1.translate(0.000000f,0.000000f).equals(new Area(-1,-1)));
	}
	
	@Test
	public void test6() {
		assertTrue(g1.translate(-74.913585f,0.000000f).equals(new Area(-1,-1)));
	}
	
}
