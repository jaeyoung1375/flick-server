package kr.co.motive.common.constant;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 문자 인코딩(Charset) 정의 Enum
 */
public enum EncodingEnum {

	UTF_8("UTF-8", StandardCharsets.UTF_8),
	EUC_KR("EUC-KR", Charset.forName("EUC-KR")),
	MS949("MS949", Charset.forName("MS949")),
	ISO_8859_1("ISO-8859-1", StandardCharsets.ISO_8859_1),
	US_ASCII("US-ASCII", StandardCharsets.US_ASCII);

	/** 인코딩 문자열 (예: "UTF-8") */
	private final String code;

	/** java.nio.charset.Charset 객체 */
	private final Charset charset;

	EncodingEnum(String code, Charset charset) {
		this.code = code;
		this.charset = charset;
	}

	public String getCode() {
		return code;
	}

	public Charset getCharset() {
		return charset;
	}

	/**
	 * 코드 문자열로 EncodingEnum 조회 (대소문자, 하이픈/언더스코어 무시)
	 * @param code 인코딩 코드 문자열 (예: "utf-8", "UTF8")
	 * @return 일치하는 EncodingEnum, 없으면 UTF_8 반환 (기본값)
	 */
	public static EncodingEnum fromCode(String code) {
		if (code == null || code.isEmpty()) {
			return UTF_8;
		}
		String normalized = code.replace("-", "").replace("_", "").toUpperCase();
		for (EncodingEnum value : values()) {
			String target = value.code.replace("-", "").replace("_", "").toUpperCase();
			if (target.equals(normalized)) {
				return value;
			}
		}
		return UTF_8;
	}
}