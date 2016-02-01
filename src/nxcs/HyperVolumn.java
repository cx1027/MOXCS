package nxcs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nxcs.Qvector;

public class HyperVolumn {

	public double calcHyperVolumn(ArrayList<Qvector> vList, Qvector refer) {
		double hper = 0;
		if (vList.size() == 1) {
			return Math.abs((vList.get(0).getQvalue().get(0) - refer.getQvalue().get(0))
					* (vList.get(0).getQvalue().get(1) - refer.get(1)));
		}
		vList.add(refer);
		// sort by the first value by ascending
		vList = sortQvector(vList);
		for (int i = 1; i < vList.size(); i++) {
			hper += Math.abs(vList.get(i).getQvalue().get(0) - vList.get(i - 1).getQvalue().get(0))
					* Math.abs(vList.get(i).getQvalue().get(1) - refer.getQvalue().get(1));
		}
		return hper;
	}

	public double calcHyperVolumn(Qvector q, Qvector refer) {
		double hper = 0;
		hper += Math.abs(q.getQvalue().get(0) - refer.getQvalue().get(0))
				* Math.abs(q.getQvalue().get(1) - refer.getQvalue().get(1));
		return hper;
	}

	public Qvector normailze(Qvector q) {
		// double sumSqrt = Math
		// .sqrt(q.getQvalue().get(0) * q.getQvalue().get(0) +
		// q.getQvalue().get(1) * q.getQvalue().get(1));
		//
		// return new Qvector(q.getQvalue().get(0) / sumSqrt,
		// q.getQvalue().get(1) / sumSqrt);
		return new Qvector((20 + q.get(0)) / 20, q.get(1) / 20);
	}

	public ArrayList<Qvector> normailze(ArrayList<Qvector> list) {
		ArrayList<Qvector> ret = new ArrayList<Qvector>();
		for (Qvector q : list) {
			ret.add(this.normailze(q));
		}
		return ret;
	}

	public ArrayList<Qvector> sortQvector(ArrayList<Qvector> vList) {
		// Collections.sort(vList, (a, b) -> (int) ((a.getQvalue().get(0) -
		// b.getQvalue().get(0)) * 10024));

		Collections.sort(vList, new Comparator<Qvector>() {
			@Override
			public int compare(Qvector o1, Qvector o2) {
				return o1.get(0).compareTo(o2.get(0));
			}
		});
		return vList;
	}

	public ArrayList<Qvector> sortQvector(ArrayList<Qvector> vList, Comparator<Qvector> comparator) {
		Collections.sort(vList, comparator);
		return vList;
	}
}