package kr.co.motive.configuration;

import com.p6spy.engine.spy.appender.Slf4JLogger;

public class CustomP6SpyLogger extends Slf4JLogger {

	public CustomP6SpyLogger() {
	    System.out.println("CustomP6SpyLogger initialized");
	}

	@Override
	public void logText(String text) {
	    if (text == null || text.trim().isEmpty()) return;

	    // Mapper ID 주석이 포함된 SQL만 출력
	    if (text.contains("/* [") && (text.contains("SELECT") || text.contains("INSERT") ||
	                                   text.contains("UPDATE") || text.contains("DELETE") ||
	                                   text.contains("CREATE") || text.contains("ALTER") ||
	                                   text.contains("DROP"))) {
	        super.logText(text);
	    }
	}
}