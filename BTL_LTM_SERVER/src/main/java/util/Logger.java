package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }

    private static final String LOG_FORMAT = "[%s] %s - %s: %s";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static volatile LogLevel currentLevel = LogLevel.INFO;

    // Thay vì instance, dùng static method trực tiếp
    public static void setLogLevel(LogLevel level) {
        currentLevel = level;
    }

    public static void debug(String message) {
        log(LogLevel.DEBUG, message, null);
    }

    public static void info(String message) {
        log(LogLevel.INFO, message, null);
    }

    public static void warn(String message) {
        log(LogLevel.WARN, message, null);
    }

    public static void warn(String message, Throwable throwable) {
        log(LogLevel.WARN, message, throwable);
    }

    public static void error(String message) {
        log(LogLevel.ERROR, message, null);
    }

    public static void error(String message, Throwable throwable) {
        log(LogLevel.ERROR, message, throwable);
    }

    private static void log(LogLevel level, String message, Throwable throwable) {
        if (level.ordinal() < currentLevel.ordinal()) {
            return;
        }

        String timestamp = DATE_FORMAT.format(new Date());
        String logMessage = String.format(LOG_FORMAT, timestamp, level.name(), getCallerInfo(), message);

        if (level == LogLevel.ERROR || level == LogLevel.WARN) {
            System.err.println(logMessage);
        } else {
            System.out.println(logMessage);
        }

        if (throwable != null) {
            System.err.println("Exception details:");
            throwable.printStackTrace();
        }
    }

    private static String getCallerInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // stackTrace[0] = getStackTrace()
        // stackTrace[1] = getCallerInfo()
        // stackTrace[2] = log()
        // stackTrace[3] = debug/info/warn/error()
        // stackTrace[4] = actual caller
        if (stackTrace.length > 4) {
            StackTraceElement caller = stackTrace[4];
            return caller.getClassName() + "." + caller.getMethodName() + ":" + caller.getLineNumber();
        }
        return "Unknown";
    }
}
