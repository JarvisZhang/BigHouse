package sawt;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import generator.EmpiricalGenerator;
import generator.MTRandom;
import core.Experiment;
import core.ExperimentInput;
import core.ExperimentOutput;
import core.Constants.StatName;
import datacenter.DataCenter;
import datacenter.Server;
import math.EmpiricalDistribution;

public class SingleServerTest {

	public SingleServerTest() {
		
	}
	
	public void run(String workloadDir, String workload, double targetRho, double sla, int seed) {
		
		// service file
		String arrivalFile = workloadDir+"workloads/"+workload+".arrival.cdf";
		String serviceFile = workloadDir+"workloads/"+workload+".service.cdf";

		// specify distribution
		int cores = 4;
		int sockets = 1;
		
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
		
		ExperimentInput experimentInput = new ExperimentInput();		

		// add experiment outputs
		ExperimentOutput experimentOutput = new ExperimentOutput();
		int warmingUp = 6000;
		experimentOutput.addOutput(StatName.SOJOURN_TIME, .05, .999, .05, warmingUp);
		experimentOutput.addOutput(StatName.WAIT_TIME, .05, .999, .05, warmingUp);
		
		MTRandom rand = new MTRandom(1);
		Experiment experiment = new Experiment("Single server experiment", rand, experimentInput, experimentOutput);
	
		DataCenter dataCenter = new DataCenter();
		
		MTRandom arrivalRand = new MTRandom(1);
		EmpiricalGenerator arrivalGenerator  = new EmpiricalGenerator(arrivalRand, arrivalDistribution, "arrival", arrivalScale);
		MTRandom serviceRand = new MTRandom(seed);
		EmpiricalGenerator serviceGenerator  = new EmpiricalGenerator(serviceRand, serviceDistribution, "service", 1.0);
		Server server = new Server(sockets, cores, experiment, arrivalGenerator, serviceGenerator);
		dataCenter.addServer(server);
		
		experimentInput.setDataCenter(dataCenter);
		
		int orderOfMag = 8;
		int printSamples = (int) Math.pow(10, orderOfMag);
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
		double prob = experiment.getStats().getStat(StatName.SOJOURN_TIME).getCdfValue(sla);
		this.printProbToFile(targetRho, sla, prob);
	}
	
	private void printProbToFile(double targetRho, double sla, double prob) {
		try {
			int rho = (int) (targetRho * 100);
			int slams = (int) (sla * 1000);
			FileWriter fileWriter = new FileWriter("prob/" + "prob_" + slams + ".func", true);
			fileWriter.write(rho + "\t" + prob + "\n");
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void buildFunc(String workloadDir, String workload, double startRho, double endRho, double sla, int seed) {
		while(startRho < endRho) {
			SingleServerTest singleServerTest = new SingleServerTest();
			singleServerTest.run(workloadDir, workload, startRho, sla, seed);
			startRho += 0.01;
		}
	}
	
	public static void main(String[] args) {
		double startRho = Double.valueOf(args[2]);
		double endRho = Double.valueOf(args[3]);
		double sla = Double.valueOf(args[4]);
		int seed = Integer.valueOf(args[5]);
		System.out.println("===== Single Server Test =====");
		System.out.println("Start Rho: " + startRho);
		System.out.println("End Rho: " + endRho);
		System.out.println("SLA: " + sla);
		System.out.println("random seed: " + seed);
		System.out.println("========================================");
		buildFunc(args[0], args[1], startRho, endRho, sla, seed);
	}
}
