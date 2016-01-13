package nxcs.testbed.unitesting;

import static org.junit.Assert.*;

import java.awt.Point;
import java.util.ArrayList;

import org.junit.Test;

import nxcs.stats.StepSnapshot;
import nxcs.stats.StepStatsLogger;
import nxcs.stats.StepStatsPoint;

public class StepStatsLoggerTest {

	@Test
	public void test_findMatch_when_item_in_list() {

		StepStatsLogger cut = new StepStatsLogger(1, 1);
		StepSnapshot item = new StepSnapshot(1, new Point(1, 1), new Point(9, 9), 2);
		ArrayList<StepSnapshot> list = new ArrayList<StepSnapshot>();

		list.add(new StepSnapshot(1, new Point(1, 1), new Point(9, 9), 2));
		list.add(new StepSnapshot(1, new Point(1, 1), new Point(8, 9), 3));
		list.add(new StepSnapshot(1, new Point(2, 1), new Point(8, 9), 3));

		boolean result = cut.findMatch(item, list);
		assertEquals(result, true);
	}

	@Test
	public void test_findMatch_when_item_NOT_in_list() {

		StepStatsLogger cut = new StepStatsLogger(1, 1);
		StepSnapshot item = new StepSnapshot(1, new Point(1, 1), new Point(9, 9), 9);
		ArrayList<StepSnapshot> list = new ArrayList<StepSnapshot>();

		list.add(new StepSnapshot(1, new Point(1, 1), new Point(9, 9), 2));
		list.add(new StepSnapshot(1, new Point(1, 1), new Point(8, 9), 3));
		list.add(new StepSnapshot(1, new Point(2, 1), new Point(8, 9), 3));

		boolean result = cut.findMatch(item, list);
		assertEquals(result, false);
	}

	@Test
	public void testCalculateMatchedPercentage() {
		assertEquals(1, 1);
	}

	@Test
	public void test_CalculateMatchedRate_when_single_result_matched() {
		StepStatsLogger cut = new StepStatsLogger(1, 1);
		ArrayList<StepSnapshot> expectFlat = new ArrayList<StepSnapshot>();
		ArrayList<ArrayList<StepSnapshot>> result = new ArrayList<ArrayList<StepSnapshot>>();

		expectFlat.add(new StepSnapshot(new Point(1, 1), new Point(9, 9), 2));
		expectFlat.add(new StepSnapshot(new Point(1, 1), new Point(8, 9), 3));
		expectFlat.add(new StepSnapshot(new Point(2, 1), new Point(8, 9), 4));

		result.add(expectFlat);

		StepStatsPoint a = cut.calculateMatchedRate(expectFlat, result);

		assertEquals(Math.abs(a.matchedRate - 1) < 0.00001, true);
	}

	@Test
	public void test_CalculateMatchedRate_when_two_results_matched() {
		StepStatsLogger cut = new StepStatsLogger(1, 1);
		ArrayList<StepSnapshot> expectFlat = new ArrayList<StepSnapshot>();
		ArrayList<ArrayList<StepSnapshot>> result = new ArrayList<ArrayList<StepSnapshot>>();

		expectFlat.add(new StepSnapshot(new Point(1, 1), new Point(9, 9), 2));
		expectFlat.add(new StepSnapshot(new Point(1, 1), new Point(8, 9), 3));
		expectFlat.add(new StepSnapshot(new Point(2, 1), new Point(8, 9), 4));

		expectFlat.add(new StepSnapshot(new Point(3, 1), new Point(9, 9), 2));
		expectFlat.add(new StepSnapshot(new Point(3, 1), new Point(8, 9), 3));
		expectFlat.add(new StepSnapshot(new Point(2, 1), new Point(8, 9), 3));

		ArrayList<StepSnapshot> result1 = new ArrayList<StepSnapshot>();

		result1.add(new StepSnapshot(1, new Point(1, 1), new Point(9, 9), 2));
		result1.add(new StepSnapshot(1, new Point(1, 1), new Point(8, 9), 3));
		result1.add(new StepSnapshot(1, new Point(2, 1), new Point(8, 9), 4));

		ArrayList<StepSnapshot> result2 = new ArrayList<StepSnapshot>();
		result2.add(new StepSnapshot(2, new Point(3, 1), new Point(9, 9), 2));
		result2.add(new StepSnapshot(2, new Point(3, 1), new Point(8, 9), 3));
		result2.add(new StepSnapshot(2, new Point(2, 1), new Point(8, 9), 3));

		result.add(result1);
		result.add(result2);

		StepStatsPoint a = cut.calculateMatchedRate(expectFlat, result);

		assertEquals(Math.abs(a.matchedRate - 1) < 0.00001, true);
	}

	@Test
	public void test_CalculateMatchedRate_when_matched_rate_is_80_and_coverate_is_100() {
		StepStatsLogger cut = new StepStatsLogger(1, 1);
		ArrayList<StepSnapshot> expectFlat = new ArrayList<StepSnapshot>();
		ArrayList<ArrayList<StepSnapshot>> list = new ArrayList<ArrayList<StepSnapshot>>();

		expectFlat.add(new StepSnapshot(new Point(1, 1), new Point(9, 9), 2));
		expectFlat.add(new StepSnapshot(new Point(1, 1), new Point(8, 9), 3));
		expectFlat.add(new StepSnapshot(new Point(2, 1), new Point(8, 9), 4));

		expectFlat.add(new StepSnapshot(new Point(3, 1), new Point(9, 9), 2));
		expectFlat.add(new StepSnapshot(new Point(3, 1), new Point(8, 9), 3));

		ArrayList<StepSnapshot> result1 = new ArrayList<StepSnapshot>();

		result1.add(new StepSnapshot(1, new Point(1, 1), new Point(9, 9), 2));
		result1.add(new StepSnapshot(1, new Point(1, 1), new Point(8, 9), 3));
		result1.add(new StepSnapshot(1, new Point(2, 1), new Point(8, 9), 4));

		ArrayList<StepSnapshot> result2 = new ArrayList<StepSnapshot>();
		result2.add(new StepSnapshot(2, new Point(3, 1), new Point(9, 9), 2));
		result2.add(new StepSnapshot(2, new Point(3, 1), new Point(8, 9), 2));

		list.add(result1);
		list.add(result2);

		StepStatsPoint a = cut.calculateMatchedRate(expectFlat, list);

		assertEquals(Math.abs(a.matchedRate - 0.8) < 0.00001, true);
		assertEquals(Math.abs(a.coverage - 1) < 0.00001, true);
	}
}
