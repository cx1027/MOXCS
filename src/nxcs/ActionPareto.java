package nxcs;

import java.util.ArrayList;
import java.util.List;

public class ActionPareto {
	private Qvector paretoValue;

	private Qvector R;

	private int action;

	public ActionPareto(Qvector pareto, int action) {
		this.paretoValue = new Qvector(pareto);
		this.action = action;
	}

	public ActionPareto(Qvector pareto, int action, Qvector r) {
		this.paretoValue = new Qvector(pareto);
		this.R = new Qvector(r);
		this.action = action;
	}

	public Qvector getPareto() {
		return paretoValue;
	}

	public void setPareto(Qvector pareto) {
		this.paretoValue = pareto;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public int size() {
		// TODO Auto-generated method stub
		return paretoValue.size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Action:").append(this.action);
		sb.append(" Pareto:[");
		for (Double p : this.paretoValue.getQvalue()) {
			sb.append(p);
			sb.append(", ");
		}
		sb.append("]");

		sb.append(" R:[");
		try {
			for (Double r : this.R.getQvalue()) {
				sb.append(r);
				sb.append(", ");
			}
		} catch (Exception e) {
			System.out.print("Print R Exception:" + e);
		}
		sb.append("]");

		return sb.toString();
	}

	public Qvector getR() {
		return R;
	}

	public void setR(Qvector r) {
		R = r;
	}

}
