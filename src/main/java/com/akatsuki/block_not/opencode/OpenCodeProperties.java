package com.akatsuki.block_not.opencode;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blocknot.opencode")
public class OpenCodeProperties {

    private String apiUrl = "https://api.github.com/repos/anomalyco/opencode/releases/latest";

    private long pollDelayMs = 3_600_000;

    private long requestTimeoutMs = 10_000;

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public long getPollDelayMs() {
        return pollDelayMs;
    }

    public void setPollDelayMs(long pollDelayMs) {
        this.pollDelayMs = pollDelayMs;
    }

    public long getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public void setRequestTimeoutMs(long requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
    }
}
