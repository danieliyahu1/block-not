package com.akatsuki.block_not.ping;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.stereotype.Component;

@Component
public class PingClient {

	private final PingProperties properties;
	private final HttpClient httpClient;

	public PingClient(PingProperties properties) {
		this.properties = properties;
		this.httpClient = HttpClient.newBuilder()
				.connectTimeout(Duration.ofMillis(properties.getRequestTimeoutMs()))
				.build();
	}

	public boolean isUp() {
		try {
			HttpRequest request = HttpRequest.newBuilder(URI.create(properties.getUrl()))
					.timeout(Duration.ofMillis(properties.getRequestTimeoutMs()))
					.GET()
					.build();

			HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
			return response.statusCode() >= 200 && response.statusCode() < 300;
		} catch (IOException e) {
			return false;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}
}
