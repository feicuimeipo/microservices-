package com.nx.logger.adapter;



/**
 * @author nianxiaoling
 */
public interface Logger {

  boolean isInfoEnabled();

  boolean isDebugEnabled();

  boolean isTraceEnabled();

  void error(String s);
  
  void error(String s, Throwable e);

  void debug(String s);
  
  void debug(String format, Object... arguments);

  void trace(String s);
  
  void trace(String format, Object... arguments);

  void warn(String s);
  
  void warn(String format, Object... arguments);
  
  void info(String s);
  
  void info(String format, Object... arguments);

}
