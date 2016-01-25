package nxcs.testbed.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nxcs.testbed.maze_run;
import nxcs.testbed.maze.settings.*;

public class testSuit1 {
	public static void main(String[] args) throws IOException {
		List<IGetMazeTestSettings> tests = new ArrayList<IGetMazeTestSettings>();
		tests.add(new maze4Settings(3, 1));
		// tests.add(new maze5Settings(3001, 30));
		// tests.add(new maze6Settings(3001, 30));

		tests.stream().forEach(x -> {
			maze_run maze;
			try {
				maze = new maze_run();
				maze.run(x);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}
}
