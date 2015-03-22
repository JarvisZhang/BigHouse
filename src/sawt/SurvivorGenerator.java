package sawt;

import java.util.Vector;

import core.Job;
import datacenter.Server;

public class SurvivorGenerator {
	
	private static Vector<Survivor> survivorVec  = new Vector<Survivor>();
	
	public static void generate(Job job, Server server) {
		for(Survivor survivor : SurvivorGenerator.survivorVec) {
			if(survivor.getJobId() == job.getJobId()) {
				setSurvivor(job, server, survivor);
				return;
			}
		}//end for
		Survivor survivor = new Survivor(job, server);
		survivorVec.add(survivor);
		setSurvivor(job, server, survivor);
	}
	
	private static void setSurvivor(Job job, Server server, Survivor survivor) {
		job.setSurvivor(survivor.getServerId() == server.getServerId());
		survivor.updateCount();
		if(survivor.isEmpty())
			survivorVec.remove(survivor);
	}

}
