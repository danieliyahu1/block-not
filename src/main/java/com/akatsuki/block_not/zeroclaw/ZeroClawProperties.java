package com.akatsuki.block_not.zeroclaw;

import com.akatsuki.block_not.github.GitHubReleaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blocknot.zeroclaw")
public class ZeroClawProperties implements GitHubReleaseProperties {

    private String apiUrl = "https://api.github.com/repos/zeroclaw-labs/zeroclaw/releases/latest";

    private long requestTimeoutMs = 10_000;

    private String githubToken = "";

    @Override
    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public long getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public void setRequestTimeoutMs(long requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
    }

    @Override
    public String getGithubToken() {
        return githubToken;
    }

    public void setGithubToken(String githubToken) {
        this.githubToken = githubToken;
    }
}
