package io.github.carjooj.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jAppLogger implements AppLogger {

    private final Logger logger;


    private Slf4jAppLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public static AppLogger getLogger(Class<?> clazz) {
        return new Slf4jAppLogger(clazz);
    }

    @Override
    public void info(String format, Object... args) {
        logger.info(format, args);
    }

    @Override
    public void warn(String format, Object... args) {
        logger.warn(format, args);
    }

    @Override
    public void error(String format, Object... args) {
        logger.error(format, args);
    }

    @Override
    public void error(String message, Exception exception) {
        logger.error(message, exception);
    }
}
