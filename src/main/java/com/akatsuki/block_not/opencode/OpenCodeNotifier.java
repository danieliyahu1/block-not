package com.akatsuki.block_not.opencode;

import com.akatsuki.block_not.telegram.TelegramClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class OpenCodeNotifier {

    private static final Logger log = LoggerFactory.getLogger(OpenCodeNotifier.class);

    private final OpenCodeClient openCodeClient;
    private final OpenCodeVersionParser versionParser;
    private final TelegramClient telegramClient;
    private final AtomicReference<String> lastNotifiedVersion;

    public OpenCodeNotifier(OpenCodeClient openCodeClient, OpenCodeVersionParser versionParser, TelegramClient telegramClient) {
        this.openCodeClient = openCodeClient;
        this.versionParser = versionParser;
        this.telegramClient = telegramClient;
        this.lastNotifiedVersion = new AtomicReference<>(null);
    }

    @Scheduled(fixedDelayString = "${blocknot.opencode.poll-delay-ms:3600000}", initialDelayString = "5000")
    public void checkForNewVersion() {
        try {
            String json = openCodeClient.fetchLatestRelease();
            String latestVersion = versionParser.parseVersion(json);

            if (latestVersion == null) {
                log.warn("{} | Could not parse latest OpenCode version from response", Instant.now());
                return;
            }

            String lastVersion = lastNotifiedVersion.get();

            log.info("{} | Latest OpenCode version: {} — Last notified: {}", Instant.now(), latestVersion, lastVersion);

            if (lastVersion == null) {
                // First run — store the current version silently, no notification
                lastNotifiedVersion.set(latestVersion);
                log.info("{} | OpenCode version tracker initialized at {}", Instant.now(), latestVersion);
                return;
            }

            if (!latestVersion.equals(lastVersion)) {
                String message = "New OpenCode version available: " + latestVersion
                        + "\nhttps://github.com/anomalyco/opencode/releases/latest";
                telegramClient.sendMessage(message);
                lastNotifiedVersion.set(latestVersion);
                log.info("{} | Notified about new OpenCode version {}", Instant.now(), latestVersion);
            }

        } catch (Exception ex) {
            log.error("{} | Failed to check for new OpenCode version: {}", Instant.now(), ex.getMessage());
        }
    }
}
