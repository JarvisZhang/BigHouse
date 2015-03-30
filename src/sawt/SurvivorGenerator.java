package sawt;

import java.util.Vector;

import core.Job;
import datacenter.Server;

public class SurvivorGenerator {
	
	private Vector<Survivor> survivorVec  = new Vector<Survivor>();
	
	public void generate(Job job, Server server) {
		for(Survivor survivor : this.survivorVec) {
			if(survivor.getJobId() == job.getJobId()) {
				setSurvivor(job, server, survivor);
				return;
			}
		}//end for
		Survivor survivor = new Survivor(job, server);
		this.survivorVec.add(survivor);
		this.setSurvivor(job, server, survivor);
	}
	
	private void setSurvivor(Job job, Server server, Survivor survivor) {
		job.setSurvivor(survivor.getServerId() == server.getServerId());
		survivor.updateCount();
		if(survivor.isEmpty())
			this.survivorVec.remove(survivor);
	}

}
