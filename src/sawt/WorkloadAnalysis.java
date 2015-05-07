package sawt;

import math.EmpiricalDistribution;

public class WorkloadAnalysis {

	public static void main(String[] args) {
		
		String workloadDir = "/Users/zhangzuowei/Documents/workspace/BigHouse/";
		String workload = "search";
		
		String arrivalFile = workloadDir+"workloads/"+workload+".arrival.cdf";
		String serviceFile = workloadDir+"workloads/"+workload+".service.cdf";
		
		EmpiricalDistribution arrivalDistribution = EmpiricalDistribution.loadDistribution(arrivalFile, 1);
		EmpiricalDistribution serviceDistribution = EmpiricalDistribution.loadDistribution(serviceFile, 1);

		double sla = .020;
		
		double serviceMean = serviceDistribution.getMean();
		double SLAQuantile = serviceDistribution.getCdfValue(sla);
		
		System.out.println("SLAQuantile: " + SLAQuantile);
	}

}
