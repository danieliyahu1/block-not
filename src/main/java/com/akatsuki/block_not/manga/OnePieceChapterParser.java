package com.akatsuki.block_not.manga;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OnePieceChapterParser {

    private static final Pattern CHAPTER_TITLE_PATTERN = Pattern.compile(
            "href=\"([^\"]*one-piece-chapter-(\\d+)[^\"]*)\"[^>]*>\\s*<div[^>]*>\\s*One Piece\\s*Chapter\\s*\\d+\\s*</div>\\s*<div[^>]*>([^<]+)</div>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    public ChapterInfo parseLatestChapter(String html) {
        if (html == null || html.isBlank()) {
            return null;
        }

        Matcher matcher = CHAPTER_TITLE_PATTERN.matcher(html);
        if (matcher.find()) {
            String path = matcher.group(1);
            int number = Integer.parseInt(matcher.group(2));
            String title = matcher.group(3).trim();
            String url = path.startsWith("http") ? path : "https://tcbonepiecechapters.com" + path;
            return new ChapterInfo(number, title, url);
        }

        return null;
    }

    public static class ChapterInfo {
        private final int number;
        private final String title;
        private final String url;

        public ChapterInfo(int number, String title, String url) {
            this.number = number;
            this.title = title;
            this.url = url;
        }

        public int getNumber() {
            return number;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return "Chapter " + number + " — " + title;
        }
    }
}
