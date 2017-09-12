/*
 * BasicStats.h
 *
 *  Created on: 11.09.2017
 *      Author: zerr
 */

#ifndef BASICSTATS_H_
#define BASICSTATS_H_

class BasicStats {
	double median;
	double mean;
	double stddev;

public:
	BasicStats(const BasicStats &stats) {
		this->mean = stats.mean;
		this->median = median;
		this->stddev = stats.stddev;
	}
	BasicStats() {
		median = 0;
		mean = 0;
		stddev = 0;
	}
	BasicStats(double median, double mean, double stddev) {
		this->median = median;
		this->mean = mean;
		this->stddev = stddev;
	}
	double getMedian() {
		return median;
	}
	double getMean() {
		return mean;
	}
	double getStddev() {
		return stddev;
	}
};

#endif /* BASICSTATS_H_ */
