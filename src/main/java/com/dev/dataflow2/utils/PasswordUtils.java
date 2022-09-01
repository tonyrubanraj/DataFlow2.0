/**
 * 
 */
package com.dev.dataflow2.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * @author tonyr
 *
 */
public class PasswordUtils {

	private static final Random RANDOM = new SecureRandom();
	private static final int ITERATIONS = 1000;
	private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final int KEY_LENGTH = 128;
	private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

	public static String getSalt(int length) {
		StringBuilder returnValue = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			returnValue.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
		}
		return new String(returnValue);
	}

	public static String encryptPassword(String password, byte[] salt) {
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
		Arrays.fill(password.toCharArray(), Character.MIN_VALUE);
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
			byte[] hash = factory.generateSecret(spec).getEncoded();
			return Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new AssertionError("Error while hashing the password: " + e.getMessage(), e);
		} finally {
			spec.clearPassword();
		}
	}

	public static boolean verifyPassword(String providedPassword, String securedPassword, byte[] salt) {
		String encodedPassword = encryptPassword(providedPassword, salt);
		int index = 0;
		while (index < encodedPassword.length() && index < securedPassword.length()) {
			if (encodedPassword.charAt(index) != securedPassword.charAt(index)) {
				return false;
			}
			index++;
		}
		return encodedPassword.length() == securedPassword.length() ? true : false;
	}
}
