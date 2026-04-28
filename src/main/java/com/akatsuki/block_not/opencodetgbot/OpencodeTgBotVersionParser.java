package com.akatsuki.block_not.opencodetgbot;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OpencodeTgBotVersionParser {

    private static final Pattern TAG_NAME_PATTERN = Pattern.compile("\"tag_name\"\\s*:\\s*\"([^\"]+)\"");

    public String parseVersion(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        Matcher matcher = TAG_NAME_PATTERN.matcher(json);
        return matcher.find() ? matcher.group(1).trim() : null;
    }
}
