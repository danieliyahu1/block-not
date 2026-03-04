package com.akatsuki.block_not.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TelegramLifecycleNotifier {

	private static final Logger log = LoggerFactory.getLogger(TelegramLifecycleNotifier.class);

	private final TelegramClient telegramClient;

	public TelegramLifecycleNotifier(TelegramClient telegramClient) {
		this.telegramClient = telegramClient;
	}

	@EventListener(ContextClosedEvent.class)
	public void onContextClosed() {
		// Best-effort: do not prevent or delay shutdown if Telegram is misconfigured or unreachable.
		try {
			telegramClient.sendMessage("🤖 Bot is going down");
		} catch (Exception ex) {
			log.warn("Failed to send Telegram shutdown notification: {}", ex.getMessage());
		}
	}
}
