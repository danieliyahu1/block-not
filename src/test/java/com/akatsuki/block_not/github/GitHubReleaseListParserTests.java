package com.akatsuki.block_not.github;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GitHubReleaseListParserTests {

    private final GitHubReleaseListParser parser = new GitHubReleaseListParser();

    @Test
    void keepsFeedOrderAndSkipsDraftReleases() {
        String json = """
                [
                  {"tag_name": "process-daemon-v0.5.0", "draft": false, "prerelease": false},
                  {"tag_name": "v2.2.5", "draft": false, "prerelease": false},
                  {"tag_name": "v2.3.0", "draft": true, "prerelease": false}
                ]
                """;

        assertEquals(List.of("process-daemon-v0.5.0", "v2.2.5"), parser.parseTags(json));
    }

    @Test
    void keepsPreReleasesSoNothingIsMissed() {
        String json = """
                [
                  {"tag_name": "v2.3.0-beta.1", "draft": false, "prerelease": true},
                  {"tag_name": "v2.2.5", "draft": false, "prerelease": false}
                ]
                """;

        assertEquals(List.of("v2.3.0-beta.1", "v2.2.5"), parser.parseTags(json));
    }

    @Test
    void returnsEmptyListForErrorPayload() {
        assertTrue(parser.parseTags("{\"message\": \"Not Found\"}").isEmpty());
    }

    @Test
    void returnsEmptyListForBlankInput() {
        assertTrue(parser.parseTags(null).isEmpty());
        assertTrue(parser.parseTags("   ").isEmpty());
    }
}
