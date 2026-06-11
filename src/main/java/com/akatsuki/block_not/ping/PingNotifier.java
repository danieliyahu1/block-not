package com.akatsuki.block_not.ping;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import com.akatsuki.block_not.telegram.TelegramClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PingNotifier {

	private static final Logger log = LoggerFactory.getLogger(PingNotifier.class);

	private final PingClient pingClient;
	private final PingProperties properties;
	private final TelegramClient telegramClient;
	private final AtomicReference<Boolean> lastState = new AtomicReference<>(null);

	public PingNotifier(PingClient pingClient, PingProperties properties, TelegramClient telegramClient) {
		this.pingClient = pingClient;
		this.properties = properties;
		this.telegramClient = telegramClient;
	}

	@Scheduled(fixedDelayString = "${blocknot.ping.poll-delay-ms:300000}", initialDelayString = "10000")
	public void ping() {
		try {
			boolean up = pingClient.isUp();
			Boolean previous = lastState.get();
			Instant now = Instant.now();

			if (previous == null || up != previous) {
				String state = up ? "UP \u2705" : "DOWN \u274C";
				String message = "Ping " + state + ": " + properties.getUrl();
				telegramClient.sendMessage(message);
				lastState.set(up);
				log.info("{} | Ping state changed to {}: {}", now, up ? "UP" : "DOWN", properties.getUrl());
			} else {
				log.debug("{} | Ping no change ({}): {}", now, up ? "UP" : "DOWN", properties.getUrl());
			}
		} catch (Exception ex) {
			log.error("{} | Ping failed: {}", Instant.now(), ex.getMessage());
		}
	}
}
