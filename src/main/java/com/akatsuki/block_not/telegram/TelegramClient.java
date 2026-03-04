package com.akatsuki.block_not.telegram;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TelegramClient {

	private static final Logger log = LoggerFactory.getLogger(TelegramClient.class);

	private final TelegramProperties properties;
	private final HttpClient httpClient;
	private final AtomicInteger consecutiveFailures;

	public TelegramClient(TelegramProperties properties) {
		this.properties = properties;
		this.httpClient = HttpClient.newBuilder()
				.connectTimeout(Duration.ofMillis(properties.getRequestTimeoutMs()))
				.build();
		this.consecutiveFailures = new AtomicInteger(0);
	}

	@PostConstruct
	public void validateConnection() {
		try {
			sendMessage("🤖 Bot started");
			log.info("✅ Telegram bot connection validated successfully");
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to validate Telegram bot connection on startup: " + ex.getMessage(), ex);
		}
	}

	public void sendMessage(String text) throws IOException, InterruptedException {
		if (properties.getBotToken() == null || properties.getBotToken().isBlank()) {
			throw new IllegalStateException("Telegram bot token is not configured");
		}
		if (properties.getChatId() == null || properties.getChatId().isBlank()) {
			throw new IllegalStateException("Telegram chat ID is not configured");
		}

		String apiUrl = "https://api.telegram.org/bot" + properties.getBotToken() + "/sendMessage";

		// Build JSON request body manually (no external JSON library needed)
		String jsonBody = buildSendMessageJson(properties.getChatId(), text);

		HttpRequest request = HttpRequest.newBuilder(URI.create(apiUrl))
				.timeout(Duration.ofMillis(properties.getRequestTimeoutMs()))
				.header("Content-Type", "application/json; charset=utf-8")
				.POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
				.build();

		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() < 200 || response.statusCode() >= 300) {
				handleFailure("Telegram API returned HTTP " + response.statusCode() + ": " + response.body());
				return;
			}

			// Success - reset failure counter
			consecutiveFailures.set(0);

		} catch (IOException | InterruptedException ex) {
			handleFailure("Failed to send Telegram message: " + ex.getMessage());
			throw ex;
		}
	}

	private void handleFailure(String errorMessage) {
		int failures = consecutiveFailures.incrementAndGet();
		log.error("⚠️ Telegram send failure #{}: {}", failures, errorMessage);

		if (failures >= properties.getMaxConsecutiveFailures()) {
			throw new IllegalStateException(
					"Telegram bot has failed " + failures + " consecutive times. Crashing application.");
		}
	}

	private String buildSendMessageJson(String chatId, String text) {
		// Escape JSON strings manually
		String escapedText = escapeJson(text);
		String escapedChatId = escapeJson(chatId);

		return "{\"chat_id\":\"" + escapedChatId + "\",\"text\":\"" + escapedText + "\"}";
	}

	private String escapeJson(String value) {
		if (value == null) {
			return "";
		}
		return value.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\t", "\\t");
	}
}

