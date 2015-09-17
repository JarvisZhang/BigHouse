package squeezer;

import java.util.Vector;

import core.Job;
import datacenter.Server;

public class RandomInfo {

	private long jobId;
	
	private Vector<Integer> randomServerVec;
	
	private int count;
	
	public RandomInfo(final Job job, final Server server, final int randomNum) {
		this.jobId = job.getJobId();
		int serverNum = server.getExperiment().getServerNumber();
		this.count = serverNum;
		this.randomServerVec = ReservoirSampling.generate(randomNum, serverNum);
	}
	
	public long getJobId() {
		return this.jobId;
	}

	public Vector<Integer> getRandomSerVector() {
		return this.randomServerVec;
	}
	
	public boolean containsServerId(int serverId) {
		return this.randomServerVec.contains(serverId);
	}
	
	public void updateCount() {
		this.count--;
	}
	
	public boolean isEmpty() {
		return this.count == 0;
	}
}
