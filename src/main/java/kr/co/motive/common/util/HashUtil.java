package kr.co.motive.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.extern.slf4j.Slf4j;

/**
 * Hash 관련 유틸리티
 */
@Slf4j
public class HashUtil {

	private static String SHA_256_ALGORITHM = "SHA-256";

	/**
	 * 바이트를 Hex값으로 변환한다
	 *
	 * @param bytes
	 * @return
	 */
	private static String byteToHex(byte[] bytes) {

		StringBuilder builder = new StringBuilder();

		for(byte b : bytes) {
			builder.append(String.format("%02x", b));
		}

		return builder.toString();
	}

	/**
	 * SHA 256
	 * @param plain
	 * @return
	 */
	public static String sha256(String plain) {

		try {
			MessageDigest messageDigest = MessageDigest.getInstance(SHA_256_ALGORITHM);
			messageDigest.update(plain.getBytes(StandardCharsets.UTF_8));

			return byteToHex(messageDigest.digest());
		}catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}
}
