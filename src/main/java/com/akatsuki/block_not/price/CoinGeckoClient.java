package com.akatsuki.block_not.price;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

@Component
public class CoinGeckoClient {

	private final TrackerProperties properties;
	private final HttpClient httpClient;
	private final CoinGeckoPriceParser parser;
	private final String apiKey;

	public CoinGeckoClient(TrackerProperties properties) {
		this.properties = properties;
		String configuredApiKey = properties.getApiKey();
		if (configuredApiKey == null || configuredApiKey.isBlank()) {
			throw new IllegalStateException(
					"Missing CoinGecko API key. Set COINGECKO_DEMO_API_KEY (mapped to blocknot.tracker.api-key)."
			);
		}
		this.apiKey = configuredApiKey.trim();
		this.httpClient = HttpClient.newBuilder()
				.connectTimeout(Duration.ofMillis(properties.getRequestTimeoutMs()))
				.build();
		this.parser = new CoinGeckoPriceParser();
	}

	public Map<String, BigDecimal> fetchTrackedPrices() throws IOException, InterruptedException {
		List<String> coinIds = normalizeCoinIds(properties.getCoinIds());
		if (coinIds.isEmpty()) {
			return Collections.emptyMap();
		}
		String vsCurrency = properties.getVsCurrency().toLowerCase(Locale.ROOT);
		URI uri = buildSimplePriceUri(coinIds, vsCurrency);

		HttpRequest request = HttpRequest.newBuilder(uri)
				.timeout(Duration.ofMillis(properties.getRequestTimeoutMs()))
				.header("Accept", "application/json")
				.GET()
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() < 200 || response.statusCode() >= 300) {
			String body = response.body();
			if (body == null) {
				body = "";
			}
			throw new IOException("CoinGecko returned HTTP " + response.statusCode() + ": " + body);
		}

		return parser.parseSimplePriceResponse(response.body(), coinIds, vsCurrency);
	}

	private static List<String> normalizeCoinIds(List<String> coinIds) {
		if (coinIds == null) {
			return List.of();
		}
		return coinIds.stream()
				.filter(Objects::nonNull)
				.map(String::trim)
				.filter(id -> !id.isBlank())
				.toList();
	}

	private URI buildSimplePriceUri(List<String> coinIds, String vsCurrency) {
		String base = properties.getApiBaseUrl().toString();
		if (base.endsWith("/")) {
			base = base.substring(0, base.length() - 1);
		}

		String idsParam = URLEncoder.encode(String.join(",", coinIds), StandardCharsets.UTF_8);
		String currencyParam = URLEncoder.encode(vsCurrency, StandardCharsets.UTF_8);
		String apiKeyParam = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

		return URI.create(base + "/simple/price?ids=" + idsParam + "&vs_currencies=" + currencyParam
				+ "&x_cg_demo_api_key=" + apiKeyParam);
	}
}
