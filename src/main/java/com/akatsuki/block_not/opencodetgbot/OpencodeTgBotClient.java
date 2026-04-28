package com.akatsuki.block_not.opencodetgbot;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class OpencodeTgBotClient {

    private static final String ACCEPT_HEADER = "application/vnd.github+json";
    private static final String GITHUB_API_VERSION_HEADER = "2022-11-28";
    private static final String USER_AGENT = "block-not-bot";

    private final OpencodeTgBotProperties properties;
    private final HttpClient httpClient;

    public OpencodeTgBotClient(OpencodeTgBotProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(properties.getRequestTimeoutMs()))
                .build();
    }

    public String fetchLatestRelease() throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(properties.getApiUrl()))
                .timeout(Duration.ofMillis(properties.getRequestTimeoutMs()))
                .header("Accept", ACCEPT_HEADER)
                .header("X-GitHub-Api-Version", GITHUB_API_VERSION_HEADER)
                .header("User-Agent", USER_AGENT);

        if (!properties.getGithubToken().isBlank()) {
            requestBuilder.header("Authorization", "Bearer " + properties.getGithubToken());
        }

        HttpResponse<String> response = httpClient.send(requestBuilder.GET().build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("GitHub API returned HTTP " + response.statusCode());
        }

        return response.body();
    }
}
