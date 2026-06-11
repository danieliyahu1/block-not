package com.akatsuki.block_not.ping;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blocknot.ping")
public class PingProperties {

	private String url;

	private long requestTimeoutMs = 10_000;

	private long pollDelayMs = 300_000;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getRequestTimeoutMs() {
		return requestTimeoutMs;
	}

	public void setRequestTimeoutMs(long requestTimeoutMs) {
		this.requestTimeoutMs = requestTimeoutMs;
	}

	public long getPollDelayMs() {
		return pollDelayMs;
	}

	public void setPollDelayMs(long pollDelayMs) {
		this.pollDelayMs = pollDelayMs;
	}
}
