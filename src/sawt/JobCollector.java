package sawt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import core.Job;
import datacenter.Server;

public class JobCollector {
	
	private HashMap<Server, Vector<Job>> jobInfo;
	
	private HashMap<Long, Integer> jobCounter;
	
	private Vector<Long> finishJobVec;
	
	private long emptyJobNum;
	
	private int nServers;
	
	private static long sampleNumberMax = 15;
	
	private boolean stop = false;
	
	public JobCollector(int nServers) {
		this.jobInfo = new HashMap<Server, Vector<Job>>();
		this.jobCounter = new HashMap<Long, Integer>();
		this.emptyJobNum = 0;
		this.nServers = nServers;
		this.finishJobVec = new Vector<Long>();
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
						+ ": \tis survivor = "
						+ job.isSurvivor()
						+ ": \tarrivalTime = "
						+ job.getArrivalTime()
						+ ", \tstartTime = "
						+ job.getStartTime()
						+ ", \tfinishTime = "
						+ job.getFinishTime());
			}
			System.out.println("----------");
		}
	}
	
	public boolean returnToMaster(Job aJob) {
		int currentCount = this.jobCounter.getOrDefault(aJob.getJobId(), 0);
		if(currentCount != 0) {
			this.jobCounter.replace(aJob.getJobId(), ++currentCount);
		}
		else {
			this.jobCounter.put(aJob.getJobId(), 1);
		}
		
		boolean firstFinished = false;
		if(!(this.finishJobVec.contains(aJob.getJobId())) && aJob.getFinishTime() != 0) {
			this.finishJobVec.add(aJob.getJobId());
			firstFinished = true;
		}
		
		if(currentCount == this.nServers) {
			this.jobCounter.remove(aJob.getJobId());
			this.finishJobVec.remove(aJob.getJobId());
		}
		return firstFinished;
	}
}
