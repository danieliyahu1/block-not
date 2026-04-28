package com.akatsuki.block_not.opencodetgbot;

import com.akatsuki.block_not.telegram.TelegramClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class OpencodeTgBotNotifier {

    private static final Logger log = LoggerFactory.getLogger(OpencodeTgBotNotifier.class);
    private static final String RELEASES_URL = "https://github.com/grinev/opencode-telegram-bot/releases/latest";

    private final OpencodeTgBotClient client;
    private final OpencodeTgBotVersionParser versionParser;
    private final TelegramClient telegramClient;
    private final AtomicReference<String> lastNotifiedVersion;

    public OpencodeTgBotNotifier(OpencodeTgBotClient client, OpencodeTgBotVersionParser versionParser, TelegramClient telegramClient) {
        this.client = client;
        this.versionParser = versionParser;
        this.telegramClient = telegramClient;
        this.lastNotifiedVersion = new AtomicReference<>(null);
    }

    @Scheduled(fixedDelayString = "${blocknot.opencodetgbot.poll-delay-ms:3600000}", initialDelayString = "5000")
    public void checkForNewVersion() {
        try {
            String json = client.fetchLatestRelease();
            String latestVersion = versionParser.parseVersion(json);
            Instant now = Instant.now();

            if (latestVersion == null) {
                log.warn("{} | Could not parse latest opencode-telegram-bot version from response", now);
                return;
            }

            String lastVersion = lastNotifiedVersion.get();
            log.info("{} | Latest opencode-telegram-bot version: {} — Last notified: {}", now, latestVersion, lastVersion);

            if (!latestVersion.equals(lastVersion)) {
                notifyNewVersion(latestVersion, now);
            }

        } catch (Exception ex) {
            log.error("{} | Failed to check for new opencode-telegram-bot version: {}", Instant.now(), ex.getMessage());
        }
    }

    private void notifyNewVersion(String version, Instant now) throws IOException, InterruptedException {
        String message = "New opencode-telegram-bot version available: " + version + "\n" + RELEASES_URL;
        telegramClient.sendMessage(message);
        lastNotifiedVersion.set(version);
        log.info("{} | Notified about new opencode-telegram-bot version {}", now, version);
    }
}
