package com.akatsuki.block_not.github;

import java.io.IOException;

/**
 * Supplies the raw JSON payload of a repository's release feed.
 */
@FunctionalInterface
public interface ReleaseSource {

    String fetchReleases() throws IOException, InterruptedException;
}
