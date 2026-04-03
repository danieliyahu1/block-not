package com.akatsuki.block_not.manga;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.stereotype.Component;

@Component
public class MangaTrackerClient {

    private final MangaTrackerProperties properties;
    private final HttpClient httpClient;

    public MangaTrackerClient(MangaTrackerProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(properties.getRequestTimeoutMs()))
                .build();
    }

    public String fetchPage() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(properties.getTargetUrl()))
                .timeout(Duration.ofMillis(properties.getRequestTimeoutMs()))
                .header("Accept", "text/html,application/xhtml+xml")
                .header("User-Agent", properties.getUserAgent())
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Manga site returned HTTP " + response.statusCode());
        }

        return response.body();
    }
}
