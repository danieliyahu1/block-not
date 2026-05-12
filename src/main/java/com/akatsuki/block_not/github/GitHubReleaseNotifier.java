package com.akatsuki.block_not.github;

import com.akatsuki.block_not.telegram.TelegramClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

public abstract class GitHubReleaseNotifier {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final GitHubReleaseClient releaseClient;
    private final GitHubReleaseVersionParser versionParser;
    private final TelegramClient telegramClient;
    private final AtomicReference<String> lastNotifiedVersion = new AtomicReference<>(null);

    protected GitHubReleaseNotifier(GitHubReleaseClient releaseClient, GitHubReleaseVersionParser versionParser, TelegramClient telegramClient) {
        this.releaseClient = releaseClient;
        this.versionParser = versionParser;
        this.telegramClient = telegramClient;
    }

    protected abstract String projectName();

    protected abstract String releasesUrl();

    public void checkForNewVersion() {
        try {
            String json = releaseClient.fetchLatestRelease();
            String latestVersion = versionParser.parseVersion(json);
            Instant now = Instant.now();

            if (latestVersion == null) {
                log.warn("{} | Could not parse latest {} version from response", now, projectName());
                return;
            }

            String lastVersion = lastNotifiedVersion.get();
            log.info("{} | Latest {} version: {} — Last notified: {}", now, projectName(), latestVersion, lastVersion);

            if (!latestVersion.equals(lastVersion)) {
                notifyNewVersion(latestVersion, now);
            }

        } catch (Exception ex) {
            log.error("{} | Failed to check for new {} version: {}", Instant.now(), projectName(), ex.getMessage());
        }
    }

    private void notifyNewVersion(String version, Instant now) throws IOException, InterruptedException {
        String message = "New " + projectName() + " version available: " + version + "\n" + releasesUrl();
        telegramClient.sendMessage(message);
        lastNotifiedVersion.set(version);
        log.info("{} | Notified about new {} version {}", now, projectName(), version);
    }
}
