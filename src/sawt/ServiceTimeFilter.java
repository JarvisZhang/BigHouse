package sawt;

import core.Job;

public class ServiceTimeFilter extends Filter {
	
	private double movingAverage;
	
	private double sumService;
	
	private final double sla;
	
	private int warmingUpNum;
	
	private int currentNum;
	
	public ServiceTimeFilter(final long size, final double sla, final int warmingUpNum) {
		super(size);
		this.movingAverage = 0;
		this.sumService = 0;
		this.sla = sla;
		this.warmingUpNum = warmingUpNum;
		this.currentNum = 0;
	}

	public void addSample(final Job job) {
//		if(job.isSurvivor())
//			return;
		if(this.currentNum < this.warmingUpNum) {
			this.currentNum++;
		}
		double serviceTime = job.getSize();
		double oldestTime = this.updateQueue(serviceTime); 
		this.sumService = this.sumService - oldestTime + serviceTime;
		this.movingAverage = this.sumService / this.getCurrentSize();
	}
	
	public double getMovingAverage() {
		return this.movingAverage;
	}
	
	public boolean predictValid(Job job) {
		if(job.isSurvivor() || this.currentNum < this.warmingUpNum)
			return true;
		double waitingTime = job.getStartTime() - job.getArrivalTime();
		double predictResponse = waitingTime + this.movingAverage;
		return predictResponse <= this.sla;
	}
}
