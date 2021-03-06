/**
 * Copyright (c) 2011 The Regents of The University of Michigan
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met: redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer;
 * redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution;
 * neither the name of the copyright holders nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @author David Meisner (meisner@umich.edu)
 *
 */

package squeezer.experiment;

import squeezer.RandomNGenerator;
import generator.EmpiricalGenerator;
import generator.MTRandom;
import math.EmpiricalDistribution;
import core.Experiment;
import core.ExperimentInput;
import core.ExperimentOutput;
import core.Constants.FilterType;
import core.Constants.StatName;
import core.Constants.WorkType;
import datacenter.DataCenter;
import datacenter.Server;

public class RandomNCollectAll {

	public RandomNCollectAll(){

	}
	
	public void run(String workloadDir, String workload, int nServers, double targetRho, int randomNum, double sla, int printSamples, int seed) {

		// service file
		String arrivalFile = workloadDir+"workloads/"+workload+".arrival.cdf";
		String serviceFile = workloadDir+"workloads/"+workload+".service.cdf";

		// specify distribution
		int cores = 4;
		int sockets = 1;
//		double targetRho = .5;
		
		EmpiricalDistribution arrivalDistribution = EmpiricalDistribution.loadDistribution(arrivalFile, 1);
		EmpiricalDistribution serviceDistribution = EmpiricalDistribution.loadDistribution(serviceFile, 1);

		double averageInterarrival = arrivalDistribution.getMean();
		double averageServiceTime = serviceDistribution.getMean();
		double qps = 1/averageInterarrival;
		double rho = qps/(cores*(1/averageServiceTime));
		double arrivalScale = rho/targetRho;
		averageInterarrival = averageInterarrival*arrivalScale;
		double serviceRate = 1/averageServiceTime;
		double scaledQps =(qps/arrivalScale);

		System.out.println("Cores " + cores);
		System.out.println("rho " + rho);		
		System.out.println("recalc rho " + scaledQps/(cores*(1/averageServiceTime)));
		System.out.println("arrivalScale " + arrivalScale);
		System.out.println("Average interarrival time " + averageInterarrival);
		System.out.println("Average service time " + averageServiceTime);
		System.out.println("QPS as is " +qps);
		System.out.println("Scaled QPS " +scaledQps);
		System.out.println("Service rate as is " + serviceRate);
		System.out.println("Service rate x" + cores + " is: "+ (serviceRate)*cores);
		System.out.println("\n------------------\n");

		// setup experiment
		ExperimentInput experimentInput = new ExperimentInput();		

		MTRandom rand = new MTRandom(1);

		// add experiment outputs
		ExperimentOutput experimentOutput = new ExperimentOutput();
//		experimentOutput.addOutput(StatName.SOJOURN_TIME, .05, .999, .05, 5000);
//		experimentOutput.addOutput(StatName.WAIT_TIME, .05, .999, .05, 5000);
		int warmingUp = 6000 * nServers;
		experimentOutput.addOutput(StatName.SOJOURN_TIME, .05, .999, .05, warmingUp);
		experimentOutput.addOutput(StatName.WAIT_TIME, .05, .999, .05, warmingUp);
		
		experimentOutput.setJobCollector(nServers);
		
		Experiment experiment = new Experiment("Random N experiment", rand, experimentInput, experimentOutput);
		
		// setup datacenter
		DataCenter dataCenter = new DataCenter();
		
		for(int i = 0; i < nServers; i++) {
			MTRandom arrivalRand = new MTRandom(1);
			EmpiricalGenerator arrivalGenerator  = new EmpiricalGenerator(arrivalRand, arrivalDistribution, "arrival", arrivalScale);
//			MTRandom serviceRand = new MTRandom(2 + i);
			MTRandom serviceRand = new MTRandom(seed + i);
			EmpiricalGenerator serviceGenerator  = new EmpiricalGenerator(serviceRand, serviceDistribution, "service" + i, 1.0);
			Server server = new Server(sockets, cores, experiment, arrivalGenerator, serviceGenerator);
			dataCenter.addServer(server);
		}//End for i
		
		RandomNGenerator randomNGenerator = new RandomNGenerator(randomNum);
		experimentInput.setRandomNGenerator(randomNGenerator);
		
		experimentInput.setDataCenter(dataCenter);
		experimentInput.setWorkType(WorkType.DEFAULT);
		experimentInput.setFilterType(FilterType.RANDOM_N);
		
//		int orderOfMag = 8;
//		int orderOfMag = 9;
//		int printSamples = (int) Math.pow(10, orderOfMag) / 3;
		experiment.setEventLimit(printSamples);

		// run the experiment
		experiment.run();

		// display results
		System.out.println("====== Results ======");
		double responseTimeMean = experiment.getStats().getStat(StatName.SOJOURN_TIME).getAverage();
		System.out.println("Response Mean: " + responseTimeMean);
		double responseTime95th = experiment.getStats().getStat(StatName.SOJOURN_TIME).getQuantile(.95);
		System.out.println("Response 95: " + responseTime95th);
		double responseTime99th = experiment.getStats().getStat(StatName.SOJOURN_TIME).getQuantile(.99);
		System.out.println("Response 99: " + responseTime99th);
		double responseTime999th = experiment.getStats().getStat(StatName.SOJOURN_TIME).getQuantile(.999);
		System.out.println("Response 999: " + responseTime999th);
		double waitingTimeMean = experiment.getStats().getStat(StatName.WAIT_TIME).getAverage();
		System.out.println("Waiting Mean: " + waitingTimeMean);
		double waitingTime95th = experiment.getStats().getStat(StatName.WAIT_TIME).getQuantile(.95);
		System.out.println("Waiting 95: " + waitingTime95th);
		double waitingTime99th = experiment.getStats().getStat(StatName.WAIT_TIME).getQuantile(.99);
		System.out.println("Waiting 99: " + waitingTime99th);
		double waitingTime999th = experiment.getStats().getStat(StatName.WAIT_TIME).getQuantile(.999);
		System.out.println("Waiting 999: " + waitingTime999th);
		
		try {
			double responseSlaQuantile = experiment.getStats().getStat(StatName.WAIT_TIME).getCdfValue(sla);
			System.out.println("Waiting < " + sla + ": " + responseSlaQuantile);
			double responsePredictValue = sla - averageServiceTime;
			double responsePredictQuantile = experiment.getStats().getStat(StatName.WAIT_TIME).getCdfValue(responsePredictValue);
			System.out.println("Waiting < " + responsePredictValue + ": " + responsePredictQuantile);
		}
		catch(Exception e) {
			
		}
		
		System.out.println("############ Response time CDF ##############");
		experiment.getStats().getStat(StatName.SOJOURN_TIME).printCdf();
		System.out.println("############ Response time Histogram ##############");
		experiment.getStats().getStat(StatName.SOJOURN_TIME).printHistogram();
		System.out.println("############ Waiting time CDF ##############");
		experiment.getStats().getStat(StatName.WAIT_TIME).printCdf();
		System.out.println("############ Waiting time Histogram ##############");
		experiment.getStats().getStat(StatName.WAIT_TIME).printHistogram();
		
	}//End run()
	
	public static void main(String[] args) {
		double targetRho = Double.valueOf(args[3]);
		int randomNum = Integer.valueOf(args[4]);
		double sla = Double.valueOf(args[5]);
		int orderOfMag = Integer.valueOf(args[6]);
		int seed = Integer.valueOf(args[7]);
		System.out.println("===== Random N Experiment =====");
		System.out.println("workload: " + args[1]);
		System.out.println("worker numbers: " + args[2]);
		System.out.println("rho: " + targetRho);
		System.out.println("random numbers: " + randomNum);
		System.out.println("sla: " + sla);
		System.out.println("order of magtitude: " + orderOfMag);
		System.out.println("random seed: " + seed);
		System.out.println("========================================");
		RandomNCollectAll exp  = new RandomNCollectAll();
		exp.run(args[0],args[1],Integer.valueOf(args[2]),targetRho,randomNum,sla,orderOfMag,seed);
	}
	
}//End Random1Experimen
