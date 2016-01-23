package nxcs;

import java.util.ArrayList;
import java.util.List;

import com.rits.cloning.Cloner;

public class ParetoCal {
	private Cloner cloner;

	private List<ArrayList<Double>> candidatelist;

	// public ParetoCal(List<ArrayList<Double>> candidatelist) {
	// this.candidatelist = candidatelist;
	// }

	public ParetoCal() {
		this.cloner = new Cloner();
	}

	public List<ActionPareto> getPareto(ArrayList<ActionPareto> currParentoCandidate) {
		List<ActionPareto> archivinglist = new ArrayList<ActionPareto>();
		try {
			// TODO: how to add element
			ActionPareto c0 = currParentoCandidate.get(0);// get first item from
															// candidatelist
			archivinglist.add(c0);// give first item to archivinglist

			// archivinglist.set(0, c0);
			int result;
			int ci = 0;

			for (int i = 1; i < currParentoCandidate.size(); i++) {
				// if (ci == 0) {
				// ci = 1;// c0 is already in archivinglist
				// continue;
				// }
				boolean flag = true;
				// ActionPareto candidate = currParentoCandidate.get(i);//
				ActionPareto candidate = cloner.deepClone(currParentoCandidate.get(i));
				List<ActionPareto> removeList = new ArrayList<ActionPareto>();
				for (int j = 0; j < archivinglist.size(); j++) {
					ActionPareto archiving = archivinglist.get(j);
					result = Dominate(candidate, archiving);
					if (result == 2) {// candidate is non donminate
						// archivinglist.remove(archiving);
						removeList.add(archiving);
					} else if (result == 1) {// both non dominate
						continue;
					} else if (result == 3) {// archiving is non dominate
						flag = false;
						break;
					}
				}
				if (removeList.size() > 0) {
					for (ActionPareto p : removeList) {
						archivinglist.remove(p);
					}
				}
				if (flag == true) {
					archivinglist.add(candidate);
				}
			}

		} catch (Exception ex) {
			System.console().printf("pareto error!" + ex.getMessage());
		}
		return archivinglist;
	}

	public int Dominate(ActionPareto candidate, ActionPareto archiving) {
		int result = 0;
		if ((candidate.getPareto().get(0) > archiving.getPareto().get(0)
				&& candidate.getPareto().get(1) < archiving.getPareto().get(1))
				|| (candidate.getPareto().get(0) < archiving.getPareto().get(0)
						&& candidate.getPareto().get(1) > archiving.getPareto().get(1))) {
			result = 1;
		} else if (candidate.getPareto().get(0) >= archiving.getPareto().get(0)
				&& candidate.getPareto().get(1) >= archiving.getPareto().get(1)) {
			result = 2;
		} else if (candidate.getPareto().get(0) <= archiving.getPareto().get(0)
				&& candidate.getPareto().get(1) <= archiving.getPareto().get(1)) {
			result = 3;
		}
		return result;
	}

}
