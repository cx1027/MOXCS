package nxcs.testbed.maze.settings;

import java.awt.Point;
import java.util.ArrayList;

import nxcs.stats.StepSnapshot;

public class maze5Settings implements IGetMazeTestSettings {
	private int finalStates;
	private int trailTimes;
	

	public maze5Settings(int finalStates, int trailTimes) {
		this.finalStates = finalStates;
		this.trailTimes = trailTimes;
	}

	@Override
	public String getTestFile() {
		return "data/maze5.txt";
	}

	@Override
	public String getMazeName() {
		return "maze5";
	}

	@Override
	public ArrayList<ArrayList<StepSnapshot>> getOpenLocationExpectPaths() {
		ArrayList<ArrayList<StepSnapshot>> expect = new ArrayList<ArrayList<StepSnapshot>>();
		ArrayList<StepSnapshot> e11 = new ArrayList<StepSnapshot>();
		e11.add(new StepSnapshot(new Point(1,1), new Point(1,7),  6));
		expect.add(e11);
		ArrayList<StepSnapshot> e21 = new ArrayList<StepSnapshot>();
		e21.add(new StepSnapshot(new Point(2,1), new Point(7,1),  5));
		e21.add(new StepSnapshot(new Point(2,1), new Point(1,7),  7));
		expect.add(e21);
		ArrayList<StepSnapshot> e31 = new ArrayList<StepSnapshot>();
		e31.add(new StepSnapshot(new Point(3,1), new Point(1,7),  8));
		e31.add(new StepSnapshot(new Point(3,1), new Point(7,1),  4));
		expect.add(e31);
		ArrayList<StepSnapshot> e41 = new ArrayList<StepSnapshot>();
		e41.add(new StepSnapshot(new Point(4,1), new Point(7,1),  3));
		e41.add(new StepSnapshot(new Point(4,1), new Point(1,7),  9));
		expect.add(e41);
		ArrayList<StepSnapshot> e51 = new ArrayList<StepSnapshot>();
		e51.add(new StepSnapshot(new Point(5,1), new Point(1,7),  10));
		e51.add(new StepSnapshot(new Point(5,1), new Point(7,1),  2));
		expect.add(e51);
		ArrayList<StepSnapshot> e61 = new ArrayList<StepSnapshot>();
		e61.add(new StepSnapshot(new Point(6,1), new Point(1,7),  11));
		e61.add(new StepSnapshot(new Point(6,1), new Point(7,1),  1));
		expect.add(e61);
		ArrayList<StepSnapshot> e12 = new ArrayList<StepSnapshot>();
		e12.add(new StepSnapshot(new Point(1,2), new Point(1,7),  5));
		expect.add(e12);
		ArrayList<StepSnapshot> e22 = new ArrayList<StepSnapshot>();
		e22.add(new StepSnapshot(new Point(2,2), new Point(1,7),  6));
		expect.add(e22);
		ArrayList<StepSnapshot> e42 = new ArrayList<StepSnapshot>();
		e42.add(new StepSnapshot(new Point(4,2), new Point(7,1),  4));
		e42.add(new StepSnapshot(new Point(4,2), new Point(1,7),  8));
		expect.add(e42);
		ArrayList<StepSnapshot> e72 = new ArrayList<StepSnapshot>();
		e72.add(new StepSnapshot(new Point(7,2), new Point(7,1),  1));
		e72.add(new StepSnapshot(new Point(7,2), new Point(1,7),  11));
		expect.add(e72);
		ArrayList<StepSnapshot> e13 = new ArrayList<StepSnapshot>();
		e13.add(new StepSnapshot(new Point(1,3), new Point(1,7),  4));
		expect.add(e13);
		ArrayList<StepSnapshot> e33 = new ArrayList<StepSnapshot>();
		e33.add(new StepSnapshot(new Point(3,3), new Point(1,7),  6));
		expect.add(e33);
		ArrayList<StepSnapshot> e43 = new ArrayList<StepSnapshot>();
		e43.add(new StepSnapshot(new Point(4,3), new Point(1,7),  7));
		e43.add(new StepSnapshot(new Point(4,3), new Point(7,1),  5));
		expect.add(e43);
		ArrayList<StepSnapshot> e53 = new ArrayList<StepSnapshot>();
		e53.add(new StepSnapshot(new Point(5,3), new Point(1,7),  8));
		e53.add(new StepSnapshot(new Point(5,3), new Point(7,1),  4));
		expect.add(e53);
		ArrayList<StepSnapshot> e63 = new ArrayList<StepSnapshot>();
		e63.add(new StepSnapshot(new Point(6,3), new Point(1,7),  9));
		e63.add(new StepSnapshot(new Point(6,3), new Point(7,1),  3));
		expect.add(e63);
		ArrayList<StepSnapshot> e73 = new ArrayList<StepSnapshot>();
		e73.add(new StepSnapshot(new Point(7,3), new Point(7,1),  2));
		e73.add(new StepSnapshot(new Point(7,3), new Point(1,7),  10));
		expect.add(e73);
		ArrayList<StepSnapshot> e14 = new ArrayList<StepSnapshot>();
		e14.add(new StepSnapshot(new Point(1,4), new Point(1,7),  3));
		expect.add(e14);
		ArrayList<StepSnapshot> e24 = new ArrayList<StepSnapshot>();
		e24.add(new StepSnapshot(new Point(2,4), new Point(1,7),  4));
		expect.add(e24);
		ArrayList<StepSnapshot> e34 = new ArrayList<StepSnapshot>();
		e34.add(new StepSnapshot(new Point(3,4), new Point(1,7),  5));
		expect.add(e34);
		ArrayList<StepSnapshot> e64 = new ArrayList<StepSnapshot>();
		e64.add(new StepSnapshot(new Point(6,4), new Point(1,7),  10));
		e64.add(new StepSnapshot(new Point(6,4), new Point(7,1),  4));
		expect.add(e64);
		ArrayList<StepSnapshot> e74 = new ArrayList<StepSnapshot>();
		e74.add(new StepSnapshot(new Point(7,4), new Point(7,1),  3));
		e74.add(new StepSnapshot(new Point(7,4), new Point(1,7),  11));
		expect.add(e74);
		ArrayList<StepSnapshot> e15 = new ArrayList<StepSnapshot>();
		e15.add(new StepSnapshot(new Point(1,5), new Point(1,7),  2));
		expect.add(e15);
		ArrayList<StepSnapshot> e35 = new ArrayList<StepSnapshot>();
		e35.add(new StepSnapshot(new Point(3,5), new Point(1,7),  4));
		expect.add(e35);
		ArrayList<StepSnapshot> e55 = new ArrayList<StepSnapshot>();
		e55.add(new StepSnapshot(new Point(5,5), new Point(1,7),  12));
		e55.add(new StepSnapshot(new Point(5,5), new Point(7,1),  6));
		expect.add(e55);
		ArrayList<StepSnapshot> e65 = new ArrayList<StepSnapshot>();
		e65.add(new StepSnapshot(new Point(6,5), new Point(1,7),  11));
		e65.add(new StepSnapshot(new Point(6,5), new Point(7,1),  5));
		expect.add(e65);
		ArrayList<StepSnapshot> e16 = new ArrayList<StepSnapshot>();
		e16.add(new StepSnapshot(new Point(1,6), new Point(1,7),  1));
		expect.add(e16);
		ArrayList<StepSnapshot> e36 = new ArrayList<StepSnapshot>();
		e36.add(new StepSnapshot(new Point(3,6), new Point(1,7),  3));
		expect.add(e36);
		ArrayList<StepSnapshot> e46 = new ArrayList<StepSnapshot>();
		e46.add(new StepSnapshot(new Point(4,6), new Point(1,7),  4));
		expect.add(e46);
		ArrayList<StepSnapshot> e66 = new ArrayList<StepSnapshot>();
		e66.add(new StepSnapshot(new Point(6,6), new Point(1,7),  12));
		e66.add(new StepSnapshot(new Point(6,6), new Point(7,1),  6));
		expect.add(e66);
		ArrayList<StepSnapshot> e76 = new ArrayList<StepSnapshot>();
		e76.add(new StepSnapshot(new Point(7,6), new Point(1,7),  13));
		e76.add(new StepSnapshot(new Point(7,6), new Point(7,1),  7));
		expect.add(e76);
		ArrayList<StepSnapshot> e27 = new ArrayList<StepSnapshot>();
		e27.add(new StepSnapshot(new Point(2,7), new Point(1,7),  1));
		expect.add(e27);
		ArrayList<StepSnapshot> e37 = new ArrayList<StepSnapshot>();
		e37.add(new StepSnapshot(new Point(3,7), new Point(1,7),  2));
		expect.add(e37);
		ArrayList<StepSnapshot> e47 = new ArrayList<StepSnapshot>();
		e47.add(new StepSnapshot(new Point(4,7), new Point(1,7),  3));
		expect.add(e47);
		ArrayList<StepSnapshot> e57 = new ArrayList<StepSnapshot>();
		e57.add(new StepSnapshot(new Point(5,7), new Point(1,7),  4));
		expect.add(e57);
		ArrayList<StepSnapshot> e77 = new ArrayList<StepSnapshot>();
		e77.add(new StepSnapshot(new Point(7,7), new Point(1,7),  14));
		e77.add(new StepSnapshot(new Point(7,7), new Point(7,1),  8));
		expect.add(e77);

		return expect;
	}
	
	@Override
	public int getFinalStates() {
		return this.finalStates;
	}

	@Override
	public int getTrailTimes() {
		return this.trailTimes;
	}

}
