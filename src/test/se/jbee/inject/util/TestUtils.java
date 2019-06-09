package se.jbee.inject.util;

public class TestUtils {

	private TestUtils() {
		throw new UnsupportedOperationException("util");
	}

	public static boolean wait50() {
		return await(50);
	}

	public static boolean wait20() {
		return await(20);
	}

	@SuppressWarnings("squid:S2925")
	public static boolean await(long millis) {
		try {
			Thread.sleep(millis);
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}
}
