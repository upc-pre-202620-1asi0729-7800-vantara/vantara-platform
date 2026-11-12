package com.vantara.platform.livestockmanagement.application.commands;

/** Request to delete an animal by identifier. */
public record DeleteAnimalCommand(String id) {
    public DeleteAnimalCommand {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id cannot be blank");
    }
}
