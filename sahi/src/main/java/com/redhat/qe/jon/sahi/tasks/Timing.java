package com.redhat.qe.jon.sahi.tasks;

/**
 * This class holds time constants, we should reference them every time we need to wait
 * @author lzoubek
 *
 */
public class Timing {

	public static double multiplier_default = 1.0;
	public static int TIME_5S = (int)(5000 * multiplier());
	public static int TIME_10S = (int)(10 * 1000 * multiplier());
	public static int TIME_30S = (int)(30 * 1000 * multiplier());
	public static int TIME_1M = (int)(60 * 1000 * multiplier());
    public static int TIME_1S = (int)(1000 * multiplier());
	public static int WAIT_TIME = TIME_5S;
	public static int REPEAT = 10;
	public static int DISCOVERY_WAIT = TIME_30S;
	
	public static String toString(int time) {
		return String.valueOf((time/1000))+"s";
	}
	public static double multiplier() {
	    try {
		return Double.parseDouble(System.getProperty("jon.sahi.time.multiplier", "1.0"));
	    }
	    catch (Exception ex ){
		return multiplier_default;
	    }
	}
}
