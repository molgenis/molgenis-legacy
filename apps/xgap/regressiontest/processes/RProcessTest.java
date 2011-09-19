package regressiontest.processes;

import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import plugins.ronline.RProcess;

public class RProcessTest extends TestCase{

	RProcess rp;
	
	public RProcessTest() throws Exception{
		rp = new RProcess(3600);
		new Thread(rp).start();
	}

	@Before
	public void start() throws Exception{
		assertTrue(rp.isRunning());
	}
	
	@After
	public void stop() throws Exception{
		rp.quit();
	}
	
	@Test
	public void test1() throws Exception{
		assertEquals("[1] 1", rp.execute("1").get(0));
		assertEquals(0, rp.execute("q<-2").size());
		assertEquals(0, rp.execute("w<-3").size());
		assertEquals("[1] 5", rp.execute("q+w").get(0));
	}
	
	@Test
	public void test2() throws Exception{
		assertEquals("Error: object 'error' not found", rp.execute("error").get(0));
	}
	
	@Test
	public void test3() throws Exception{
		assertEquals(0, rp.execute("p<-pi").size(), 0);
		assertEquals("[1] TRUE", rp.execute("p==pi").get(0));
	}
	
	@Test
	public void test4() throws Exception{
		List<String> res = rp.execute("data.frame(cbind( 1, 1:2), cbind(3,1:2))");
		assertEquals(("  X1 X2 X1.1 X2.1"), res.get(0));
		assertEquals(("1  1  1    3    1"), res.get(1));
		assertEquals(("2  1  2    3    2"), res.get(2));
	}
	
}
