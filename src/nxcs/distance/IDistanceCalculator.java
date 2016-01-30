package nxcs.distance;

import java.util.ArrayList;

import nxcs.Qvector;

public interface IDistanceCalculator {
	public double getDistance(ArrayList<Qvector> qSet1, ArrayList<Qvector> qSet2);
}
