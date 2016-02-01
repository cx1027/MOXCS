package nxcs.testbed.unitesting;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Comparator;

import org.junit.Test;

import nxcs.HyperVolumn;
import nxcs.Qvector;

public class HyperVolumnTest {

	@Test
	public void test_sortQvector_when_sorting_as_ASC() {
		Qvector q1 = new Qvector(1, 2);
		Qvector q2 = new Qvector(2, 3);
		Qvector q3 = new Qvector(3, 3);
		Qvector q4 = new Qvector(2, 4);
		ArrayList<Qvector> qlist = new ArrayList<Qvector>();
		qlist.add(q1);
		qlist.add(q2);
		qlist.add(q3);
		qlist.add(q4);
		HyperVolumn hv = new HyperVolumn();
		ArrayList<Qvector> actual = hv.sortQvector(qlist);

		assertEquals(actual.get(2).equals(q4), true);
		assertEquals(actual.get(3).equals(q3), true);

	}

	@Test
	public void test_sortQvector_when_sorting_as_ASC_by_passing_in_custom_comparetor() {
		Qvector q1 = new Qvector(1, 2);
		Qvector q2 = new Qvector(2, 3);
		Qvector q3 = new Qvector(3, 3);
		Qvector q4 = new Qvector(2, 4);
		ArrayList<Qvector> qlist = new ArrayList<Qvector>();
		qlist.add(q1);
		qlist.add(q2);
		qlist.add(q3);
		qlist.add(q4);
		HyperVolumn hv = new HyperVolumn();
		ArrayList<Qvector> actual = hv.sortQvector(qlist, new Comparator<Qvector>() {
			@Override
			public int compare(Qvector o1, Qvector o2) {
				return o1.get(0).compareTo(o2.get(0));
			}
		});

		assertEquals(actual.get(2).equals(q4), true);
		assertEquals(actual.get(3).equals(q3), true);

	}

	@Test
	public void test_calcHyperVolumn_should_equals_to_3() {
		Qvector q1 = new Qvector(1, 2);
		Qvector q2 = new Qvector(2, 1);

		ArrayList<Qvector> qlist = new ArrayList<Qvector>();
		qlist.add(q1);
		qlist.add(q2);
		HyperVolumn hv = new HyperVolumn();
		Qvector refer = new Qvector(0, 0);
		double actual = hv.calcHyperVolumn(qlist, refer);

		assertEquals(actual == 3, true);

	}

	@Test
	public void test_calcHyperVolumn_should_equals_to_4() {
		Qvector q1 = new Qvector(1, 2);
		Qvector q2 = new Qvector(3, 1);

		ArrayList<Qvector> qlist = new ArrayList<Qvector>();
		qlist.add(q1);
		qlist.add(q2);
		HyperVolumn hv = new HyperVolumn();
		Qvector refer = new Qvector(0, 0);
		double actual = hv.calcHyperVolumn(qlist, refer);

		assertEquals(actual == 4, true);

	}

	@Test
	public void test_calcHyperVolumn_three_Qvector_should_equals_to_425() {
		Qvector q1 = new Qvector(1, 2);
		Qvector q2 = new Qvector(1.5, 1.5);
		Qvector q3 = new Qvector(3, 1);

		ArrayList<Qvector> qlist = new ArrayList<Qvector>();
		qlist.add(q1);
		qlist.add(q2);
		qlist.add(q3);
		HyperVolumn hv = new HyperVolumn();
		Qvector refer = new Qvector(0, 0);
		double actual = hv.calcHyperVolumn(qlist, refer);

		assertEquals(actual == 4.25, true);

	}
}
