package nxcs.testbed.unitesting;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import nxcs.Action;
import nxcs.ActionPareto;
import nxcs.ParetoCal;

public class ParetoTest {

	@Test
	public void test2d() {
		ArrayList<ActionPareto> candidateList = new ArrayList<ActionPareto>();
		List<Double> va1 = new ArrayList<Double>();
		va1.add(-1d);
		va1.add(1d);
		ActionPareto v1 = new ActionPareto(va1, Action.DOWN);

		candidateList.add(v1);

		List<Double> va2 = new ArrayList<Double>();
		va2.add(0d);
		va2.add(1d);
		ActionPareto v2 = new ActionPareto(va2, Action.DOWN);
		candidateList.add(v2);

		List<Double> va3 = new ArrayList<Double>();
		va3.add(-2d);
		va3.add(2d);
		ActionPareto v3 = new ActionPareto(va3, Action.DOWN);

		candidateList.add(v3);

		ParetoCal pUtil = new ParetoCal();

		List<ActionPareto> result = pUtil.getPareto(candidateList);
		assertEquals(v2, result.get(0));
		assertEquals(v3, result.get(1));

	}

	@Test
	public void test3d() {
		ArrayList<ActionPareto> candidateList = new ArrayList<ActionPareto>();
//		ActionPareto v1 = new ActionPareto();
//		v1.add(-1d);
//		v1.add(1d);
//		v1.add(0d);
//		candidateList.add(v1);
//
//		ActionPareto v2 = new ActionPareto();
//		v2.add(0d);
//		v2.add(1d);
//		v2.add(1d);
//		candidateList.add(v2);
//
//		ActionPareto v3 = new ActionPareto();
//		v3.add(-2d);
//		v3.add(2d);
//		v3.add(1d);
//		candidateList.add(v3);
//
//		ActionPareto v4 = new ActionPareto();
//		v4.add(0d);
//		v4.add(1d);
//		v4.add(2d);
//		candidateList.add(v4);
		
		
		List<Double> va1 = new ArrayList<Double>();
		va1.add(-1d);
		va1.add(1d);
		va1.add(0d);
		ActionPareto v1 = new ActionPareto(va1, Action.DOWN);
		
		candidateList.add(v1);
		
		List<Double> va2 = new ArrayList<Double>();
		va2.add(0d);
		va2.add(1d);
		va2.add(1d);
		ActionPareto v2 = new ActionPareto(va2, Action.DOWN);
		candidateList.add(v2);
		
		List<Double> va3 = new ArrayList<Double>();
		va3.add(-2d);
		va3.add(2d);
		va3.add(1d);
		ActionPareto v3 = new ActionPareto(va3, Action.DOWN);
		
		candidateList.add(v3);
		
		List<Double> va4 = new ArrayList<Double>();
		va4.add(-2d);
		va4.add(2d);
		va4.add(2d);
		ActionPareto v4 = new ActionPareto(va4, Action.DOWN);
		
		candidateList.add(v4);
		
		

		ParetoCal pUtil = new ParetoCal();

		List<ActionPareto> result = pUtil.getPareto(candidateList);
		assertEquals(v2, result.get(0));
		assertEquals(v4, result.get(1));

	}

}
