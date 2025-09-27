package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    
    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
    
    private static final String LOG_FORMAT = "[%s] %s - %s: %s";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Logger instance;
    private LogLevel currentLevel = LogLevel.INFO;
    
    private Logger() {}
    
    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }
    
    public void setLogLevel(LogLevel level) {
        this.currentLevel = level;
    }
    
    public void debug(String message) {
        log(LogLevel.DEBUG, message, null);
    }
    
    public void info(String message) {
        log(LogLevel.INFO, message, null);
    }
    
    public void warn(String message) {
        log(LogLevel.WARN, message, null);
    }
    
    public void warn(String message, Throwable throwable) {
        log(LogLevel.WARN, message, throwable);
    }
    
    public void error(String message) {
        log(LogLevel.ERROR, message, null);
    }
    
    public void error(String message, Throwable throwable) {
        log(LogLevel.ERROR, message, throwable);
    }
    
    private void log(LogLevel level, String message, Throwable throwable) {
        if (level.ordinal() < currentLevel.ordinal()) {
            return;
        }
        
        String timestamp = DATE_FORMAT.format(new Date());
        String logMessage = String.format(LOG_FORMAT, timestamp, level.name(), getCallerInfo(), message);
        
        // Ghi ra console
        if (level == LogLevel.ERROR || level == LogLevel.WARN) {
            System.err.println(logMessage);
        } else {
            System.out.println(logMessage);
        }
        
        // In stack trace nếu có exception
        if (throwable != null) {
            System.err.println("Exception details:");
            throwable.printStackTrace();
        }
    }
    
    private String getCallerInfo() {
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