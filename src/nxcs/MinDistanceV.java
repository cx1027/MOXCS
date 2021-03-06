package nxcs;
import static java.util.stream.Collectors.*;

import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.List;

public class MinDistanceV {
	private ArrayList<Qvector> newV;
	private int exp;
	private double avgDis;
	private double sumDistance;

	public int increaseExp() {
		return this.exp++;
	}

	public int getExp() {
		return exp;
	}

	public double getAvgDis() {
		return avgDis;
	}

	public void setAvgDis(double distance) {
		sumDistance += distance;
		this.avgDis = sumDistance / (exp + 1);
	}

	public ArrayList<Qvector> getNewV() {
		return newV;
	}
//
//	public void setNewV(ArrayList<Qvector> v) {
//		newV = v;
//	}

	public MinDistanceV(ArrayList<Qvector> v) {
		this.newV = v.stream().map(d -> d.clone()).collect(toCollection(ArrayList::new));
		this.exp = 0;
		this.avgDis = 0;
		this.sumDistance = 0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append("Exp:").append(this.exp);
		sb.append(" AvgDis:").append(this.avgDis);
		for (Qvector p : this.newV) {
			sb.append(" V:[");
			for (Double d : p.getQvalue()) {
				sb.append(d.toString());
				sb.append(", ");
			}
			sb.append("], ");
		}
		sb.append("]");
		return sb.toString().replace("], ", "]");
	}
}
