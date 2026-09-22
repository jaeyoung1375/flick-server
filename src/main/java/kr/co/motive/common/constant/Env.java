package kr.co.motive.common.constant;

public enum Env {

	/**
	 * 애플리케이션 실행 환경 정의 Enum
	 * 시스템 프로퍼티(예: -Dspring.profiles.active=dev) 또는 환경변수 값과 매핑하여 사용
	 */

		LOCAL("local", "로컬 개발 환경"),
		DEV("dev", "개발 서버"),
		STG("stg", "스테이징(검증) 서버"),
		PROD("prod", "운영 서버");

		/** 환경을 나타내는 코드 문자열 (profile 명 등과 매칭) */
		private final String code;

		/** 환경에 대한 설명 */
		private final String description;

		Env(String code, String description) {
			this.code = code;
			this.description = description;
		}

		public String getCode() {
			return code;
		}

		public String getDescription() {
			return description;
		}

		/**
		 * 코드 문자열로 EnvEnum 조회 (대소문자 무시)
		 * @param code 환경 코드 문자열 (예: "prod", "PROD")
		 * @return 일치하는 EnvEnum, 없으면 LOCAL 반환 (기본값)
		 */
		public static Env fromCode(String code) {
			if (code == null || code.isEmpty()) {
				return LOCAL;
			}
			for (Env value : values()) {
				if (value.code.equalsIgnoreCase(code.trim())) {
					return value;
				}
			}
			return LOCAL;
		}

		/**
		 * 운영(PROD) 환경 여부
		 * @return 운영 환경이면 true
		 */
		public boolean isProd() {
			return this == PROD;
		}

		/**
		 * 로컬 또는 개발(DEV) 환경 여부
		 * @return 로컬/개발 환경이면 true
		 */
		public boolean isDevLike() {
			return this == LOCAL || this == DEV;
		}

}
