
package com.nx.logger.adapter.slf4j;

import com.nx.logger.adapter.AdapterLogger;
import org.slf4j.Logger;



/**
 * @author Eduardo Macarron
 */
public class Slf4jLoggerImpl extends AdapterLogger {

	private final Logger log;

	public Slf4jLoggerImpl(Logger logger) {
		log = logger;
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
