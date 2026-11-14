package com.vantara.platform.livestockmanagement.interfaces.acl;

/**
 * Read view of an ingredient of a feeding plan published to other bounded contexts.
 *
 * @param id            identifier of the plan item
 * @param feedingPlanId plan the item belongs to
 */
public record FeedingPlanItemRecord(String id, String feedingPlanId) {
}
