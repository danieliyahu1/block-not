package com.akatsuki.block_not.manga;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OnePieceChapterParser {

    private static final Pattern CHAPTER_PATTERN = Pattern.compile(
            "one-piece-chapter-(\\d+)[^\"]*\"[^>]*>\\s*<div[^>]*>\\s*One Piece\\s*Chapter\\s*\\d+\\s*</div>\\s*<div[^>]*>([^<]+)</div>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private static final Pattern CHAPTER_TITLE_PATTERN = Pattern.compile(
            "one-piece-chapter-(\\d+)[^\"]*\"[^>]*>\\s*<div[^>]*>\\s*One Piece\\s*Chapter\\s*\\d+\\s*</div>\\s*<div[^>]*>([^<]+)</div>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    public ChapterInfo parseLatestChapter(String html) {
        if (html == null || html.isBlank()) {
            return null;
        }

        Matcher matcher = CHAPTER_TITLE_PATTERN.matcher(html);
        if (matcher.find()) {
            int number = Integer.parseInt(matcher.group(1));
            String title = matcher.group(2).trim();
            return new ChapterInfo(number, title);
        }

        return null;
    }

    public static class ChapterInfo {
        private final int number;
        private final String title;

        public ChapterInfo(int number, String title) {
            this.number = number;
            this.title = title;
        }

        public int getNumber() {
            return number;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return "Chapter " + number + " — " + title;
        }
    }
}
