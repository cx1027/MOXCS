package nxcs.stats;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import nxcs.Qvector;

public class StepSnapshot {
	private int timestamp;
	private Point openState;
	private Point finalState;
	private int steps;
	private List<Point> path;

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public Point getOpenState() {
		return openState;
	}

	public void setOpenState(Point openState) {
		this.openState = openState;
	}

	public Point getFinalState() {
		return finalState;
	}

	public void setFinalState(Point finalState) {
		this.finalState = finalState;
	}

	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}

	public List<Point> getPath() {
		return path;
	}

	public void setPath(List<Point> path) {
		this.path = path;
	}

	public StepSnapshot(int timestamp, Point openState, Point finalState, int steps, ArrayList<Point> path) {
		this.timestamp = timestamp;
		this.openState = openState;
		this.finalState = finalState;
		this.steps = steps;
		this.setPath(path);
	}

	public StepSnapshot(Point openState, Point finalState, int steps, ArrayList<Point> path) {
		this(0, openState, finalState, steps, path);
	}

	public StepSnapshot(int timestamp, Point openState, Point finalState, int steps) {
		this(timestamp, openState, finalState, steps, null);
	}

	public StepSnapshot(Point openState, Point finalState, int steps) {
		this(0, openState, finalState, steps, null);
	}

	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		build.append(String.format("Timestep:%d", this.timestamp));
		build.append(String.format("\tOpen State:(%d,%d)", (int) this.openState.getX(), (int) this.openState.getY()));
		build.append(
				String.format("\tFinal State:(%d,%d)", (int) this.finalState.getX(), (int) this.finalState.getY()));
		build.append(String.format("\tSteps: %d", this.steps));
		build.append("\tPath:");
		if (this.path.size() > 0)
			for (Point p : this.path) {
				build.append(String.format("->(%d,%d)", (int) p.getX(), (int) p.getY()));
			}
		build.append("->");
		return build.toString();
	}

	public String toCSV() {
		StringBuilder build = new StringBuilder();
		build.append(this.timestamp);
		build.append(", ");
		build.append(String.format("(%d-%d)", (int) this.openState.getX(), (int) this.openState.getY()));
		build.append(", ");
		build.append(String.format("(%d-%d)", (int) this.finalState.getX(), (int) this.finalState.getY()));
		build.append(", ");
		build.append(this.steps);
		build.append(", ");
		if (this.path.size() > 0)
			for (Point p : this.path) {
				build.append(String.format("->(%d-%d)", (int) p.getX(), (int) p.getY()));
			}
		build.append("->");
		build.append("\n");

		return build.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StepSnapshot))
			return false;
		if (obj == this)
			return true;
		StepSnapshot q = (StepSnapshot) obj;
		if (this.openState.equals(q.openState) && this.finalState.equals(q.finalState) && this.steps == q.steps)
			return true;
		else
			return false;
	}
}
