package nxcs.testbed.unitesting;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

import nxcs.testbed.DST;

public class DSTTest {
	private static final double DELTA = 1e-15;

	@Test
	public void testDST_when_load_file_to_dst_successs() {
		try {
			DST dst = new DST("./data/DST.csv");

			assertEquals(dst.getOpenLocations().get(0), new Point(0, 0));
			assertEquals(dst.getFinalStates().get(0), new Point(0, 1));
			assertEquals(dst.getFinalStates().size(), 3);
			assertEquals(dst.getRewardGrid().get(0).getGridReward(), 1.0d, DELTA);
		} catch (Exception ex) {
		}
	}

	@Test
	public void testDST_when_a_point_is_valid() {
		try {
			int x = 0;
			int y = 1;
			DST dst = new DST("./data/DST.csv");
			boolean result = dst.isValidPosition(x, y);
			assertEquals(result, true);
		} catch (Exception ex) {

		}
	}
}
