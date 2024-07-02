package com.nx.logger.storage;


import org.springframework.core.Ordered;

public interface ILogStorage extends Comparable<ILogStorage> {

	void writeLog(NxLoggerModel nxLog);

	void writeLog(String jsonMessage);

	default int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	@Override
	default int compareTo(ILogStorage o) {
		return Integer.compare(this.getOrder(), o.getOrder());
	}


}
