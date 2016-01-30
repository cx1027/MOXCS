package nxcs.testbed.unitesting;
import static java.util.stream.Collectors.*;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

import nxcs.Qvector;

public class testArrayListQvector {

	@Test
	public void test_EQUALS_when_two_Arraylist_are_equal() {

		ArrayList<Qvector> a1 = new ArrayList<Qvector>();
		ArrayList<Qvector> a2 = new ArrayList<Qvector>();
		a1.add(new Qvector(1, 1));
		a1.add(new Qvector(1, 2));

		a2.add(new Qvector(1, 1));
		a2.add(new Qvector(1, 2));

		assertEquals(a1.equals(a2), true);
	}

	@Test
	public void test_EQUALS_when_two_Arraylist_are_NOT_equal() {

		ArrayList<Qvector> a1 = new ArrayList<Qvector>();
		ArrayList<Qvector> a2 = new ArrayList<Qvector>();
		a1.add(new Qvector(1, 1));
		a1.add(new Qvector(1, 2));

		a2.add(new Qvector(1, 1));
		a2.add(new Qvector(1, 3));

		assertEquals(a1.equals(a2), false);
	}

	@Test
	public void test_EQUALS_when_two_Arraylist_are_NOT_equal_bylength() {

		ArrayList<Qvector> a1 = new ArrayList<Qvector>();
		ArrayList<Qvector> a2 = new ArrayList<Qvector>();
		a1.add(new Qvector(1, 1));
		a1.add(new Qvector(1, 2));

		a2.add(new Qvector(1, 1));
		assertEquals(a1.equals(a2), false);
		assertEquals(a1.size() > a2.size(), true);
	}

	@Test
	public void test_when_copy_by_ArrayList_Constructor_is_shadow_copy_then_should_point_to_same_object() {

		ArrayList<Qvector> a1 = new ArrayList<Qvector>();
		a1.add(new Qvector(1, 1));

		ArrayList<Qvector> a2 = new ArrayList<Qvector>(a1);
		a2.get(0).set(0, 100d);

		assertEquals(a2.get(0).get(0) == 100d, true);
		assertEquals(a1.get(0).get(0) == 100d, true);
		assertEquals(a1.equals(a2), true);
	}

	@Test
	public void test_COPY_when_copy_with_custom_clone_should_point_to_diff_objects(){

		ArrayList<Qvector> a1 = new ArrayList<Qvector>();
		a1.add(new Qvector(1, 1));

		ArrayList<Qvector> a2 = new ArrayList<Qvector>();

		for (Qvector q : a1) {
			a2.add(q.clone());
		}
		a2.get(0).set(0, 100d);
		
		assertEquals(a2.get(0).get(0) == 100d, true);
		assertEquals(a1.get(0).get(0) == 100d, false);
		assertEquals(a1.equals(a2), false);
	}
	
	@Test
	public void test_when_copy_with_custom_clone_and_java8_new_feature(){

		ArrayList<Qvector> a1 = new ArrayList<Qvector>();
		a1.add(new Qvector(1, 1));

		ArrayList<Qvector> a2 = a1.stream().map(d -> d.clone()).collect(toCollection(ArrayList::new));
 
		//modify a2
		a2.get(0).set(0, 100d);
		
		assertEquals(a2.get(0).get(0) == 100d, true);
		assertEquals(a1.get(0).get(0) == 100d, false);
		assertEquals(a1.equals(a2), false);
	}
	
//	@Test
//	public void test_when_copy_by_clone_should_point_to_diff_objects() {
//
//		ArrayList<Qvector> a1 = new ArrayList<Qvector>();
//		a1.add(new Qvector(1, 1));
//
//		ArrayList<Qvector> a2 = new ArrayList<Qvector>();
//
//		Collections.copy(a2, a1);
//		
//		a2.get(0).set(0, 100d);
//		
//		assertEquals(a2.get(0).get(0) == 100d, true);
//		assertEquals(a1.get(0).get(0) == 100d, true);
//		assertEquals(a1.equals(a2), true);
//	}
}
