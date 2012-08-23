package regressiontest.util;

import java.util.Random;

public class Util {
	public static String getRandomString(int maxLength, boolean fixedTextLength) {
		
		Random r = new Random();

		int length;
		if (!fixedTextLength) {
			length = (int) ((r.nextDouble() * (double) maxLength) + 1.0);
		} else {
			length = maxLength;
		}
		String random = "";
		for (int i = 0; i < length; i++) {
			random += getRandomChar();
		}

		// String token = Long.toString(Math.abs(r.nextLong()), 32);
		return random;
	}

	private static char getRandomChar() {
		Random r = new Random();
		int i = (int) ((r.nextDouble() * 26.0) + 97.0); // range for a-z
		return (char) i;
	}

	public static int getRandomInt() {
		Random r = new Random();
		return r.nextInt();
	}

	public static double getRandomDouble() {
		Random r = new Random();
		double d = r.nextDouble();
		//DecimalFormat df = new DecimalFormat("#.##########");
		//double d2 = Double.parseDouble(df.format(d));
		return d;
	}

	public static boolean getRandomBoolean() {
		Random r = new Random();
		return r.nextBoolean();
	}

}
