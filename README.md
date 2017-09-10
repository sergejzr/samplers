# samplers
<b>1. What is it useful for:</b>
Given naximal expected error and minimal expected confidence interval, the smart sampling will estimate the value of any property of the given dataset of any size, in constant time and the error will not exceed given value under given confidence interval. It is applicable for any BigData set or BigData stream and can reduce running time drastically, especially if the measurements are costly (for example involve calls to external APIs). It makes use of the <a href="https://en.wikipedia.org/wiki/Central_limit_theorem">Central Limit Theorem</a> and <a href="https://en.wikipedia.org/wiki/Chebyshev%27s_inequality">Chebyshev Inequality</a>.

Just start&nbsp;de.l3s.examples.SamplingBasedAveragerEx.java for an example

    //Example:
    double _error_ = 0.05, _confidence_ = 0.95;
		SamplingBasedAverager SBA = new SamplingBasedAverager(_error_, _confidence_, 1);


		double average = SBA.randomSampling(new Sampler() {
			Random r = new Random();
      
      //This is what YOU have to implement for your problem
			public double sampleOne() throws InterruptedException {
					// Throw a dice (note, the SBA does not "know" the distribution)
					return r.nextInt(6)+1;
			}
		});
    System.out.println("The estimated average:" + average +
    ". The error of this value does not exceed " +(_error_*100) +
    "% within "+_confidence_+"% confidence");

(new to sampling, statistics and co.? go to <a href="#samplingoverview">5 directly</a>)


<b>2. Example and History</b>
We used the algorith to estimate average diversity of a large set of objects (such as Web documents). The naive algorithm is quadratic in complexity, because every object has to be compared to every other object. The running times of the naive algorith on real datasets of couple of millions of objects exceeded years and the sampling algorithm estimated the value within minutes.

  <b>Publication:</b>
  Fan Deng, Stefan Siersdorfer, and Sergej Zerr. 2012. Efficient jaccard-based diversity analysis of large document   collections. In Proceedings of the 21st ACM international conference on Information and knowledge management (CIKM '12).<br>
  DOI=http://dx.doi.org/10.1145/2396761.2398445

<b>3. How does it work:</b>
The method is very simple. Having the confidence value, the algorithm first determines the number of sample buckets. In the first round it will add samples to every bucket, compute average value in each bucket and the standard deviation among those averages. This standard deviation does give the sampling error. If the sampling error is larger that the value we defined through &quot;error&quot; parameter, the algorithm will enlarge every bucket by size N and repeat.

<b>4. Pros, Cons, Specs and Chalenges</b>

* pro: The algorithm runs in constant time on any data set size
* pro: The estimated value is mathematically proven accurate within the given bounds.
* pro: Any property of the dataset can be estimated

* con: The running time can be infinite in case the needed value maximally varies within the dataset (all objects are different), but the most distributions follow Chebyshev inequality and computation usng the software package is very fast.

Challenges:
* The sampling procedure has to be implemented by the user and to be representative(!)</div><div>
Specs
* Brings best profit on very large data sets, or in case a sampling is costly (for example requires external API calls)

<b>5. What is sampling and why do we need it?</b>
<a name="samplingoverview"/>Sometimes the data we use to find an answer can be much much bigger than we think we can solve. For example, answers to questions: What is the average size of a fish in the ocean(?), what NLP tool works best for any page in the Web(?) or what is the taste of the average apple harvested this year(?). A classical &nbsp;way would be to touch all objects and derive needed values, but the size makes a trouble and furthermore, tasting all the apples would destroy all of them :).

The strategy is to sample. A sample is a (hopefully) much smaller sub set of a population (for example a catch of fish). We call a sample &quot;representative&quot; in case its property we are looking for closely corresponds to that real property of a population (for example the average size of a fish in the catch corresponds to the average size of the fish in the ocean). There exist several strategies to obtain a representative sample with &quot;random sampling&quot; as the most simple of them.
However, in praxis, a sample value will deviate from the real value, because we can rarely sample without an error. The help here comes from a central limit theorem, which tells that if you have a sufficient number of suffieciently large samples, the average of their averages will closely correspond to the real average (please read external material if you can not understand this here).

We are almost done, the final question is, how large should be the sample size and how many sample we need. In statistics there exists a rule of thumb (the fastest way is to type &quot;sample size calculator&quot; into google to find out). In other words, how many apple do we have to eat before we can say with certain probability that all of the harvest is sweet(?). However the real numbers will depend on the dataset properties (if all apples are sweet, tasting one is enough, but if all are different, we have indeed to taste all of them for an exact value).

This software package will do it for you. In short terms, it will try apples one by one and determine, when it had enough to tell about the population. The nice property of this method is that the effort does not depend on the size of the data set, but only on the variation of the needed value within this set. And using Chebyshev inequality, we bound the sampling error within a confidence interval (for example, means that with 95% confidence we can say that 95% of the apples are sweet).
