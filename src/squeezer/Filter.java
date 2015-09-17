package squeezer;

import java.util.LinkedList;
import java.util.Queue;

import core.Sim;

public class Filter {
	
	private Queue<Double> valueQueue;
	
	private long maxSize;

	public Filter (final long size) {
		this.maxSize = size;
		this.valueQueue = new LinkedList<Double>();
	}
	
	public long getMaxSize() {
		return this.maxSize;
	}
	
	public long getCurrentSize() {
		return this.valueQueue.size();
	}
	
	public double updateQueue(final double value) {
		if(this.getCurrentSize() > this.getMaxSize()) {
			Sim.fatalError("Service time window out of range: "
					+ this.valueQueue.size()
					+ " > "
					+ this.maxSize);
			return 0;
		}
		else if(this.valueQueue.size() == this.maxSize) {
			double oldestValue = this.valueQueue.poll();
			this.valueQueue.add(value);
			return oldestValue;
		}
		else {
			this.valueQueue.add(value);
			return 0;
		}
	}
}
