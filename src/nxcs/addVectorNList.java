

//the vector in a list (Q) + a vector R

package nxcs;

import java.util.ArrayList;
import java.util.List;

public class addVectorNList {
	private ArrayList<Qvector> vecList;
	private Qvector vecR;
	private ArrayList<Qvector> vecResult;

	public ArrayList<Qvector> addVectorNList(ArrayList<Qvector> vecList, Qvector vecR) {
		ArrayList<Qvector> vecResult = new ArrayList<Qvector>();
		for (Qvector qvec : vecList) {
			Qvector q = new Qvector();
			q = addVector(qvec, vecR);
			vecResult.add(q);
		}
		return vecResult;
	}
    
	//+
	public Qvector addVector(Qvector q, Qvector vecR) {
		Qvector V = new Qvector();
//		System.out.println("zzzzzQ:" + q );
		for (int i = 0; i < q.size(); i++) {
			V.set(i, q.getQvalue().get(i) + vecR.get(i));
		}
		return V;
	}
	
	//-
	public Qvector minusVector(Qvector q, Qvector vecR) {
		Qvector V = new Qvector();
//		System.out.println("zzzzzQ:" + q );
		for (int i = 0; i < q.size(); i++) {
			V.set(i, q.getQvalue().get(i) - vecR.get(i));
		}
		return V;
	}
	
	//"/"
	public Qvector divideVector(Qvector q, int num) {
		Qvector V = new Qvector();
//			System.out.println("zzzzzQ:" + q );
		for (int i = 0; i < q.size(); i++) {
			V.set(i, q.getQvalue().get(i)/num);
		}
		return V;
	}

}
