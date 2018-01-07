package com.sunyard.insurance.filenet.ce.depend;

public class TimeCounter {

	protected long start = 0L;
	private TimeCounter() {
		super();
	}
	
	public static TimeCounter record() {
		TimeCounter tc = new TimeCounter();
		tc.start = System.currentTimeMillis();
		return tc;
	}
	
	public long exhaust() {
		long end = System.currentTimeMillis();
		long exhaust = end - start;
		return exhaust;
	}
	
}
