/*
 * SamplingBasedAveragerEx.cpp
 *
 *  Created on: 11.09.2017
 *      Author: zerr
 */

#include "SamplingBasedAverager.h"
#include <iostream>
#include <cmath>
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

int main() {

	// SamplingBasedAverager(_error_, _confidence_) - Estimate a value with
	// at most _error_ % error guaranteed with at least _confidence_ %.
	double _error_ = 0.05, _confidence_ = 0.95;
	SamplingBasedAverager SBA(_error_, _confidence_);

	// Our playground range 0-N
	int N = 100000000;
	srand (time(NULL));
	cout<<"start"<<endl;
	class MySampler: public Sampler {
	private:
		int N;
	public:
		MySampler(int N) {
			this->N = N;
			samplerounds=0;
		}

		int samplerounds;
		double sampleOne() {
			samplerounds++;

			// We are computing random number from the
			// range (0-N].
		//	double pairaverage = (rand() % this->N + 1);

			//Please note, the averager does not know our range
			return (rand() % this->N + 1);
		}
	};
	MySampler mysampler(N);
	double average = SBA.randomSampling(mysampler);

	// Best printed as excel table :)
	println(std::string("The average of the numbers of the range (0-") + std::to_string(N)+ std::string("], estimation and statistics:") );
	println(
			"Computed estimation should have at most " + std::to_string(_error_* 100)
					+ "% error, with "
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
									(abs(average - (N / 2.)) / (N / 2))* 100)) + "\t"
					+ format("%.2f", (SBA.getEstimatedError()) * 100));
	println(
			"Operations    :\t" + format("%10s", N) + "\t"
					+ std::to_string(mysampler.samplerounds));
	println(
			"x times faster:\t" + format("%10s", 1) + "\t"
					+ std::to_string(N / (mysampler.samplerounds)));
	println();

	/*
	 if(sampleErrors.size()>1)
	 print(format("%.3f",SBA.getEstimatedError()));
	 println();

	 for (int i = 0; i < sampleErrors.size(); i++) {

	 println(format("%.3f",sampleErrors.get(i)));

	 }

	 println(format("%.3f",SBA.getEstimatedError()));
	 */

	return 0;
}

