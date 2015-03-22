package sawt;

import core.Job;

public class ServiceTimeFilter extends Filter {
	
	private double movingAverage;
	
	private double sumService;
	
	private double sla;
	
	public ServiceTimeFilter(final long size, final double sla) {
		super(size);
		this.movingAverage = 0;
		this.sumService = 0;
		this.sla = sla;
	}

	public void addSample(final double serviceTime) {
		double oldestTime = this.updateQueue(serviceTime); 
		this.sumService = this.sumService - oldestTime + serviceTime;
		this.movingAverage = this.sumService / this.getCurrentSize();
	}
	
	public double getMovingAverage() {
		return this.movingAverage;
	}
	
	public boolean predictValid(Job job) {
		if(job.isSurvivor())
			return true;
		double waitingTime = job.getStartTime() - job.getArrivalTime();
		double predictResponse = waitingTime + this.movingAverage;
		return predictResponse <= this.sla;
	}
}
