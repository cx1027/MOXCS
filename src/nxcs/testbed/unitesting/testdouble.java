package nxcs.testbed.unitesting;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

public class testdouble {
	private static final double DELTA = 1e-3;

	@Test
	public void test() {

		double a = 0.555;
		double b = 1.00;
		//assertEquals(a * 10 % 10, 5, DELTA);
		assertEquals(b * 10 % 10, 0, DELTA);
		assertEquals(a * 10 % 10 > 0, true);
		assertEquals(b * 10 % 10 > 0, false);
	}

}
