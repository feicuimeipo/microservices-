package com.nx.logger.utils;



import java.util.ArrayList;
import java.util.List;


public class LogExceptionUtils {
    public static List<StackTraceElement> getStackTraceElement(){
        List<StackTraceElement> list = new ArrayList<>();
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().toLowerCase().endsWith("test")
                    || element.getClassName().toLowerCase().endsWith("tests")
                    || element.getClassName().startsWith("Test")
                    || element.getClassName().indexOf(".test")>-1
            ){
                list.add(element);
            }else if(element.getClassName().startsWith("com.nx") &&
                    !element.getClassName().startsWith("com.nx.log.adapter") &&
                    !element.getClassName().startsWith("com.nx.logger.tracing")
            ){
                list.add(element);
            }
        }
      return list;
    }


    public String getRootCauseMessage(Throwable e) {
        Throwable throwable = org.apache.commons.lang3.exception.ExceptionUtils.getRootCause(e);


        String rcmessage = null;
        if (throwable!=null) {
            if (throwable.getCause()!=null) {
                rcmessage = throwable.getCause().getMessage();
            }
            rcmessage = (rcmessage == null) ? throwable.getMessage() : rcmessage;
            rcmessage = (rcmessage == null) ? e.getMessage() : rcmessage;
            rcmessage = (rcmessage == null) ? "NONE" : rcmessage;
        }
        return rcmessage;
    }

}
