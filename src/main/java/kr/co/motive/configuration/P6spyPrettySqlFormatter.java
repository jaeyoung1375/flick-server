package kr.co.motive.configuration;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

import java.util.Locale;

import org.slf4j.MDC;

public class P6spyPrettySqlFormatter implements MessageFormattingStrategy {

    private static final String NEW_LINE = System.lineSeparator();
    private static final String indent = "        ";

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category,
            String prepared, String sql, String url) {

		if (sql == null || sql.trim().isEmpty() || !"statement".equalsIgnoreCase(category)) {
		return ""; // null 반환 금지
		}

		 // 키워드 기준 줄바꿈 + 대문자
	    String formattedSql = alignColumns(sql.trim());
	    String mapperId = MDC.get("P6SPY_CATEGORY");
	    if (mapperId == null) {
	        mapperId = category; // 기본 fallback
	    }


	    return NEW_LINE + NEW_LINE +"/* [" + mapperId + "] */\n" + formattedSql;
		}

    private String formatSql(String sql) {
        String trimmed = sql.trim();

        // DDL은 CREATE, ALTER, COMMENT 기준 줄바꿈
        if (trimmed.toLowerCase(Locale.ROOT).startsWith("create") ||
            trimmed.toLowerCase(Locale.ROOT).startsWith("alter") ||
            trimmed.toLowerCase(Locale.ROOT).startsWith("comment")) {

            return trimmed.replaceAll("(?i)\\b(CREATE|ALTER|COMMENT)\\b", "\n$1")
                          .toUpperCase(Locale.ROOT);
        }

        // SELECT, UPDATE, DELETE 등 일반 쿼리 키워드 기준 줄바꿈
        return trimmed.replaceAll("(?i)\\b(FROM|WHERE|GROUP BY|ORDER BY|JOIN|ON|AND|OR)\\b", "\n$1")
                      .toUpperCase(Locale.ROOT);
    }


    private boolean isStatement(String category) {
        return category != null && category.equalsIgnoreCase("statement");
    }

    private String alignColumns(String sql) {
        sql = sql.replace("\t", "    "); // 탭 → 공백
        String[] lines = sql.split("\n");
        int maxIdx = 0;

        for (String line : lines) {
            int idx = line.indexOf("/*");
            if (idx > maxIdx) maxIdx = idx;
        }

        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            int idx = line.indexOf("/*");
            if (idx >= 0) {
                int pad = maxIdx - idx;
                sb.append(line, 0, idx)
                  .append(" ".repeat(pad))
                  .append(line.substring(idx))
                  .append("\n");
            } else {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}