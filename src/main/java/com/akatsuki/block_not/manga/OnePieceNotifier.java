package com.akatsuki.block_not.manga;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import com.akatsuki.block_not.telegram.TelegramClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OnePieceNotifier {

    private static final Logger log = LoggerFactory.getLogger(OnePieceNotifier.class);

    private final MangaTrackerClient mangaTrackerClient;
    private final OnePieceChapterParser chapterParser;
    private final TelegramClient telegramClient;
    private final AtomicInteger lastNotifiedChapter;

    public OnePieceNotifier(MangaTrackerClient mangaTrackerClient, OnePieceChapterParser chapterParser, TelegramClient telegramClient) {
        this.mangaTrackerClient = mangaTrackerClient;
        this.chapterParser = chapterParser;
        this.telegramClient = telegramClient;
        this.lastNotifiedChapter = new AtomicInteger(0);
    }

    @Scheduled(fixedDelayString = "${blocknot.manga.poll-delay-ms:1800000}", initialDelayString = "5000")
    public void checkForNewChapter() {
        try {
            String html = mangaTrackerClient.fetchPage();
            OnePieceChapterParser.ChapterInfo latest = chapterParser.parseLatestChapter(html);

            if (latest == null) {
                log.warn("{} | Could not parse latest One Piece chapter from page", Instant.now());
                return;
            }

            int currentChapter = latest.getNumber();
            int lastNotified = lastNotifiedChapter.get();

            log.info("{} | Latest chapter on site: {} — Last notified: {}", Instant.now(), currentChapter, lastNotified);

            if (currentChapter > lastNotified) {
                String message = "\uD83D\uDCDA New One Piece chapter: " + latest.toString();
                telegramClient.sendMessage(message);
                lastNotifiedChapter.set(currentChapter);
                log.info("{} | Notified about chapter {} ({})", Instant.now(), currentChapter, latest.getTitle());
            }

        } catch (Exception ex) {
            log.error("{} | Failed to check for new One Piece chapter: {}", Instant.now(), ex.getMessage());
        }
    }
}
