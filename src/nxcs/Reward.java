package nxcs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Reward {

	private Qvector rewardVec;
	private Point _state;

	public Point getState() {
		return _state;
	}

	public Qvector getRewardVec() {
		return rewardVec;
	}

	public void setRewardVec(Qvector rewardVec) {
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

	public Reward(Point state, double reward1, double gridReward) {
		this._state = state;
		this.rewardVec = new Qvector(reward1,gridReward);

	}

	public Reward(Point state, Qvector reward) {
		this._state = state;
		this.rewardVec = reward;

	}
	
	public Reward(Point state) {
		this._state = state;
		//this.rewardVec = reward;

	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Point:").append(this._state);

		sb.append(" Reward:[");
			for (Double p : this.rewardVec.getQvalue()) {
				sb.append(p.toString());
				sb.append(", ");
			}
			sb.append("]");
			return sb.toString();
	}
}
