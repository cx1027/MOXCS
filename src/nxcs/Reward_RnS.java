package nxcs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Reward_RnS {

	private ArrayList<Double> rewardVec;
	private Point _state;

	public Point getState() {
		return _state;
	}

	public ArrayList<Double> getRewardVec() {
		return rewardVec;
	}

	public void setRewardVec(ArrayList<Double> rewardVec) {
		this.rewardVec = rewardVec;
	}

	public void SetGridReward(double value) {
		this.rewardVec.set(1, value);
	}

	public double getGridReward() {
		return this.rewardVec.get(1);
	}

	public double getReward1() {
		return this.rewardVec.get(0);
	}

	public void SetReward1(double value) {
		this.rewardVec.set(0, value);
	}

	public Reward_RnS(Point state, double reward1, double gridReward) {
		this._state = state;
		this.rewardVec = new ArrayList<Double>();
		this.rewardVec.add(reward1);
		this.rewardVec.add(gridReward);

	}

	public Reward_RnS(Point state, ArrayList<Double> reward) {
		this._state = state;
		this.rewardVec = reward;

	}
	
	public Reward_RnS(Point state) {
		this._state = state;
		//this.rewardVec = reward;

	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Point:").append(this._state);

		sb.append(" Reward:[");
			for (Double p : this.rewardVec) {
				sb.append(p.toString());
				sb.append(", ");
			}
			sb.append("]");
			return sb.toString();
	}
}
