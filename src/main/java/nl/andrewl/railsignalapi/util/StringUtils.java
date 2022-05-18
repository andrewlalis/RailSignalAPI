package nl.andrewl.railsignalapi.util;

import java.security.SecureRandom;
import java.util.Random;

public final class StringUtils {
	private StringUtils() {}

	public static final String ALPHA_NUM = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final Random random = new SecureRandom();

	public static String randomString(int length, String alphabet) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
		}
		return sb.toString();
	}
}
