package com.akatsuki.block_not.price;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blocknot.tracker")
public class TrackerProperties {

	/**
	 * CoinGecko "ids" (comma-separated in properties), e.g. cardano,bitcoin,ethereum.
	 */
	private List<String> coinIds = new ArrayList<>(List.of("cardano"));

	/**
	 * CoinGecko vs_currency, e.g. usd, eur.
	 */
	private String vsCurrency = "usd";

	/**
	 * Price thresholds per coin. Map of coin ID to threshold price.
	 * Telegram notification sent only when price surpasses threshold.
	 */
	private Map<String, BigDecimal> priceThresholds = new HashMap<>();

	/**
	 * Polling interval in milliseconds.
	 */
	private long pollDelayMs = 10_000;

	/**
	 * Request timeout in milliseconds.
	 */
	private long requestTimeoutMs = 3_000;

	/**
	 * CoinGecko API base URL.
	 */
	private URI apiBaseUrl = URI.create("https://api.coingecko.com/api/v3");

	public List<String> getCoinIds() {
		return coinIds;
	}

	public void setCoinIds(List<String> coinIds) {
		this.coinIds = coinIds;
	}

	public String getVsCurrency() {
		return vsCurrency;
	}

	public void setVsCurrency(String vsCurrency) {
		this.vsCurrency = vsCurrency;
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

	public URI getApiBaseUrl() {
		return apiBaseUrl;
	}

	public void setApiBaseUrl(URI apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;
	}

	public Map<String, BigDecimal> getPriceThresholds() {
		return priceThresholds;
	}

	public void setPriceThresholds(Map<String, BigDecimal> priceThresholds) {
		this.priceThresholds = priceThresholds;
	}
}
