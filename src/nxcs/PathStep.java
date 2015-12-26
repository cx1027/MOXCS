package nxcs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class PathStep {
	private int step;
	private List<Point> Path; 
	
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
		this.step = step;
	}
	public List<Point> getPath() {
		return Path;
	}
	public void setPath(List<Point> path) {
		Path = path;
	}
	
	
	public PathStep(){
		this.step=0;
		this.Path = new ArrayList<Point>();
	}
	
	public void add(Point point)
	{
		this.Path.add(point);
	}
}
