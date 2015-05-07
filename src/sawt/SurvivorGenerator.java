package sawt;

import java.util.Vector;

import core.Job;
import datacenter.Server;

public class SurvivorGenerator {
	
	private Vector<Survivor> survivorVec;
	
	private int survivorNum;
	
	public SurvivorGenerator(final int survivorNum) {
		this.survivorVec = new Vector<Survivor>();
		this.survivorNum = survivorNum;
	}
	
	public void generate(Job job, Server server) {
		for(Survivor survivor : this.survivorVec) {
			if(survivor.getJobId() == job.getJobId()) {
				this.setSurvivor(job, server, survivor);
				return;
			}
		}//end for
		Survivor survivor = new Survivor(job, server, this.survivorNum);
		this.survivorVec.add(survivor);
		this.setSurvivor(job, server, survivor);
	}
	
	public void generate(Job job, Server server, Vector<Integer> serverVec) {
		for(Survivor survivor : this.survivorVec) {
			if(survivor.getJobId() == job.getJobId()) {
				this.setSurvivor(job, server, survivor);
				return;
			}
		}//end for
		Survivor survivor = new Survivor(job, server, this.survivorNum, serverVec);
		this.survivorVec.add(survivor);
		this.setSurvivor(job, server, survivor);
	}
	
	private void setSurvivor(Job job, Server server, Survivor survivor) {
		int serverId = server.getServerId();
		job.setSurvivor(survivor.containsServerId(serverId));
		survivor.updateCount();
		if(survivor.isEmpty())
			this.survivorVec.remove(survivor);
	}

}
