package com.vantara.platform.livestockmanagement.interfaces.acl;

/**
 * Read view of a lot published to other bounded contexts.
 *
 * @param id        identifier of the lot
 * @param rancherId owner of the lot
 * @param name      name of the lot
 */
public record LotRecord(String id, String rancherId, String name) {
}
