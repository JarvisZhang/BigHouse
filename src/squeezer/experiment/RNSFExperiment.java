package squeezer.experiment;

import squeezer.RandomNGenerator;
import squeezer.ServiceTimeFilter;
import squeezer.SurvivorGenerator;
import core.Constants.FilterType;
import core.Constants.WorkType;
import core.Experiment;
import core.ExperimentInput;
import core.ExperimentOutput;
import core.Constants.StatName;
import datacenter.DataCenter;
import datacenter.Server;
import generator.EmpiricalGenerator;
import generator.MTRandom;
import math.EmpiricalDistribution;

public class RNSFExperiment {
	
	public RNSFExperiment() {
		
	}

	public void run(String workloadDir, String workload, int nServers, double targetRho, int randomNum, double sla, int survivorNum, int printSamples, int seed) {

		// service file
		String arrivalFile = workloadDir+"workloads/"+workload+".arrival.cdf";
		String serviceFile = workloadDir+"workloads/"+workload+".service.cdf";

		// specify distribution
		int cores = 4;
		int sockets = 1;
//		double targetRho = .75;
		
		double scaledByServer = (double) nServers / (double) survivorNum;
		
//		EmpiricalDistribution arrivalDistribution = EmpiricalDistribution.loadDistribution(arrivalFile, 1e-3);
//		EmpiricalDistribution serviceDistribution = EmpiricalDistribution.loadDistribution(serviceFile, 1e-3);
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
		
		double serviceTime50 = serviceDistribution.getQuantile(.50);
		double serviceTime95 = serviceDistribution.getQuantile(.95);
		double serviceTime99 = serviceDistribution.getQuantile(.99);
		double serviceTime999 = serviceDistribution.getQuantile(.999);

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
		
		System.out.println("sla: " + sla);
		
		System.out.println("Service time 50th " + serviceTime50);
		System.out.println("Service time 95th " + serviceTime95);
		System.out.println("Service time 99th " + serviceTime99);
		System.out.println("Service time 999th " + serviceTime999);
		
		System.out.println("\n------------------\n");

		// setup experiment
		ExperimentInput experimentInput = new ExperimentInput();		

		// add experiment outputs
		ExperimentOutput experimentOutput = new ExperimentOutput();
		experimentOutput.addOutput(StatName.SOJOURN_TIME, .05, .999, .05, 5000);
		experimentOutput.addOutput(StatName.WAIT_TIME, .05, .999, .05, 5000);
		
		experimentOutput.setJobCollector(nServers);
		
		MTRandom rand = new MTRandom(1);
		Experiment experiment = new Experiment("Service Filter experiment", rand, experimentInput, experimentOutput);
		
		// setup datacenter
		DataCenter dataCenter = new DataCenter();
		
		for(int i = 0; i < nServers; i++) {
			MTRandom arrivalRand = new MTRandom(1);
			EmpiricalGenerator arrivalGenerator  = new EmpiricalGenerator(arrivalRand, arrivalDistribution, "arrival", arrivalScale);
			MTRandom serviceRand = new MTRandom(seed + i);
			EmpiricalGenerator serviceGenerator  = new EmpiricalGenerator(serviceRand, serviceDistribution, "service" + i, 1.0);
			Server server = new Server(sockets, cores, experiment, arrivalGenerator, serviceGenerator);
			dataCenter.addServer(server);
		}//End for i
		
		long maxSize = 1000;
//		double sla = 0.0200;//ms
		int warmingUpNum = 5000;
		
		ServiceTimeFilter serviceTimeFilter = new ServiceTimeFilter(maxSize, sla, warmingUpNum);
		RandomNGenerator randomNGenerator = new RandomNGenerator(randomNum);
		
		experimentInput.setRandomNGenerator(randomNGenerator);
		experimentInput.setServiceTimeFilter(serviceTimeFilter);
		experimentInput.setDataCenter(dataCenter);
		experimentInput.setWorkType(WorkType.SPECULATE);
		experimentInput.setFilterType(FilterType.RANDOM_N);
		experimentInput.setFilterType(FilterType.ServiceFilter);

		SurvivorGenerator survivorGenerator = new SurvivorGenerator(survivorNum);
		experimentInput.setSurvivorGenerator(survivorGenerator);
		
//		int orderOfMag = 8;
//		int printSamples = (int) Math.pow(10, orderOfMag);
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
		
		long filteredJobs = experiment.getJobCollector().getFilteredNum();
		System.out.println("Filtered Jobs: " + filteredJobs);
		long finishedJobs = experiment.getJobCollector().getFinishedNum();
		System.out.println("Finished Jobs: " + finishedJobs);
		double savedPortion = (double) filteredJobs / (double) (filteredJobs + finishedJobs);
		System.out.println("savedPortion: " + savedPortion);

		System.out.println("Moving Average: " + experiment.getServieTimeFilter().getMovingAverage());
		
//		System.out.println("############ Response time CDF ##############");
//		experiment.getStats().getStat(StatName.SOJOURN_TIME).printCdf();
//		System.out.println("############ Response time Histogram ##############");
//		experiment.getStats().getStat(StatName.SOJOURN_TIME).printHistogram();
//		System.out.println("############ Waiting time CDF ##############");
//		experiment.getStats().getStat(StatName.WAIT_TIME).printCdf();
//		System.out.println("############ Waiting time Histogram ##############");
//		experiment.getStats().getStat(StatName.WAIT_TIME).printHistogram();
		
//		experiment.getJobCollector().printAllSample();
	}//End run()
	
	public static void main(String[] args) {
		double targetRho = Double.valueOf(args[3]);
		int randomNum = Integer.valueOf(args[4]);
		double pruning = Double.valueOf(args[5]) / 1000;
		int survivorNum = Integer.valueOf(args[6]);
		int orderOfMag = Integer.valueOf(args[7]);
		int seed = Integer.valueOf(args[8]);
		System.out.println("===== Random N Service Filter Experiment =====");
		System.out.println("workload: " + args[1]);
		System.out.println("worker numbers: " + args[2]);
		System.out.println("rho: " + targetRho);
		System.out.println("random numbers: " + randomNum);
		System.out.println("pruning: " + pruning);
		System.out.println("survivor numbers: " + survivorNum);
		System.out.println("order of magtitude: " + orderOfMag);
		System.out.println("random seed: " + seed);
		System.out.println("========================================");
		RNSFExperiment exp  = new RNSFExperiment();
		exp.run(args[0],args[1],Integer.valueOf(args[2]),targetRho,randomNum,pruning,survivorNum,orderOfMag,seed);
	}
}
