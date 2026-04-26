package com.akatsuki.block_not.opencode;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class OpenCodeClient {

    private static final String ACCEPT_HEADER = "application/vnd.github+json";
    private static final String GITHUB_API_VERSION_HEADER = "2022-11-28";
    private static final String USER_AGENT = "block-not-bot";

    private final OpenCodeProperties properties;
    private final HttpClient httpClient;

    public OpenCodeClient(OpenCodeProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(properties.getRequestTimeoutMs()))
                .build();
    }

    public String fetchLatestRelease() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(properties.getApiUrl()))
                .timeout(Duration.ofMillis(properties.getRequestTimeoutMs()))
                .header("Accept", ACCEPT_HEADER)
                .header("X-GitHub-Api-Version", GITHUB_API_VERSION_HEADER)
                .header("User-Agent", USER_AGENT)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("GitHub API returned HTTP " + response.statusCode());
        }

        return response.body();
    }
}
