package com.akatsuki.block_not.opencode;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OpenCodeVersionParser {

    private static final Pattern TAG_NAME_PATTERN = Pattern.compile("\"tag_name\"\\s*:\\s*\"([^\"]+)\"");

    public String parseVersion(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }

        Matcher matcher = TAG_NAME_PATTERN.matcher(json);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return null;
    }
}
