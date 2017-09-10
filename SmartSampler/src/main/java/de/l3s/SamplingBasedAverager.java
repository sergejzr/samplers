package de.l3s;

import java.util.Arrays;

/**
 * This is a class for efficient estimation of a statistics in a very large data
 * set.<br>
 * It delivers a value with a bounded error that does not exceed given value
 * within a given confidence interval.<br>
 * It is using a central limit theorem and Tschebyshev inequality.<br>
 * The algorithm was first designed in 2012 to compute a diversity value of
 * large data sets, but can be used for any estimation:<br>
 * Fan Deng, Stefan Siersdorfer, and Sergej Zerr. 2012. <br>
 * Efficient jaccard-based diversity analysis of large document collections.
 * <br>
 * In Proceedings of the 21st ACM international conference on Information and
 * knowledge management (CIKM '12).<br>
 * DOI=http://dx.doi.org/10.1145/2396761.2398445<br>
 * 
 * It is assumed that sampling operation is very expensive to achieve the
 * highest profit in number of saved operations.
 * 
 */

public class SamplingBasedAverager {
	double error, confidence;
	int W;

	class BasicStats {

		double median;
		double mean;

		public double getMedian() {
			return median;
		}

		public double getMean() {
			return mean;
		}

		public double getStddev() {
			return stddev;
		}

		double stddev;

		public BasicStats(double median, double average, double diff) {
			super();
			this.median = median;
			this.mean = average;
			this.stddev = diff;
		}

	}

	public SamplingBasedAverager() {
		this(0.05, 0.95, 10);
	}

	Double abs_error = null;
	Double estimated_error = null;

	/**
	 * Creates a new Object with given maximal error at minimum confidence. <br>
	 * Both numbers need to be in the range 0-1.0<br>
	 * The default step of a sample size is 10<br>
	 * 
	 * @param error:
	 *            maximal sampling error (0-1.0)
	 * @param confidence:
	 *            minimal confidence (0-1.0)
	 */
	public SamplingBasedAverager(double error, double confidence) {
		this(error, confidence, 10);
	}

	/**
	 * Creates a new Object with given maximal error at minimum confidence. <br>
	 * Both numbers need to be in the range 0-1.0<br>
	 * The step of a sample size is W<br>
	 * 
	 * @param error:
	 *            maximal sampling error (0-1.0)
	 * @param confidence:
	 *            minimal confidence (0-1.0)
	 * @param w:
	 *            initial sample size and sample step.
	 */
	public SamplingBasedAverager(double error, double confidence, int W) {
		assert (error > 0 && error <= 1.0);
		assert (confidence > 0 && confidence < 1.0);
		this.error = error;
		this.confidence = confidence;
		this.W = W;
	}

	/**
	 * Performs the estimation through random sampling
	 * 
	 * @param s:
	 *            sampler object, which task is to provide a value of a random
	 *            object from the population
	 * @return
	 */
	public double randomSampling(Sampler s) {
		double rdj = 0.0D;
		int r1 = 0;

		// define the number of sample buckets
		int r2 = (int) Math.ceil(Math.log(1 / (1 - confidence)) / Math.log(2)); // (int)
																				// Math.ceil(Math.log(1.0D
																				// /
																				// confidence)
																				// /
																				// Math.log(2D));

		// for each sample backer keep the sum of sampled values
		double jsSum[] = new double[r2];


		boolean interrupted = false;
		do {
			// for each sample bucket sample W vaues
			sampleloop: for (int i = 0; i < r2; i++) {
				for (int j = 1; j <= W; j++) {
					try {
						jsSum[i] += s.sampleOne();
					} catch (InterruptedException ex) {
						System.err.println("Sampling interrupted");
						interrupted = true;
						break sampleloop;
					}

				}
			}
			// increase sample size by W
			r1 += W;

			BasicStats stats = getStats(jsSum, r1);

			rdj = stats.getMedian() / r1;

			estimated_error = (stats.getStddev()) / (stats.getMedian());

		} while (!interrupted && estimated_error > error);
		return rdj;
	}

	private BasicStats getStats(double[] a, int r1) {

		double median = a[0], average;

		Arrays.sort(a);
		double sum = 0.0D;
		double ad[] = a;
		int j = ad.length;
		for (int i = 0; i < j; i++) {
			double cur = ad[i];
			sum += cur;
		}
		average = sum / a.length;

		double mean = sum / (double) a.length;
		double before = a[0];
		double ad1[];
		int l = (ad1 = a).length;
		boolean found = false;
		for (int k = 0; k < l; k++) {
			double cur = ad1[k];
			if (cur > mean) {
				found = true;
				median = Math.abs(cur - mean) >= Math.abs(cur - before) ? before : cur;
				break;
			}
			before = cur;
		}

		if (!found) {
			median = a[a.length - 1];
		}

		double diff = 0;
		for (int i = 0; i < j; i++) {
			double cur = ad[i];
			diff += Math.pow(cur - average, 2);
		}
		diff = Math.sqrt(diff / a.length);

		return new BasicStats(median, average, diff);
	}

	/**
	 * Returns the sampling error estimated by the algorithm
	 * 
	 * @return
	 */
	public Double getEstimatedError() {
		return estimated_error;
	}

	public static int log2(int n) {
		if (n <= 0)
			throw new IllegalArgumentException();
		return 31 - Integer.numberOfLeadingZeros(n);
	}

}
