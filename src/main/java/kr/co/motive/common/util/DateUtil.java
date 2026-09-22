package kr.co.motive.common.util;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * 날짜/시간 처리 유틸리티
 */
public class DateUtil {

	/**
	 * 현재 일시 반환
	 */
	public static LocalDateTime now() {
		return LocalDateTime.now();
	}

	/**
	 * 현재 일시 반환
	 * @param pattern
	 * @return
	 */
	public static String now(String pattern) {
		return DateUtil.format(now(), pattern);
	}

	/**
	 * {@link TemporalAccessor}를 지정 패턴으로 문자열 포맷팅
	 * @param t
	 * @param pattern
	 * @return
	 */
	public static String format(TemporalAccessor t, String pattern) {
		return DateTimeFormatter.ofPattern(pattern).format(t);
	}

	public static String format(long epochMillli, String pattern) {
		return DateTimeFormatter.ofPattern(pattern).format(fromEpochMilli(epochMillli, ZoneId.systemDefault()));
	}

	/**
	 * 지정 일수만큼 날짜 가산
	 * @param d
	 * @param days
	 * @return
	 */
	public static LocalDate plusDays(LocalDate d, long days) {
		return d.plusDays(days);
	}

	/**
	 * 두 날짜 간 일수 차이 계산
	 * <p>
	 * a가 b보다 크면 양수, 이후이면 음수 반환
	 * </p>
	 * @param a 시작날짜
	 * @param b 종료날짜
	 * @return 일수차이 (b-a)
	 */
	public static long betweenDays(LocalDate a, LocalDate b) {
		return Duration.between(a.atStartOfDay(), b.atStartOfDay()).toDays();
	}

	/**
	 * 에폭 밀리초를 {@link LocalDateTime}으로 변환
	 * @param epochMilli
	 * @param zone
	 * @return
	 */
	public static LocalDateTime fromEpochMilli(long epochMilli, ZoneId zone) {
		return Instant.ofEpochMilli(epochMilli).atZone(zone).toLocalDateTime();
	}

	/**
	 * 주말(토요일 또는 일요일) 여부 판정
	 * @param d 대상날짜
	 * @return 주말이면 True
	 */
	public static boolean isWeekend(LocalDate d) {
		DayOfWeek w = d.getDayOfWeek();
		return w == DayOfWeek.SATURDAY || w == DayOfWeek.SUNDAY;
	}

	/**
	 * 윤년 여부 판정
	 * @param year 대상 연도
	 * @return 윤년이면 True
	 */
	public static boolean isLeapYear(int year) {
		return Year.isLeap(year);
	}

}
