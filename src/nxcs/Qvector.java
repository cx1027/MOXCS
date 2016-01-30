//initial a QVector with 2 double values in 2 dimention problem

package nxcs;

import java.util.ArrayList;
import java.util.List;

public class Qvector {
	private List<Double> qValue;

	public Qvector(List<Double> qValue) {
		this.qValue = new ArrayList<Double>(qValue);// 解决arraylist值引用的问题
	}

	public Qvector(Qvector q) {
		this.qValue = new ArrayList<Double>(q.getQvalue());// 解决arraylist值引用的问题
	}

	public Qvector() {
	}

	public Qvector(double a, double b) {
		qValue = new ArrayList<Double>();
		qValue.add(a);
		qValue.add(b);
	}

	public Qvector(double a, double b, double c) {
		qValue = new ArrayList<Double>();
		qValue.add(a);
		qValue.add(b);
		qValue.add(c);
	}

	public List<Double> getQvalue() {
		return qValue;
	}

	public void setQvalue(List<Double> q) {
		this.qValue = qValue;
	}

	public int size() {
		// TODO Auto-generated method stub
		return qValue.size();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Qvector))
			return false;
		if (obj == this)
			return true;
		Qvector q = (Qvector) obj;
		if (this.getQvalue().size() != q.getQvalue().size())
			return false;
		for (int i = 0; i < this.getQvalue().size(); i++) {
			if (!this.getQvalue().get(i).equals(q.getQvalue().get(i)))
				return false;
		}
		return true;
	}

	@Override
	public Qvector clone() {
		Qvector clone = new Qvector(this.qValue);
		return clone;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" Pareto:[");
		for (Double p : this.qValue) {
			sb.append(p);
			sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}

	public void set(int i, Double d) {
		if (qValue == null) {
			qValue = new ArrayList<Double>();
		}
		if (qValue.size() < i + 1) {
			qValue.add(d);
		} else {
			qValue.set(i, d);
		}

	}

	public Double get(int i) {
		return qValue.get(i);

	}
}
