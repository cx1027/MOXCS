package nxcs.testbed.maze.settings;

import java.util.ArrayList;

import nxcs.stats.StepSnapshot;

public interface IGetMazeTestSettings {
	public ArrayList<ArrayList<StepSnapshot>> getOpenLocationExpectPaths() ;
	public String getTestFile();
	public String getMazeName();
	public int getFinalStates();
	public int getTrailTimes();
}
