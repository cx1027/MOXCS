package nxcs;

import java.util.ArrayList;
import java.util.List;

import com.rits.cloning.Cloner;

public class ParetoQvector {
    private Cloner cloner; 
    
	private List<ArrayList<Double>> candidatelist;
	
	
	public ParetoQvector() {
		this.cloner = new Cloner();
	}

	public ArrayList<Qvector> getPareto(ArrayList<Qvector> currParentoCandidate) {
		ArrayList<Qvector> archivinglist = new ArrayList<Qvector>();
		try {
			// TODO: how to add element
			Qvector c0 = currParentoCandidate.get(0);//get first item from candidatelist
			archivinglist.add(c0);//give first item to archivinglist

			// archivinglist.set(0, c0);
			int result;
			int ci = 0;
			
			
			for (int i=1; i< currParentoCandidate.size();i++) {
//				if (ci == 0) {
//					ci = 1;// c0 is already in archivinglist
//					continue;
//				}
				boolean flag = true;
//				ActionPareto candidate = currParentoCandidate.get(i);//
				Qvector candidate = cloner.deepClone(currParentoCandidate.get(i));

				for (int j =0; j<archivinglist.size();j++) {
					Qvector archiving = archivinglist.get(j);
					result = Dominate(candidate, archiving);
					if (result == 2) {// candidate is non donminate
						archivinglist.remove(archiving);
					} else if (result == 1) {// both non dominate
						continue;
					} else if (result == 3) {// archiving is non dominate
						flag = false;
						break;
					}
				}
				if (flag == true) {
					archivinglist.add(candidate);
				}
			}

		} catch (Exception ex) {
			System.console().printf("pareto error!");
		}
		return archivinglist;
	}

	public int Dominate(Qvector candidate, Qvector archiving) {
		int result = 0;
		if ((candidate.get(0) > archiving.get(0) && candidate.get(1) < archiving.get(1))
				|| (candidate.get(0) < archiving.get(0) && candidate.get(1) > archiving.get(1))) {
			result = 1;
		} else if (candidate.get(0) >= archiving.get(0) && candidate.get(1) >= archiving.get(1)) {
			result = 2;
		} else if (candidate.get(0) <= archiving.get(0)
				&& candidate.get(1) <= archiving.get(1)) {
			result = 3;
		}
		return result;
	}

}
