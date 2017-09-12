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

#include <iostream>
#include <cmath>
#include <vector>

#include "../de.l3s/SamplingBasedAverager.h"
using namespace std;
void println(std::string s = "") {
	cout << s << endl;
}
std::string format(std::string format, double d) {
	std::string ret;
	ret += std::to_string(d);
	return ret;
}
std::string format(std::string format, std::string s) {
	return s;
}

// Count the sampling rounds and gather stats about the sampling error, just for fun/statistics. Don't need is in the
// real task.
static int samplerounds;
static std::vector<double> sampleErrors;

int main() {

	// SamplingBasedAverager(_error_, _confidence_) - Estimate a value with
	// at most _error_ % error guaranteed with at least _confidence_ %.
	double _error_ = 0.05, _confidence_ = 0.95;
	static SamplingBasedAverager SBA(_error_, _confidence_);

	// Our playground range 0-N
	static int N = 100000000;
	srand(time(NULL));

	cout << "start" << endl;
	class MySampler: public Sampler {
	public:
		double sampleOne() {
			samplerounds++;
			if (SBA.getEstimatedError() > 0) {
				sampleErrors.push_back(SBA.getEstimatedError());
			}

			// We are computing random number from the
			// range (0-N].

			//Please note, the averager does not know our range
			return (rand() % N + 1);
		}
	};
	MySampler mysampler;
	double average = SBA.randomSampling(mysampler);

	// Best printed as excel table :)
	println(
			std::string("The average of the numbers of the range (0-")
					+ std::to_string(N)
					+ std::string("], estimation and statistics:"));
	println(
			"Computed estimation should have at most "
					+ std::to_string(_error_ * 100) + "% error, with "
					+ format(std::string("%.2f"), _confidence_ * 100.)
					+ "% confidence");
	println();
	println("              \t" + format("%10s", "Exact") + "\tEstimated");
	println(
			"Average       :\t" + format("%10s", (N / 2)) + "\t"
					+ std::to_string((int) (average)));
	println();
	println(
			"Error         :\t"
					+ format("%10s",
							format("%.2f",
									(abs(average - (N / 2.)) / (N / 2)) * 100))
					+ "\t" + format("%.2f", (SBA.getEstimatedError()) * 100));
	println(
			"Operations    :\t" + format("%10s", N) + "\t"
					+ std::to_string(samplerounds));
	println(
			"x times faster:\t" + format("%10s", 1) + "\t"
					+ std::to_string(N / (samplerounds)));
	println();

	cout << ("Development of the sample error: ");
	int step = sampleErrors.size() / 20;

	for (unsigned int i = 0; i < sampleErrors.size(); i++) {
		if (i % step == 0) {
			cout << (format("%.3f", sampleErrors[i]) + " ");
		}
	}

	/*
	 if(sampleErrors.size()>1)
	 println(format("%.3f",SBA.getEstimatedError()));


	 for (int i = 0; i < sampleErrors.size(); i++) {

	 println(format("%.3f",sampleErrors[i]));

	 }
	 */
	println(format("%.3f", SBA.getEstimatedError()));

	return 0;
}

