package sawt;

import java.util.Vector;

import core.Job;
import datacenter.Server;

public class Survivor {
	
	private long jobId;
	
	private Vector<Integer> survivorServerVec;
	
	private int count;
	
	public Survivor(final Job job, final Server server, final int survivorNum) {
		this.jobId = job.getJobId();
		int serverNum = server.getExperiment().getServerNumber();
		this.count = serverNum;
		this.survivorServerVec = ReservoirSampling.generate(survivorNum, serverNum);
	}
	
	public Survivor(final Job job, final Server server, final int survivorNum, final Vector<Integer> serverVec) {
		this.jobId = job.getJobId();
		int serverNum = serverVec.size();
		this.count = serverNum;
		Vector<Integer> sampleVector = ReservoirSampling.generate(survivorNum, serverNum);
		this.survivorServerVec = new Vector<Integer>();
		for(int sample : sampleVector) {
			this.survivorServerVec.add(serverVec.get(sample));
		}
		 
	}
	
	public long getJobId() {
		return this.jobId;
	}
	
	public boolean containsServerId(int serverId) {
		return this.survivorServerVec.contains(serverId);
	}
	
	public void updateCount() {
		this.count--;
	}
	
	public boolean isEmpty() {
		return this.count == 0;
	}
}
