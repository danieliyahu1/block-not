package com.akatsuki.block_not.github;

import com.akatsuki.block_not.telegram.TelegramClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Polls a GitHub release feed and notifies only about the newest release.
 *
 * <p>Tracking the top of the release feed rather than the {@code /releases/latest}
 * endpoint matters because "latest" is a flag GitHub lets maintainers pin to an
 * older release; the feed is always ordered newest first.
 */
public abstract class GitHubReleaseNotifier {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ReleaseSource releaseSource;
    private final GitHubReleaseListParser releaseListParser;
    private final TelegramClient telegramClient;
    private final AtomicReference<String> lastNotifiedVersion = new AtomicReference<>(null);

    protected GitHubReleaseNotifier(ReleaseSource releaseSource, GitHubReleaseListParser releaseListParser, TelegramClient telegramClient) {
        this.releaseSource = releaseSource;
        this.releaseListParser = releaseListParser;
        this.telegramClient = telegramClient;
    }

    protected abstract String projectName();

    protected abstract String releasesUrl();

    public void checkForNewVersion() {
        Instant now = Instant.now();
        try {
            String json = releaseSource.fetchReleases();
            List<String> releases = releaseListParser.parseTags(json);

            if (releases.isEmpty()) {
                log.warn("{} | Could not parse any {} release from response", now, projectName());
                return;
            }

            String newest = releases.get(0);
            String lastVersion = lastNotifiedVersion.get();
            log.info("{} | Newest {} release: {} — Last notified: {}", now, projectName(), newest, lastVersion);

            if (!newest.equals(lastVersion)) {
                notifyNewVersion(newest, now);
            }

        } catch (Exception ex) {
            log.error("{} | Failed to check for new {} version: {}", now, projectName(), ex.getMessage());
        }
    }

    private void notifyNewVersion(String version, Instant now) throws IOException, InterruptedException {
        String releaseUrl = releasesUrl() + "/tag/" + URLEncoder.encode(version, StandardCharsets.UTF_8);
        telegramClient.sendMessage("New " + projectName() + " version available: " + version + "\n" + releaseUrl);
        lastNotifiedVersion.set(version);
        log.info("{} | Notified about new {} version {}", now, projectName(), version);
    }
}
