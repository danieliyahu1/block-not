package com.akatsuki.block_not.opencode;

import com.akatsuki.block_not.telegram.TelegramClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class OpenCodeNotifier {

    private static final Logger log = LoggerFactory.getLogger(OpenCodeNotifier.class);
    private static final String RELEASES_URL = "https://github.com/anomalyco/opencode/releases/latest";

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
            Instant now = Instant.now();

            if (latestVersion == null) {
                log.warn("{} | Could not parse latest OpenCode version from response", now);
                return;
            }

            String lastVersion = lastNotifiedVersion.get();
            log.info("{} | Latest OpenCode version: {} — Last notified: {}", now, latestVersion, lastVersion);

            if (!latestVersion.equals(lastVersion)) {
                notifyNewVersion(latestVersion, now);
            }

        } catch (Exception ex) {
            log.error("{} | Failed to check for new OpenCode version: {}", Instant.now(), ex.getMessage());
        }
    }

    private void notifyNewVersion(String version, Instant now) throws IOException, InterruptedException {
        String message = "New OpenCode version available: " + version + "\n" + RELEASES_URL;
        telegramClient.sendMessage(message);
        lastNotifiedVersion.set(version);
        log.info("{} | Notified about new OpenCode version {}", now, version);
    }
}
