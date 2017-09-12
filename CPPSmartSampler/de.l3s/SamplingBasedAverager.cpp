/*
 * SamplingBasedAverager.cpp
 *
 *  Created on: 11.09.2017
 *      Author: zerr
 */

#include "../de.l3s/SamplingBasedAverager.h"

#include <cmath>
#include <iostream>
#include <algorithm>
#include "../de.l3s/InterruptedException.h"

SamplingBasedAverager::SamplingBasedAverager() {
	this->error = 0.05;
	this->confidence = 0.95;
	this->W = 10;
	this->estimated_error=-1.;

}

SamplingBasedAverager::SamplingBasedAverager(double error, double confidence) {
	this->error = error;
	this->confidence = confidence;
	this->W = 10;
	this->estimated_error=-1.;
}

SamplingBasedAverager::SamplingBasedAverager(double error, double confidence,
		double W) {
	this->error = error;
	this->confidence = confidence;
	this->W = W;
	this->estimated_error=-1.;
}

double SamplingBasedAverager::randomSampling(Sampler& s) {
	double rdj = 0.0;
	int r1 = 0;

	// define the number of sample buckets
	int r2 = (int) ceil(log2(1 / (1 - confidence))); // (int)
																			// Math.ceil(Math.log(1.0D
																			// /
																			// confidence)
																			// /
																			// Math.log(2D));

	// for each sample backer keep the sum of sampled values
	double* jsSum = new double[r2];


	int interrupted = 0;
	do {
		// for each sample bucket sample W values
		for (int i = 0; i < r2; i++) {
			for (int j = 1; j <= W; j++) {
				try {
					jsSum[i] += s.sampleOne();
				} catch (InterruptedException &ex) {

					interrupted = 1;
					goto sampleloop;
				}

			}
		}
		sampleloop:
		// increase sample size by W
		r1 += W;

		BasicStats stats = getStats(jsSum, r2, r1);

		rdj = stats.getMedian() / r1;

		//Some statistician take here double standard deviation (stats.getStddev()*2)
		//it would increase the number of required sample rounds by 2-3 and increase accuracy. you can play with it.
		estimated_error = (stats.getStddev()) / (stats.getMedian());

	} while (!interrupted && estimated_error > error);
	return rdj;

}

double SamplingBasedAverager::getEstimatedError() {
	return estimated_error;
}



BasicStats SamplingBasedAverager::getStats(double* a,  int len, int r1) {

	double median = a[0], average;

	//std::sort(std::begin(a), std::end(a));

	double sum = 0.0;

	int j = len;
	for (int i = 0; i < j; i++) {
		double cur = a[i];
		sum += cur;
	}
	average = sum / len;

	double mean = sum / (double) len;
	double before = a[0];

	int l = len;

	bool found = false;
	for (int k = 0; k < l; k++) {
		double cur = a[k];
		if (cur > mean) {
			found = true;
			median = abs(cur - mean) >= abs(cur - before) ? before : cur;
			break;
		}
		before = cur;
	}

	if (!found) {
		median = a[len - 1];
	}

	double diff = 0;
	for (int i = 0; i < j; i++) {
		double cur = a[i];
		diff += pow(cur - average, 2);
	}
	diff = sqrt(diff / len);

	return  BasicStats(median, average, diff);


}
