//the vector in a list (Q) + a vector R

package nxcs;

import java.util.ArrayList;
import java.util.List;

public class addVector {
	private List<Double> vecR;
	private List<Double> q;
	
	public List<Double> addVector(List<Double> q, List<Double> vecR) {
		List<Double> V = null;
		for (int i=0;i<q.size();i++){
			V.add(i, q.get(i)+vecR.get(i));
		}	
		return V;	
	}
	
	public List<Double> minusVector(List<Double> q, List<Double> vecR) {
		List<Double> V = null;
		for (int i=0;i<q.size();i++){
			V.add(i, q.get(i) - vecR.get(i));
		}	
		return V;	
	}
	
}
