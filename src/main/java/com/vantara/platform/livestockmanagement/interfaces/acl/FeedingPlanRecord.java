package com.vantara.platform.livestockmanagement.interfaces.acl;

import java.time.LocalDate;

/**
 * Read view of a feeding plan published to other bounded contexts.
 *
 * @param id       identifier of the plan
 * @param lotId    lot the plan belongs to
 * @param animalId individual animal the plan targets, or null for the whole lot
 * @param name     name of the plan
 * @param startsOn first day of the plan
 */
public record FeedingPlanRecord(String id, String lotId, String animalId, String name, LocalDate startsOn) {
}
