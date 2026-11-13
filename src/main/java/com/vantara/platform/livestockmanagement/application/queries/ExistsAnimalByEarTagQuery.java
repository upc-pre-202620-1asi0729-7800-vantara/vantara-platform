package com.vantara.platform.livestockmanagement.application.queries;

/**
 * Query to know whether a rancher already has an animal with the given ear tag.
 */
public record ExistsAnimalByEarTagQuery(String rancherId, String earTag) {

    public ExistsAnimalByEarTagQuery {
        if (rancherId == null || rancherId.isBlank()) {
            throw new IllegalArgumentException("rancherId cannot be blank");
        }
        if (earTag == null || earTag.isBlank()) {
            throw new IllegalArgumentException("earTag cannot be blank");
        }
        rancherId = rancherId.trim();
        earTag = earTag.trim();
    }
}
