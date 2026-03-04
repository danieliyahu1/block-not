package com.akatsuki.block_not.price;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.akatsuki.block_not.telegram.TelegramClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TrackedPricePrinter {

	private static final Logger log = LoggerFactory.getLogger(TrackedPricePrinter.class);

	private final CoinGeckoClient coinGeckoClient;
	private final TrackerProperties properties;
	private final TelegramClient telegramClient;

	public TrackedPricePrinter(CoinGeckoClient coinGeckoClient, TrackerProperties properties, TelegramClient telegramClient) {
		this.coinGeckoClient = coinGeckoClient;
		this.properties = properties;
		this.telegramClient = telegramClient;
	}

	@Scheduled(fixedDelayString = "${blocknot.tracker.poll-delay-ms:10000}", initialDelayString = "0")
	public void printPrices() {
		try {
			Map<String, BigDecimal> prices = coinGeckoClient.fetchTrackedPrices();
			String currency = properties.getVsCurrency();
			Instant now = Instant.now();
			List<String> coinIds = properties.getCoinIds() == null
					? List.of()
					: properties.getCoinIds().stream()
							.filter(Objects::nonNull)
							.map(String::trim)
							.filter(id -> !id.isBlank())
							.toList();

		if (prices.isEmpty()) {
			log.info("{} | No prices returned (ids={})", now, coinIds);
			return;
		}

		for (String coinId : coinIds) {
			BigDecimal price = prices.get(coinId);
			if (price == null) {
				log.info("{} | {} => (missing) {}", now, coinId, currency);
				continue;
			}

			// Print to console
			log.info("{} | {} => {} {}", now, coinId, price, currency);

			// Check if price surpasses threshold and send to Telegram
			BigDecimal threshold = properties.getPriceThresholds().get(coinId);
			if (threshold != null && price.compareTo(threshold) > 0) {
				try {
					String telegramMessage = coinId + ": " + price + " " + currency + " | " + now;
					telegramClient.sendMessage(telegramMessage);
					log.info("✅ Sent Telegram alert for {} (price {} > threshold {})", coinId, price, threshold);
				} catch (Exception ex) {
					// Exception will propagate if max consecutive failures reached
					log.error("Failed to send Telegram message for {}: {}", coinId, ex.getMessage());
				}
			}
		}
		} catch (Exception ex) {
			log.error("{} | Failed to fetch prices: {}", Instant.now(), ex.getMessage());
		}
	}
}
