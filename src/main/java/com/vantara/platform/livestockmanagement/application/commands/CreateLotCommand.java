
package com.vantara.platform.livestockmanagement.application.commands;

/**
 * Request to register a ranch lot.
 */
public record CreateLotCommand(
        String rancherId,
        String name,
        String purpose,
        String status
) {

    public CreateLotCommand {
        require(rancherId, "rancherId");
        require(name, "name");
        require(purpose, "purpose");
        require(status, "status");

        rancherId = rancherId.trim();
        name = name.trim();
        purpose = purpose.trim();
        status = status.trim();
    }

    private static void require(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    name + " cannot be blank"
            );
        }
    }
}
