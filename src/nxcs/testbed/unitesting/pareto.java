package nxcs.testbed.unitesting;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import nxcs.ActionPareto;
import nxcs.NXCS;
import nxcs.ParetoCal;
import nxcs.Qvector;
import nxcs.distance.DistanceCalculator;

public class pareto {

	@Test
	public void testinter() {
		ParetoCal pareto;
		pareto = new ParetoCal();
		ArrayList<ActionPareto> NDdots = new ArrayList<ActionPareto>();

		NDdots.add(new ActionPareto(new Qvector(-5.0, 1.0), 0));
		NDdots.add(new ActionPareto(new Qvector(-7.0, 10.0), 0));
		NDdots.add(new ActionPareto(new Qvector(-5.0, 10.0), 0));
		NDdots.add(new ActionPareto(new Qvector(-7.0, 10.0), 0));
		NDdots.add(new ActionPareto(new Qvector(-6.0, 10.0), 0));

		List<ActionPareto> ParetoDotwithA = pareto.getPareto(NDdots);

		// assertEquals(r.get(0), x.get(0));
		assertEquals(ParetoDotwithA.get(0).getPareto(), new Qvector(-5.0, 10.0));
		assertEquals(ParetoDotwithA.size(), 1);

	}

}
