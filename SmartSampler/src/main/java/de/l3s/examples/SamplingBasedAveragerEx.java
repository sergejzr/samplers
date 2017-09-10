package de.l3s.examples;

/**
 * This is an example usage of the class SamplingBasedAverager, that can efficiently compute an average statistics value from 
 * very large corpora.<br/>
 * 
 * In this example we compute an average value of all number within a range, which would require to sum up all
 *  integers in a particular range and divide this sum by the number of integers in this range.<br/>
 *  
 *  This is similar to an averaging the number of eyes from throwing a dice (just in our case the dice has N sides and one of these sides is empty)<br/>
 *  
 *  To compute the value, our sampler will require just about several hundreds of operations (additions) even for huge ranges and the error is bounded.<br>
 *  Of course, addition is a very simple operation, but imagine, instead it would be an API call, or some computation of exponential complexity
 *  
 */
import java.util.Random;

import de.l3s.Sampler;
import de.l3s.SamplingBasedAverager;

public class SamplingBasedAveragerEx {
	// Count the sampling rounds, just for fun/statistics. Don't need is in the
	// real task.
	static int samplerounds = 0;

	public static void main(String[] args) {

		// SamplingBasedAverager(_error_, _confidence_) - Estimate a value with
		// at most _error_ % error guaranteed with at least _confidence_ %.
		double _error_ = 0.05, _confidence_ = 0.95;
		SamplingBasedAverager ABA = new SamplingBasedAverager(_error_, _confidence_);

		// Our playground range 0-N
		final int N = 1000000000;

		double average = ABA.randomSampling(new Sampler() {
			Random r = new Random();

			public double sampleOne() throws InterruptedException {
					samplerounds++;

					// We are computing an average between 2 numbers here in the range (0-N]. Please
					// note, the averager does not know our range
					return (r.nextInt(N+1) + r.nextInt(N+1)) / 2;
				
			}
		});

		// Best printed as excel table :)
		System.out.println("The average of the numbers of the range 0-" + N + ", estimation and statistics:");
		System.out.println("Computed estimation should have at most " + _error_ * 100 + "% error, with "
				+ String.format("%.2f", _confidence_ * 100) + "% confidence");
		System.out.println();
		System.out.println("              \t" + String.format("%10s", "Exact") + "\tEstimated");
		System.out.println("Average       :\t" + String.format("%10s", (N / 2)) + "\t" + (int) (average));
		System.out.println("Error         :\t"
				+ String.format("%10s", String.format("%.2f", ((Math.abs(average - (N / 2)) / (N / 2))) * 100)) + "\t"
				+ String.format("%10s", String.format("%.2f", (ABA.getEstimatedError()) * 100)));
		System.out.println("Operations    :\t" + String.format("%10s", N) + "\t" + samplerounds);
		System.out.println("x times faster:\t" + String.format("%10s", 1) + "\t" + N / samplerounds);

	}
}
