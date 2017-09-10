package de.l3s;
import java.util.Arrays;
/**
 * This class is obsolete and for archiving only! Use SamplingBasedAverager instead.
 * This is a class for efficient estimation of a statistics in a very large data set.<br> It delivers a value with a bounded error that does not exceed given value within a given confidence interval.<br> It is using a central limit theorem and Tschebyshev inequality.<br>
 * The algorithm was first designed in 2012 to compute a diversity value of large data sets, but can be used for any estimation:<br>
 * Fan Deng, Stefan Siersdorfer, and Sergej Zerr. 2012. <br>
 * Efficient jaccard-based diversity analysis of large document collections. <br>
 * In Proceedings of the 21st ACM international conference on Information and knowledge management (CIKM '12).<br>
 * DOI=http://dx.doi.org/10.1145/2396761.2398445<br>
 * 
 * It is assumed that sampling operation is very expensive to achieve the highest profit in number of saved operations.
 * 
 */

@Deprecated
public class SamplingBasedAveragerRDJ {
	double error, confidence;
	int W;

	public SamplingBasedAveragerRDJ() {
		this(0.05, 0.95, 10);
	}

	Double abs_error = null;
	Double estimated_error=null;
/**
 * Creates a new Object with given maximal error at minimum confidence. <br>
 * Both numbers need to be in the range 0-1.0<br>
 * The default step of a sample size is 10
 * @param error
 * @param confidence
 */
	public SamplingBasedAveragerRDJ(double error, double confidence) {
		this(error, confidence, 10);
	}
	
	/**
	 * Creates a new Object with given maximal error at minimum confidence. Both numbers need to be in the range 0-1.0
	 * @param error
	 * @param confidence
	 */
	
	public SamplingBasedAveragerRDJ(double error, double confidence, int w) {
		assert(error>0&&error<=1.0);
		assert(confidence>0&&confidence<1.0);
		this.error = error;
		this.confidence = confidence;
		W = w;
	}

	public double randomSampling(Sampler s) {
		double rdj = 0.0D;
		int r1 = 0;
		
		//define the number of sample buckets
		int r2 = (int) Math.ceil(Math.log(1.0D / confidence) / Math.log(2D));
		
		//for each sample backer keep the sum of sampled values
		double jsSum[] = new double[r2];

		
		boolean interrupted = false;
		do {
			//for each sample bucket sample W vaues
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
//increase sample size by W
			r1 += W;
			/*
			for (int i = 0; i < r2; i++)
				rdj += jsSum[i];
*/
			//select a bucket with median sum of sampled values and assume it contains the representative sample and thus the value is the most accurate.
			rdj = median(jsSum) / (double) r1;
			
			//error is assumed to decrease with growing sample size (r1)
			abs_error = 1.0D / Math.sqrt(r1);
			
			estimated_error=abs_error / Math.abs(rdj - abs_error);
			//continue adding samples to buckets if error is too big.
			
		} while (!interrupted && estimated_error > error);
		return rdj;
	}

	  private  double median(double a[])
	    {
	        Arrays.sort(a);
	        double sum = 0.0D;
	        double ad[];
	        int j = (ad = a).length;
	        for(int i = 0; i < j; i++)
	        {
	            double cur = ad[i];
	            sum += cur;
	        }

	        double mean = sum / (double)a.length;
	        double before = a[0];
	        double ad1[];
	        int l = (ad1 = a).length;
	        for(int k = 0; k < l; k++)
	        {
	            double cur = ad1[k];
	            if(cur > mean)
	                return Math.abs(cur - mean) >= Math.abs(cur - before) ? before : cur;
	            before = cur;
	        }

	        return a[a.length - 1];
	    }

	public Double getEstimatedError() {
		return estimated_error;
	}
	public double getError() {
		return error;
	}
	public double getConfidence() {
		return confidence;
	}
}
