package nxcs.testbed;

import nxcs.Environment;
import nxcs.NXCS;
import nxcs.NXCSParameters;
import nxcs.XienceMath;

/**
 * Represents an N-bit multiplexer problem for NXCS. The number of bits
 * is controlled by the `numBitsInAddress` field
 *
 */
public class Multiplexer implements Environment{
	
	/**
	 * The number of bits in the address of this multiplexer. The
	 * final state length is thus numBitsInAddress + 2^numBitsInAddress
	 */
	private static final int numBitsInAddress = 2;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getState() {
		StringBuilder state = new StringBuilder();
		int stateLength = numBitsInAddress + (1 << numBitsInAddress);
		for(int i = 0;i < stateLength;i ++){
			if(XienceMath.random() < 0.5){
				state.append('0');
			}
			else{
				state.append('1');
			}
		}
		return state.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getReward(String state, int action) {
		int address = Integer.parseInt(state.substring(0, numBitsInAddress), 2);
		if((action + '0') == state.charAt(numBitsInAddress + address)){
			return 1000;
		}
		return -1000;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEndOfProblem(String state) {
		return false;
	}
	
	public static void main(String[] args){
		Multiplexer multiplexer = new Multiplexer();
		NXCSParameters params = new NXCSParameters();
		params.rho0 = 1000;
		params.gamma = 0;
		params.numActions = 2;
		params.stateLength = numBitsInAddress + (1 << numBitsInAddress);
		params.pHash = 0.2;
		NXCS nxcs = new NXCS(multiplexer, params);
		
		for(int i = 0;i < 10000;i ++){
			nxcs.runIteration();
		}
		
		int correct = 0;
		for(int i = 0;i < 100;i ++){
			String state = multiplexer.getState();
			int guess = nxcs.classify(state);
			double reward = multiplexer.getReward(state, guess);
			System.out.println(state + "=" + guess + " = " + reward);
			if(reward == params.rho0){
				correct ++;
			}
		}
		
		nxcs.printPopulation();
		
		System.out.println(correct);
	}
}
