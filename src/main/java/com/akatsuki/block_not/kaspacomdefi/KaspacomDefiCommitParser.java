package com.akatsuki.block_not.kaspacomdefi;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class KaspacomDefiCommitParser {

    // Matches the first "sha" field in the JSON array response (the latest commit)
    private static final Pattern SHA_PATTERN = Pattern.compile("\"sha\"\\s*:\\s*\"([a-f0-9]+)\"");

    // Matches the commit message — handles escaped characters, takes first match
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("\"message\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");

    public String parseSha(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        Matcher matcher = SHA_PATTERN.matcher(json);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    public String parseMessage(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        Matcher matcher = MESSAGE_PATTERN.matcher(json);
        if (!matcher.find()) {
            return null;
        }
        String raw = matcher.group(1)
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
        // Take only the first line
        int newline = raw.indexOf('\n');
        return newline >= 0 ? raw.substring(0, newline).trim() : raw.trim();
    }
}
