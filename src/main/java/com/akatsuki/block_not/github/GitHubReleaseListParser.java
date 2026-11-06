package com.akatsuki.block_not.github;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Extracts the tag names from a GitHub releases list response, preserving the
 * order GitHub returns them in (newest first). Draft releases are skipped
 * because they are not published yet; pre-releases are kept so that nothing is
 * missed.
 */
public class GitHubReleaseListParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> parseTags(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }

        try {
            JsonNode root = objectMapper.readTree(json);
            if (!root.isArray()) {
                return List.of();
            }

            List<String> tags = new ArrayList<>();
            for (JsonNode release : root) {
                if (release.path("draft").asBoolean(false)) {
                    continue;
                }
                String tag = release.path("tag_name").asText("").trim();
                if (!tag.isEmpty()) {
                    tags.add(tag);
                }
            }
            return List.copyOf(tags);
        } catch (JacksonException ex) {
            return List.of();
        }
    }
}
