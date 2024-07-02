package com.nx.logger.adapter;


import com.nx.logger.adapter.slf4j.Slf4jImpl;
import com.nx.logger.adapter.stdout.StdOutImpl;
import com.nx.logger.adapter.log4j2.Log4j2Impl;
import java.lang.reflect.Constructor;


/**
 * nianxiaoling
 */
public final class LogFactory {

  /**
   * Marker to be used by logging implementations that support markers.
   */
  public static final String MARKER = "nx-logger";

  private static Constructor<? extends Logger> logConstructor;

  static {
    tryImplementation(LogFactory::useSlf4jLogging);
    tryImplementation(LogFactory::useLog4J2Logging);
  }

  private LogFactory() {
    // disable construction
  }

  public static Logger getLog(Class<?> clazz) {
    return getLog(clazz.getName());
  }

  public static Logger getLog(String logger) {
    try {
      return logConstructor.newInstance(logger);
    } catch (Throwable t) {
      throw new RuntimeException("Error creating logger for logger " + logger + ".  Cause: " + t);
    }
  }

  public static synchronized void useCustomLogging(Class<? extends Logger> clazz) {
    setImplementation(clazz);
  }

  public static synchronized void useSlf4jLogging() {
    setImplementation(Slf4jImpl.class);
  }


  public static synchronized void useLog4J2Logging() {
    setImplementation(Log4j2Impl.class);
  }


  public static synchronized void useStdOutLogging() {
    setImplementation(StdOutImpl.class);
  }

  private static void tryImplementation(Runnable runnable) {
    if (logConstructor == null) {
      try {
        runnable.run();
      } catch (Throwable t) {
        // ignore
      }
    }
  }

  private static void setImplementation(Class<? extends Logger> implClass) {
    try {
      Constructor<? extends Logger> candidate = implClass.getConstructor(String.class);
      Logger log = candidate.newInstance(LogFactory.class.getName());
      if (log.isDebugEnabled()) {
        log.debug("Logging initialized using '" + implClass + "' adapter.");
      }
      logConstructor = candidate;
    } catch (Throwable t) {
      throw new RuntimeException("Error setting Log implementation.  Cause: " + t);
    }
  }

}
