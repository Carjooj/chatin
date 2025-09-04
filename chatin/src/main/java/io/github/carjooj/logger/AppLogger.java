package io.github.carjooj.logger;

public interface AppLogger {

    void info(String format, Object... args);

    void warn(String format, Object... args);

    void error(String format, Object... args);

    void error(String message, Exception exception);
}
