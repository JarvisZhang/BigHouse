package sawt;

import java.util.Random;
import java.util.Vector;

public class ReservoirSampling {

	public static Vector<Integer> generate(int k, int n) {
		Vector<Integer> result = new Vector<Integer>();
		int i = 0;
		Random random = new Random();
		for(i = 0; i < k; i++) {
			result.add(i, i);
		}
		while(i < n) {
			int swap = random.nextInt(i);
			if(swap < k) {
				result.set(swap, i);
			}
			i++;
		}
		return result;
	}
}
