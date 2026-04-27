package com.akatsuki.block_not.kaspacomdefi;

import com.akatsuki.block_not.telegram.TelegramClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class KaspacomDefiNotifier {

    private static final Logger log = LoggerFactory.getLogger(KaspacomDefiNotifier.class);
    private static final String REPO_URL = "https://github.com/KASPACOM/kaspacom-defi-mcp";

    private final KaspacomDefiClient client;
    private final KaspacomDefiCommitParser parser;
    private final TelegramClient telegramClient;
    private final AtomicReference<String> lastNotifiedSha;

    public KaspacomDefiNotifier(KaspacomDefiClient client, KaspacomDefiCommitParser parser, TelegramClient telegramClient) {
        this.client = client;
        this.parser = parser;
        this.telegramClient = telegramClient;
        this.lastNotifiedSha = new AtomicReference<>(null);
    }

    @Scheduled(fixedDelayString = "${blocknot.kaspacomdefi.poll-delay-ms:3600000}", initialDelayString = "5000")
    public void checkForNewCommit() {
        try {
            String json = client.fetchLatestCommit();
            String latestSha = parser.parseSha(json);
            Instant now = Instant.now();

            if (latestSha == null) {
                log.warn("{} | Could not parse latest commit SHA from response", now);
                return;
            }

            String lastSha = lastNotifiedSha.get();
            log.info("{} | Latest kaspacom-defi-mcp SHA: {} — Last notified: {}", now, latestSha.substring(0, 7), lastSha == null ? "none" : lastSha.substring(0, 7));

            if (!latestSha.equals(lastSha)) {
                notifyNewCommit(latestSha, parser.parseMessage(json), now);
            }

        } catch (Exception ex) {
            log.error("{} | Failed to check for new kaspacom-defi-mcp commit: {}", Instant.now(), ex.getMessage());
        }
    }

    private void notifyNewCommit(String sha, String message, Instant now) throws IOException, InterruptedException {
        String shortSha = sha.substring(0, 7);
        String commitMessage = message != null ? message : "(no message)";
        String text = "New commit in kaspacom-defi-mcp!\n\n"
                + "SHA: " + shortSha + "\n"
                + "Message: " + commitMessage + "\n"
                + REPO_URL + "/commit/" + sha;

        telegramClient.sendMessage(text);
        lastNotifiedSha.set(sha);
        log.info("{} | Notified about new kaspacom-defi-mcp commit {}", now, shortSha);
    }
}
