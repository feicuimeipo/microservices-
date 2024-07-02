package com.nx.storage.adapter.provider;

import com.nx.storage.core.client.s3.S3FileClientConfig;
import com.nx.storage.core.enums.StoProviderEnum;
import lombok.Data;


@Data
public class StoProviderConfig extends S3FileClientConfig{
	private String appId; //qcloud有用
	private StoProviderEnum provider;
	private LogHandle logHandle;
	private long maxAllowdSingleFileSize = 500 * 1024L * 1024L;
	private int maxConnections = 100;  //max-connections
	private boolean auth=true;
	private String regionName;


	public String getSecretKey() {
		return this.getAccessSecret();
	}


	@Data
	public static class LogHandle{
		//loghandler.enabled
		private boolean enabled = true;
		private int threads=1;
		private int queueSize=1000;
		private String url;
	}

}
