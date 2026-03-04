package com.akatsuki.block_not.telegram;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blocknot.telegram")
public class TelegramProperties {

	/**
	 * Telegram Bot API token (from @BotFather).
	 */
	private String botToken;

	/**
	 * Target chat ID to send messages to.
	 */
	private String chatId;

	/**
	 * Request timeout in milliseconds for Telegram API calls.
	 */
	private long requestTimeoutMs = 5_000;

	/**
	 * Maximum consecutive failures before crashing the application.
	 */
	private int maxConsecutiveFailures = 3;

	public String getBotToken() {
		return botToken;
	}

	public void setBotToken(String botToken) {
		this.botToken = botToken;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public long getRequestTimeoutMs() {
		return requestTimeoutMs;
	}

	public void setRequestTimeoutMs(long requestTimeoutMs) {
		this.requestTimeoutMs = requestTimeoutMs;
	}

	public int getMaxConsecutiveFailures() {
		return maxConsecutiveFailures;
	}

	public void setMaxConsecutiveFailures(int maxConsecutiveFailures) {
		this.maxConsecutiveFailures = maxConsecutiveFailures;
	}
}

