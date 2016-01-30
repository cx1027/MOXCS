package nxcs;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * A cherry picked sample of useful methods from the Xience library (Not released).
 * This class contains a bunch of useful facilitating mathematical methods.
 * @author Colin Douch
 *
 */
public class XienceMath {
	/**
	 * The Random instance used for generating *predictable* random numbers
	 */
	private static Random random;
	
	/**
	 * Initializes the random instance with some JVM defined seed
	 */
	static{
		random = new Random();
	}
	
	/**
	 * Sets the seed of the random number generator, affecting all 
	 * subsequent calls to the random methods in the class
	 * @param seed The new seed to use
	 */
	public static void setSeed(long seed){
		random = new Random(seed);
	}
	
	/**
	 * Generates a random integer in the range [0, max)
	 * @param max The upper bound (exclusive) of the number to generate
	 * @return The generated int
	 */
	public static int randomInt(int max){
		return random.nextInt(max);
	}
	
	/**
	 * Generates a random integer in the range [min, max)
	 * @param min The lower bound (inclusive) of the number to generate
	 * @param max The upper bound (exclusive) of the number to generate
	 * @return The generated int
	 */
	public static int randomInt(int min, int max){
		if(min > max)throw new IllegalArgumentException(String.format("Lower bound (%d) is > upper bound (%d", min, max));
		return random.nextInt(max - min) + min;
	}
	
	/**
	 * Generates a random double in the range [0, 1)
	 * @return The generated number
	 */
	public static double random(){
		return random.nextDouble();
	}
	
	/**
	 * Generates a random double in the range [0, max)
	 * @param max The upper bound (exclusive) of the number to generate
	 * @return The generated number
	 */
	public static double random(double max){
		return random.nextDouble() * max;
	}
	
	/**
	 * Generates a random number in the range [min, max)
	 * @param min The lower bound (inclusive) of the number to generate
	 * @param max The upper bound (exclusive) of the number to generate
	 * @return The generated number
	 */
	public static double random(double min, double max){
		if(min > max)throw new IllegalArgumentException(String.format("Lower bound (%d) is > upper bound (%d", min, max));
		return random.nextDouble() * (max - min) + min;
	}
	
	/**
	 * Selects a random value from the given list
	 * @param data The list to choose from
	 * @return
	 */
	public static <T> T choice(List<T> data){
		if(data == null || data.isEmpty())throw new IllegalArgumentException("Cannot choose from null or empty list");
		return data.get(randomInt(data.size()));
	}
	
	/**
	 * Selects a random value from the given array, each value with a probability in p.
	 * Assumes p is an array of <b>probabilities<b> that is, p[i] > 0 and sum_i p[i] = 1
	 * @param data The elements to choose from
	 * @param p The probabilities, one for each element
	 * @return A randomly chosen element from `data`
	 */
	public static <T> T choice(T[] data, double[] p){
		//Check some preconditions
		if(data == null || data.length == 0)throw new IllegalArgumentException("Cannot choose from null or empty list");
		if(p.length != data.length)throw new IllegalArgumentException(String.format("Different number of values (%d) from probabilities (%d)", data.length, p.length));
//		if(Math.abs(Arrays.stream(p).sum() - 1) >= 0.000001)throw new IllegalArgumentException("Probabilities are not normalized");
		
		double choicePoint = random();
		for(int i = 0;i < data.length;i ++){
			choicePoint -= p[i];
			if(choicePoint <= 0)return data[i];
		}
		return data[randomInt(data.length)];
	}
	
	/**
	 * Selects a random value from the given list, each value with a probability in p.
	 * Assumes p is an array of <b>probabilities<b> that is, p[i] > 0 and sum_i p[i] = 1
	 * @param data The elements to choose from
	 * @param p The probabilities, one for each element
	 * @return A randomly chosen element from `data`
	 */
	public static <T> T choice(List<T> data, double[] p){
		if(data == null || data.size() == 0)throw new IllegalArgumentException("Cannot choose from null or empty list");
		if(p == null)throw new IllegalArgumentException("Cannot choose with null probabilities");
		if(p.length != data.size())throw new IllegalArgumentException(String.format("Different number of values (%d) from probabilities (%d)", data.size(), p.length));
		if(Math.abs(Arrays.stream(p).sum() - 1) >= 0.000001)throw new IllegalArgumentException("Probabilities are not normalized");
		
		double choicePoint = random();
		for(int i = 0;i < data.size();i ++){
			choicePoint -= p[i];
			if(choicePoint <= 0)return data.get(i);
		}
		return data.get(randomInt(data.size()));
	}
	
	/**
	 * Clamps the given value to be strictly in the range [min, max], returning
	 * the clamped value
	 * @param val The value to clamp
	 * @param min The minimum value of the value
	 * @param max The maximum value of the value
	 * @return The clamped value
	 */
	public static double clamp(double val, double min, double max){
		if(min > max)throw new IllegalArgumentException(String.format("Lower bound (%d) is > upper bound (%d", min, max));
		if(val < min)return min;
		if(val > max)return max;
		return val;
	}
	
	/**
	 * Returns the numerical average of the given array of doubles
	 * @param data
	 * @return
	 */
	public static double average(double[] data){
		if(data == null || data.length == 0)throw new IllegalArgumentException("Cannot average null or empty list");
		return Arrays.stream(data).average().getAsDouble();
	}
	
	/**
	 * Computes the weighted average of the given double array
	 * @param data The data to average
	 * @param weights The weights for each element in data
	 * @return The weighted average
	 */
	public static double average(double[] data, double[] weights){
		if(data == null || data.length == 0)throw new IllegalArgumentException("Cannot average null or empty list");
		if(weights == null || weights.length == 0)throw new IllegalArgumentException("Cannot average with null or empty weights");
		if(weights.length != data.length)throw new IllegalArgumentException(String.format("Different number of values (%d) from weights (%d)", data.length, weights.length));
		return IntStream.range(0, data.length).mapToDouble(i -> data[i] * weights[i]).sum() / Arrays.stream(weights).sum();
	}
}
