package com.akatsuki.block_not.telegram;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TelegramLifecycleNotifierTests {

	@Test
	void sendsGoingDownMessageOnShutdown() throws Exception {
		TelegramClient telegramClient = mock(TelegramClient.class);
		TelegramLifecycleNotifier notifier = new TelegramLifecycleNotifier(telegramClient);

		notifier.onContextClosed();

		verify(telegramClient).sendMessage(contains("going down"));
	}
}
