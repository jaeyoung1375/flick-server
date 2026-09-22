package kr.co.motive.common.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * JSON 직렬화/역직렬화 유틸리티
 */
public class JsonUtil {

	private static final ObjectMapper MAPPER = createDefaultMapper();

	private static final ObjectMapper PRETTY = createDefaultMapper().enable(SerializationFeature.INDENT_OUTPUT);


	private static ObjectMapper createDefaultMapper() {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY);
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper;
	}

	/**
	 * JSON 문자열을 지정 타입 객체로 역직렬화
	 * @param <T> 대상타입
	 * @param json JSON 문자열
	 * @param type 대상 클래스
	 * @return 역직렬화된 객체
	 */
	public static <T> T fromJson(String json, Class<T> type) {
		try {
			return MAPPER.readValue(json, type);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * JSON 문자열을 지정 타입 객체로 역직렬화
	 * @param <T> 대상타입
	 * @param json JSON 문자열
	 * @param type 대상 클래스
	 * @return 역직렬화된 객체
	 */
	public static <T> T fromJson(String json, TypeReference<T> valueTypeRef) {
		try {
			return MAPPER.readValue(json, valueTypeRef);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 객체를 들여쓰기 포맷의 JSON 문자열로 직렬화
	 * <p>
	 * 로그 출력, 디버깅 등 가독성이 필요한 경우 사용
	 * </p>
	 * @param o
	 * @return
	 */
	public static String toPrettyJson(Object o) {
		try {
			return PRETTY.writeValueAsString(o);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * JSON 문자열을 {@link JsonNode} 트리 구조로 파싱
	 * <p>
	 * 구조가 정해지지 않은 JSON을 동적으로 탐색할 때 사용
	 * </p>
	 * @param json
	 * @return
	 */
	public static JsonNode readTree(String json) {
		try {
			return MAPPER.readTree(json);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * JSON 문자열에서 JSON Pointer 경로에 해당하는 값을 문자열로 추출
	 * @param json JSON 문자열
	 * @param jsonPointer JSON Pointer 경로 (예: "/user/address/city", RFC 6901 형식)
	 * @return 경로에 해당하는 값의 문자열 표현. 경로가 존재하지 않거나 값이 null이면 null 반환
	 */
	public static String getByPath(String json, String jsonPointer) {
		try {
			JsonNode node = MAPPER.readTree(json).at(jsonPointer);
			return node.isMissingNode() || node.isNull() ? null : node.asText();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
