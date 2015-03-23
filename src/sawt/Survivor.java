package sawt;

import java.util.Random;

import core.Job;
import datacenter.Server;

public class Survivor {
	
	private long jobId;
	
	private int serverId;
	
	private int count;
	
	public Survivor(final Job job, final Server server) {
		this.jobId = job.getJobId();
		this.count = server.getExperiment().getServerNumber();
		this.serverId = this.assignServerId(this.count);
	}
	
	public long getJobId() {
		return this.jobId;
	}
	
	public long getServerId() {
		return this.serverId;
	}
	
	public void updateCount() {
		this.count--;
	}
	
	public boolean isEmpty() {
		return this.count == 0;
	}

	private int assignServerId(final int serverNum) {
		Random rand = new Random();
		return rand.nextInt(serverNum);
	}
}
