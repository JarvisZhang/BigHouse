package squeezer;

import java.util.Vector;

import core.Job;
import datacenter.Server;

public class RandomNGenerator {

	private Vector<RandomInfo> randomInfoVec;
	
	private int randomNum;
	
	public RandomNGenerator(final int randomNum) {
		this.randomInfoVec = new Vector<RandomInfo>();
		this.randomNum = randomNum;
	}
	
	public RandomInfo generate(Job job, Server server) {
		for(RandomInfo randomInfo : randomInfoVec) {
			if(randomInfo.getJobId() == job.getJobId()) {
				this.setRandomValid(job, server, randomInfo);
				return randomInfo;
			}
		}// end for
		RandomInfo randomInfo = new RandomInfo(job, server, this.randomNum);
		this.randomInfoVec.add(randomInfo);
		this.setRandomValid(job, server, randomInfo);
		return randomInfo;
	}
	
	private void setRandomValid(Job job, Server server, RandomInfo randomInfo) {
		int serverId = server.getServerId();
		job.setRandomValid(randomInfo.containsServerId(serverId));
		randomInfo.updateCount();
		if(randomInfo.isEmpty())
			this.randomInfoVec.remove(randomInfo);
	}
}
