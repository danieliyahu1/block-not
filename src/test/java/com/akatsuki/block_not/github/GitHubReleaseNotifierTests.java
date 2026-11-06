package com.akatsuki.block_not.github;

import com.akatsuki.block_not.telegram.TelegramClient;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class GitHubReleaseNotifierTests {

    private final TelegramClient telegramClient = mock(TelegramClient.class);

    @Test
    void firstPollAnnouncesOnlyTheNewestRelease() throws Exception {
        TestNotifier notifier = notifier(payload("v2.2.5", "v2.2.4", "v2.2.3"));

        notifier.checkForNewVersion();

        verify(telegramClient, times(1)).sendMessage(contains("v2.2.5"));
        verify(telegramClient, times(0)).sendMessage(contains("v2.2.4"));
    }

    @Test
    void announcesEveryReleasePublishedBetweenPollsOldestFirst() throws Exception {
        AtomicReference<String> feed = new AtomicReference<>(payload("v2.2.3"));
        TestNotifier notifier = notifier(feed);
        notifier.checkForNewVersion();
        reset(telegramClient);

        feed.set(payload("v2.2.5", "v2.2.4", "v2.2.3"));
        notifier.checkForNewVersion();

        InOrder inOrder = inOrder(telegramClient);
        inOrder.verify(telegramClient).sendMessage(contains("v2.2.4"));
        inOrder.verify(telegramClient).sendMessage(contains("v2.2.5"));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void staysSilentWhenNothingChanged() throws Exception {
        TestNotifier notifier = notifier(payload("v2.2.5"));
        notifier.checkForNewVersion();
        reset(telegramClient);

        notifier.checkForNewVersion();

        verifyNoInteractions(telegramClient);
    }

    @Test
    void announcesAllReleasesWhenTheLastSeenOneLeavesTheWindow() throws Exception {
        AtomicReference<String> feed = new AtomicReference<>(payload("v1.0.0"));
        TestNotifier notifier = notifier(feed);
        notifier.checkForNewVersion();
        reset(telegramClient);

        feed.set(payload("v1.0.2", "v1.0.1"));
        notifier.checkForNewVersion();

        InOrder inOrder = inOrder(telegramClient);
        inOrder.verify(telegramClient).sendMessage(contains("v1.0.1"));
        inOrder.verify(telegramClient).sendMessage(contains("v1.0.2"));
    }

    private TestNotifier notifier(String feed) {
        return notifier(new AtomicReference<>(feed));
    }

    private TestNotifier notifier(AtomicReference<String> feed) {
        return new TestNotifier(feed::get, telegramClient);
    }

    private static String payload(String... tags) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < tags.length; i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append("{\"tag_name\":\"").append(tags[i]).append("\",\"draft\":false,\"prerelease\":false}");
        }
        return json.append(']').toString();
    }

    private static final class TestNotifier extends GitHubReleaseNotifier {

        private TestNotifier(ReleaseSource releaseSource, TelegramClient telegramClient) {
            super(releaseSource, new GitHubReleaseListParser(), telegramClient);
        }

        @Override
        protected String projectName() {
            return "TestProject";
        }

        @Override
        protected String releasesUrl() {
            return "https://example.test/releases";
        }
    }
}
