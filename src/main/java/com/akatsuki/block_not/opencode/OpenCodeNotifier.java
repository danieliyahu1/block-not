package com.akatsuki.block_not.opencode;

import com.akatsuki.block_not.github.GitHubReleaseClient;
import com.akatsuki.block_not.github.GitHubReleaseNotifier;
import com.akatsuki.block_not.github.GitHubReleaseVersionParser;
import com.akatsuki.block_not.telegram.TelegramClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OpenCodeNotifier extends GitHubReleaseNotifier {

    private static final String PROJECT_NAME = "OpenCode";
    private static final String RELEASES_URL = "https://github.com/anomalyco/opencode/releases/latest";

    public OpenCodeNotifier(OpenCodeProperties properties, TelegramClient telegramClient) {
        super(new GitHubReleaseClient(properties), new GitHubReleaseVersionParser(), telegramClient);
    }

    @Override
    protected String projectName() {
        return PROJECT_NAME;
    }

    @Override
    protected String releasesUrl() {
        return RELEASES_URL;
    }

    @Scheduled(fixedDelayString = "${blocknot.opencode.poll-delay-ms:3600000}", initialDelayString = "5000")
    @Override
    public void checkForNewVersion() {
        super.checkForNewVersion();
    }
}
