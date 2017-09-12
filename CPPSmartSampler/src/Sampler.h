/*
 * Sampler.h
 *
 *  Created on: 11.09.2017
 *      Author: zerr
 */

#ifndef SAMPLER_H_
#define SAMPLER_H_

class Sampler {
public:
	virtual double sampleOne()=0;
	virtual ~Sampler(){}
};

#endif /* SAMPLER_H_ */
