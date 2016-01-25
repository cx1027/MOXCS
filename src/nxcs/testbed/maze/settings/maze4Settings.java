package nxcs.testbed.maze.settings;

import java.awt.Point;
import java.util.ArrayList;

import nxcs.stats.StepSnapshot;

public class maze4Settings implements IGetMazeTestSettings {
	private int finalStates;
	private int trailTimes;
	

	public maze4Settings(int finalStates, int trailTimes) {
		this.finalStates = finalStates;
		this.trailTimes = trailTimes;
	}

	@Override
	public String getTestFile() {
		return "data/maze4.txt";
	}

	@Override
	public String getMazeName() {
		return "maze4";
	}

	@Override
	public ArrayList<ArrayList<StepSnapshot>> getOpenLocationExpectPaths() {
		ArrayList<ArrayList<StepSnapshot>> expect = new ArrayList<ArrayList<StepSnapshot>>();
		ArrayList<StepSnapshot> e11 = new ArrayList<StepSnapshot>();
		e11.add(new StepSnapshot(new Point(1, 1), new Point(1, 6), 7));
		expect.add(e11);
		ArrayList<StepSnapshot> e21 = new ArrayList<StepSnapshot>();
		e21.add(new StepSnapshot(new Point(2, 1), new Point(1, 6), 6));
		expect.add(e21);
		ArrayList<StepSnapshot> e41 = new ArrayList<StepSnapshot>();
		e41.add(new StepSnapshot(new Point(4, 1), new Point(1, 6), 10));
		e41.add(new StepSnapshot(new Point(4, 1), new Point(6, 1), 2));
		expect.add(e41);
		ArrayList<StepSnapshot> e51 = new ArrayList<StepSnapshot>();
		e51.add(new StepSnapshot(new Point(5, 1), new Point(6, 1), 1));
		e51.add(new StepSnapshot(new Point(5, 1), new Point(1, 6), 9));
		expect.add(e51);
		ArrayList<StepSnapshot> e22 = new ArrayList<StepSnapshot>();
		e22.add(new StepSnapshot(new Point(2, 2), new Point(1, 6), 5));
		expect.add(e22);
		ArrayList<StepSnapshot> e32 = new ArrayList<StepSnapshot>();
		e32.add(new StepSnapshot(new Point(3, 2), new Point(1, 6), 6));
		expect.add(e32);
		ArrayList<StepSnapshot> e52 = new ArrayList<StepSnapshot>();
		e52.add(new StepSnapshot(new Point(5, 2), new Point(6, 1), 2));
		e52.add(new StepSnapshot(new Point(5, 2), new Point(1, 6), 8));
		expect.add(e52);
		ArrayList<StepSnapshot> e62 = new ArrayList<StepSnapshot>();
		e62.add(new StepSnapshot(new Point(6, 2), new Point(6, 1), 1));
		e62.add(new StepSnapshot(new Point(6, 2), new Point(1, 6), 9));
		expect.add(e62);
		ArrayList<StepSnapshot> e23 = new ArrayList<StepSnapshot>();
		e23.add(new StepSnapshot(new Point(2, 3), new Point(1, 6), 4));
		expect.add(e23);
		ArrayList<StepSnapshot> e43 = new ArrayList<StepSnapshot>();
		e43.add(new StepSnapshot(new Point(4, 3), new Point(6, 1), 4));
		e43.add(new StepSnapshot(new Point(4, 3), new Point(1, 6), 6));
		expect.add(e43);
		ArrayList<StepSnapshot> e53 = new ArrayList<StepSnapshot>();
		e53.add(new StepSnapshot(new Point(5, 3), new Point(6, 1), 3));
		e53.add(new StepSnapshot(new Point(5, 3), new Point(1, 6), 7));
		expect.add(e53);
		ArrayList<StepSnapshot> e14 = new ArrayList<StepSnapshot>();
		e14.add(new StepSnapshot(new Point(1, 4), new Point(1, 6), 4));
		expect.add(e14);
		ArrayList<StepSnapshot> e24 = new ArrayList<StepSnapshot>();
		e24.add(new StepSnapshot(new Point(2, 4), new Point(1, 6), 3));
		expect.add(e24);
		ArrayList<StepSnapshot> e34 = new ArrayList<StepSnapshot>();
		e34.add(new StepSnapshot(new Point(3, 4), new Point(1, 6), 4));
		expect.add(e34);
		ArrayList<StepSnapshot> e = new ArrayList<StepSnapshot>();
		e.add(new StepSnapshot(new Point(4, 4), new Point(1, 6), 5));
		expect.add(e);
		ArrayList<StepSnapshot> e54 = new ArrayList<StepSnapshot>();
		e54.add(new StepSnapshot(new Point(5, 4), new Point(6, 1), 4));
		e54.add(new StepSnapshot(new Point(5, 4), new Point(1, 6), 6));
		expect.add(e54);
		ArrayList<StepSnapshot> e64 = new ArrayList<StepSnapshot>();
		e64.add(new StepSnapshot(new Point(6, 4), new Point(6, 1), 5));
		e64.add(new StepSnapshot(new Point(6, 4), new Point(1, 6), 7));
		expect.add(e64);
		ArrayList<StepSnapshot> e25 = new ArrayList<StepSnapshot>();
		e25.add(new StepSnapshot(new Point(2, 5), new Point(1, 6), 2));
		expect.add(e25);
		ArrayList<StepSnapshot> e45 = new ArrayList<StepSnapshot>();
		e45.add(new StepSnapshot(new Point(4, 5), new Point(1, 6), 4));
		expect.add(e45);
		ArrayList<StepSnapshot> e55 = new ArrayList<StepSnapshot>();
		e55.add(new StepSnapshot(new Point(5, 5), new Point(1, 6), 5));
		expect.add(e55);
		ArrayList<StepSnapshot> e65 = new ArrayList<StepSnapshot>();
		e65.add(new StepSnapshot(new Point(6, 5), new Point(1, 6), 6));
		expect.add(e65);
		ArrayList<StepSnapshot> e26 = new ArrayList<StepSnapshot>();
		e26.add(new StepSnapshot(new Point(2, 6), new Point(1, 6), 1));
		expect.add(e26);
		ArrayList<StepSnapshot> e36 = new ArrayList<StepSnapshot>();
		e36.add(new StepSnapshot(new Point(3, 6), new Point(1, 6), 2));
		expect.add(e36);
		ArrayList<StepSnapshot> e46 = new ArrayList<StepSnapshot>();
		e46.add(new StepSnapshot(new Point(4, 6), new Point(1, 6), 3));
		expect.add(e46);
		ArrayList<StepSnapshot> e66 = new ArrayList<StepSnapshot>();
		e66.add(new StepSnapshot(new Point(6, 6), new Point(1, 6), 7));
		expect.add(e66);

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
