package com.nx.logger.adapter.log4j2;


import com.nx.logger.adapter.AdapterLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.AbstractLogger;


/**
 *
 * @author Eduardo Macarron
 */
public class Log4j2Impl extends AdapterLogger {

	private final com.nx.logger.adapter.Logger log;

	public Log4j2Impl(String clazz) {
		Logger logger = LogManager.getLogger(clazz);

		if (logger instanceof AbstractLogger) {
			log =   new Log4J2AdapterLoggerImpl((AbstractLogger) logger);
		} else {
			log =  new Log4j2LoggerImpl(logger);
		}
	}

	@Override
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	@Override
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	@Override
	public void error(String s, Throwable e) {
		log.error(s, e);
	}

	@Override
	public void error(String s) {
		log.error(s);
	}

	@Override
	public void debug(String s) {
		log.debug(s);
	}

	@Override
	public void trace(String s) {
		log.trace(s);
	}

	@Override
	public void warn(String s) {
		log.warn(s);
	}

	@Override
	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}

	@Override
	public void info(String s) {
		log.info(s);
	}

}
