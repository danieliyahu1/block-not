package com.akatsuki.block_not.github;

import com.akatsuki.block_not.telegram.TelegramClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Polls a GitHub release feed and notifies about every release it has not seen
 * yet, oldest first. Tracking the whole feed rather than the release GitHub
 * marks as "latest" means no release is skipped, even when several are
 * published between two polls or when the "latest" flag points at an older
 * release.
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

            for (String version : newReleasesSince(releases, lastVersion)) {
                notifyNewVersion(version, now);
            }

        } catch (Exception ex) {
            log.error("{} | Failed to check for new {} version: {}", now, projectName(), ex.getMessage());
        }
    }

    /**
     * Returns the releases published after {@code lastNotified}, oldest first.
     * On the first poll only the newest release is announced so a fresh start
     * does not replay the whole feed.
     */
    private List<String> newReleasesSince(List<String> releases, String lastNotified) {
        if (lastNotified == null) {
            return List.of(releases.get(0));
        }

        int index = releases.indexOf(lastNotified);
        if (index < 0) {
            log.warn("{} | Last notified release {} is no longer in the latest {} releases; notifying all of them",
                    Instant.now(), lastNotified, releases.size());
            index = releases.size();
        }

        List<String> newer = new ArrayList<>(releases.subList(0, index));
        Collections.reverse(newer);
        return newer;
    }

    private void notifyNewVersion(String version, Instant now) throws IOException, InterruptedException {
        String releaseUrl = releasesUrl() + "/tag/" + URLEncoder.encode(version, StandardCharsets.UTF_8);
        telegramClient.sendMessage("New " + projectName() + " version available: " + version + "\n" + releaseUrl);
        lastNotifiedVersion.set(version);
        log.info("{} | Notified about new {} version {}", now, projectName(), version);
    }
}
