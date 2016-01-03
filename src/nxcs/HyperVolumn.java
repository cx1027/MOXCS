package nxcs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nxcs.Qvector;

public class HyperVolumn {
	
	public double calcHyperVolumn(ArrayList<Qvector> vList, Qvector refer) {
		double hper = 0;
		vList = sortQvector(vList);// sort by the first value by ascending
		for (int i = 0; i < vList.size(); i++) {
			if (i == vList.size() - 1) {
				hper += Math.abs(vList.get(i).getQvalue().get(0) - refer.getQvalue().get(0))
						* Math.abs(vList.get(i).getQvalue().get(1) - refer.getQvalue().get(1));
			} else {
				hper += Math.abs(vList.get(i).getQvalue().get(0) - vList.get(i + 1).getQvalue().get(0))
						* Math.abs(vList.get(i).getQvalue().get(1) - refer.getQvalue().get(1));
			}
		}
		return hper;
	}

	public ArrayList<Qvector> sortQvector(ArrayList<Qvector> vList) {
		// Collections.sort(vList, (a, b) -> (int) ((a.getQvalue().get(0) - b.getQvalue().get(0)) * 10024));

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