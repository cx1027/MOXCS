package nxcs.distance;

import java.util.ArrayList;

import nxcs.Qvector;

public class MinDistanceCalculator extends DistanceCalculator {

	@Override
	public double getDistance(ArrayList<Qvector> qSet1, ArrayList<Qvector> qSet2) {
		double dis = 0;
		double min = 0;
		int flag = 0;

		for (Qvector q1 : qSet1) {
			for (Qvector q2 : qSet2) {
				if (flag == 0) {
					min = getDisofPonit(q1, q2);
					flag = 1;
				} else {
					dis = getDisofPonit(q1, q2);
					if (dis < min) {
						min = dis;
					}
				}
			}
		}

		return min;
	}

}
