package nxcs.testbed.unitesting;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import nxcs.NXCS;
import nxcs.Qvector;
import nxcs.distance.DistanceCalculator;

public class minv {

//	@Test
//	public void testinter() {
//
//		NXCS nxcs = new NXCS();
//		minDistance dis = new minDistance();
//		ArrayList<Qvector> v = new ArrayList<Qvector>();
//		v.add(new Qvector(0, 0));
//		v.add(new Qvector(-1, 0));
//		v.add(new Qvector(-2, 2));
//
//		ArrayList<Qvector> m = new ArrayList<Qvector>();
//		m.add(new Qvector(0, 0));
//		// m.add(new Qvector(-2, 2));
//
//		ArrayList<Qvector> r = new ArrayList<Qvector>();
//		r = dis.getIntersectionSet(v, m);
//
//		ArrayList<Qvector> x = new ArrayList<Qvector>();
//		x.add(new Qvector(0, 0));
//
//		// assertEquals(r.get(0), x.get(0));
//		assertEquals(r, x);
//
//	}
//
//	@Test
//	public void testUniSet() {
//
//		NXCS nxcs = new NXCS();
//		minDistance dis = new minDistance();
//		ArrayList<Qvector> v = new ArrayList<Qvector>();
//		v.add(new Qvector(0, 0));
//		v.add(new Qvector(-1, 0));
//		v.add(new Qvector(-2, 2));
//
//		ArrayList<Qvector> m = new ArrayList<Qvector>();
//		m.add(new Qvector(0, 0));
//		m.add(new Qvector(-2, 2));
//		m.add(new Qvector(-3, 2));
//
//		ArrayList<Qvector> r = new ArrayList<Qvector>();
//		r = dis.getUnionSet(v, m);
//
//		ArrayList<Qvector> x = new ArrayList<Qvector>();
//		x.add(new Qvector(0, 0));
//		x.add(new Qvector(-1, 0));
//		x.add(new Qvector(-2, 2));
//		x.add(new Qvector(-3, 2));
//
//		// assertEquals(r.get(0), x.get(0));
//		assertEquals(r, x);
//	}

	@Test
	public void testJDis() {
		final double delta = 0.00001d;
		NXCS nxcs = new NXCS();
		DistanceCalculator dis = new DistanceCalculator();
		ArrayList<Qvector> v = new ArrayList<Qvector>();
		v.add(new Qvector(0, 0));
//		v.add(new Qvector(-1, 0));
//		v.add(new Qvector(-2, 2));

		ArrayList<Qvector> m = new ArrayList<Qvector>();
		m.add(new Qvector(0, 0));
//		m.add(new Qvector(-2, 2));
//		m.add(new Qvector(-3, 2));

		double J = dis.get10Distance(v, m);

		// assertEquals(r.get(0), x.get(0));
		assertEquals(0, J, delta);
	}

}
