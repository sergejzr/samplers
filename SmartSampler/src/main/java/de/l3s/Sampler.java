package de.l3s;
/**
 * This interface provides a single sample value and is called many times by SamplingBasedAverager
 * @author zerr
 *
 */
public interface Sampler {

	/**
	 * This method should return a value randomly sampled from the given population.<br>
	 * It could be any single value. It is up to user implementation, in case this value has to come from a pair or a set of several objects of the population (like pairwise average, distance etc.) 
	 *
	 * @return one sampled value from the population
	 * @throws InterruptedException
	 */
	public double sampleOne() throws InterruptedException;

}
