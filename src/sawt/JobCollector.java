package sawt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import core.Job;
import datacenter.Server;

public class JobCollector {
	
	private HashMap<Server, Vector<Job>> jobInfo;
	
	private static long sampleNumberMax = 15;
	
	private boolean stop = false;
	
	public JobCollector() {
		this.jobInfo = new HashMap<Server, Vector<Job>>();
	}
	
	public void addSample(Server server, Job job) {
		if(stop)
			return;
		if(jobInfo.containsKey(server)) {
			Vector<Job> jobVector = this.jobInfo.get(server);
			jobVector.add(job);
		}
		else {
			Vector<Job> jobVector = new Vector<Job>();
			jobVector.add(job);
			this.jobInfo.put(server, jobVector);
		}
		this.updateStop();
	}

	public void updateStop() {
		for(Map.Entry<Server, Vector<Job>> entry : jobInfo.entrySet()) {
//			System.out.println("Server "
//					+ entry.getKey().getServerId()
//					+ "'s size: "
//					+ entry.getValue().size());
			if(entry.getValue().size() < JobCollector.sampleNumberMax)
				return;
		}
		this.stop = true;
	}
	
	public void printAllSample() {
		for(Map.Entry<Server, Vector<Job>> entry : jobInfo.entrySet()) {
			System.out.println("Server " + entry.getKey().getServerId());
			for(Job job : entry.getValue()) {
				System.out.println("Job "
						+ job.getJobId()
						+ ": arrivalTime = "
						+ job.getArrivalTime()
						+ ", startTime = "
						+ job.getStartTime()
						+ ", finishTime = "
						+ job.getFinishTime());
			}
			System.out.println("----------");
		}
	}
}
