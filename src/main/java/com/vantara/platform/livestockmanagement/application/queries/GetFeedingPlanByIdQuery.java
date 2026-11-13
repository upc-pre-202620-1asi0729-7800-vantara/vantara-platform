
package com.vantara.platform.livestockmanagement.application.queries;

/**
 * Retrieves one feeding plan by identifier.
 */
public record GetFeedingPlanByIdQuery(
        String id
) {

    public GetFeedingPlanByIdQuery {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "id cannot be blank"
            );
        }

        id = id.trim();
    }
}
