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
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("GitHub API returned HTTP " + response.statusCode());
        }

        return response.body();
    }
}
