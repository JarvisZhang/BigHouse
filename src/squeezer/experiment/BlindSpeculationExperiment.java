package squeezer.experiment;

import core.Experiment;
import core.ExperimentInput;
import core.ExperimentOutput;
import core.Constants.StatName;
import datacenter.DataCenter;
import datacenter.Server;
import generator.EmpiricalGenerator;
import generator.MTRandom;
import math.EmpiricalDistribution;

public class BlindSpeculationExperiment {
	
	public BlindSpeculationExperiment() {
		
	}

	public void run(String workloadDir, String workload, int nServers) {

		// service file
		String arrivalFile = workloadDir+"workloads/"+workload+".arrival.cdf";
		String serviceFile = workloadDir+"workloads/"+workload+".service.cdf";

		// specify distribution
		int cores = 4;
		int sockets = 1;
		double targetRho = .5;
		
		EmpiricalDistribution arrivalDistribution = EmpiricalDistribution.loadDistribution(arrivalFile, 1e-3);
		EmpiricalDistribution serviceDistribution = EmpiricalDistribution.loadDistribution(serviceFile, 1e-3);

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

		// add experiment outputs
		ExperimentOutput experimentOutput = new ExperimentOutput();
		experimentOutput.addOutput(StatName.SOJOURN_TIME, .05, .95, .05, 5000);
		experimentOutput.addOutput(StatName.WAIT_TIME, .05, .95, .05, 5000);
		MTRandom rand = new MTRandom(1);
		Experiment experiment = new Experiment("Unlimited test", rand, experimentInput, experimentOutput);
		
		// setup datacenter
		DataCenter dataCenter = new DataCenter();
		
		for(int i = 0; i < nServers; i++) {
			MTRandom arrivalRand = new MTRandom(1);
			EmpiricalGenerator arrivalGenerator  = new EmpiricalGenerator(arrivalRand, arrivalDistribution, "arrival", arrivalScale);
			MTRandom serviceRand = new MTRandom(2 + i);
			EmpiricalGenerator serviceGenerator  = new EmpiricalGenerator(serviceRand, serviceDistribution, "service" + i, 1.0);
			Server server = new Server(sockets, cores, experiment, arrivalGenerator, serviceGenerator);
			dataCenter.addServer(server);
		}//End for i
		
		experimentInput.setDataCenter(dataCenter);

		// run the experiment
		experiment.run();

		// display results
		System.out.println("====== Results ======");
		double responseTimeMean = experiment.getStats().getStat(StatName.SOJOURN_TIME).getAverage();
		System.out.println("Response Mean: " + responseTimeMean);
		double responseTime95th = experiment.getStats().getStat(StatName.SOJOURN_TIME).getQuantile(.95);
		System.out.println("Response 95: " + responseTime95th);
		double waitingTimeMean = experiment.getStats().getStat(StatName.WAIT_TIME).getAverage();
		System.out.println("Waiting Mean: " + waitingTimeMean);
		double waitingTime95th = experiment.getStats().getStat(StatName.WAIT_TIME).getQuantile(.95);
		System.out.println("Waiting 95: " + waitingTime95th);
		
//		experiment.getJobCollector().printAllSample();
	}//End run()
	
	public static void main(String[] args) {
		System.out.println("===== Blind Speculation Experiment =====");
		System.out.println("workload: " + args[1]);
		System.out.println("worker numbers: " + args[2]);
		System.out.println("========================================");
		BlindSpeculationExperiment exp  = new BlindSpeculationExperiment();
		exp.run(args[0],args[1],Integer.valueOf(args[2]));
	}
}
