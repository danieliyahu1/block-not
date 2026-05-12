package com.akatsuki.block_not.github;

public interface GitHubReleaseProperties {

    String getApiUrl();

    long getRequestTimeoutMs();

    String getGithubToken();
}
