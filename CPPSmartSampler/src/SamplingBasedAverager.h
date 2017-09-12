/*
 * SamplingBasedAverager.h
 *
 *  Created on: 11.09.2017
 *      Author: zerr
 */

#ifndef SAMPLINGBASEDAVERAGER_H_
#define SAMPLINGBASEDAVERAGER_H_
#include "BasicStats.h"
#include "Sampler.h"

class SamplingBasedAverager {
public:
	SamplingBasedAverager();
	SamplingBasedAverager(double error, double confidence);
	SamplingBasedAverager(double error, double confidence, double W);

	double randomSampling(Sampler &s);
	double getEstimatedError();

	virtual ~SamplingBasedAverager(){}
private:
	double error, confidence, estimated_error;
	int W;

	BasicStats getStats(double* a, int r1);
};

#endif /* SAMPLINGBASEDAVERAGER_H_ */
