package xyz.ravensharp.gc.utils;

import java.util.ArrayList;
import java.util.Iterator;

public class Counter {

	public enum Unit {
		MIN, SEC, MILLISEC, NANOSEC
	}

	ArrayList<Long> counts = new ArrayList<>();
	protected static long threshold;

	public Counter(long threshold) {
		this.threshold = threshold;
	}

	public Counter(long value, Unit unit) {
		switch (unit) {
		case MIN:
			threshold = value * 60L * 1_000_000_000L;
			break;
		case SEC:
			threshold = value * 1_000_000_000L;
			break;
		case MILLISEC:
			threshold = value * 1_000_000L;
			break;
		case NANOSEC:
			threshold = value;
			break;
		}
	}

	public long get() {
		long current = System.nanoTime();
		Iterator<Long> it = counts.iterator();

		while (it.hasNext()) {
			Long count = it.next();
			if (current - count >= threshold) {
				it.remove();
			}
		}

		return counts.size();
	}

	public long count() {
		this.counts.add(System.nanoTime());
		return get();
	}

}