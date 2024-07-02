package com.nx.logger.adapter.stdout;


import com.nx.logger.adapter.AdapterLogger;

/**
 * @author Clinton Begin
 */
public class StdOutImpl extends AdapterLogger {

	public StdOutImpl(String clazz) {
		// Do Nothing
	}

	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	@Override
	public boolean isTraceEnabled() {
		return true;
	}

	@Override
	public void error(String s, Throwable e) {
		System.err.println(s);
		e.printStackTrace(System.err);
	}

	@Override
	public void error(String s) {
		System.err.println(s);
	}

	@Override
	public void debug(String s) {
		System.out.println(s);
	}

	@Override
	public void trace(String s) {
		System.out.println(s);
	}

	@Override
	public void warn(String s) {
		System.out.println(s);
	}

	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	@Override
	public void info(String s) {
		System.out.println(s);
	}
}
