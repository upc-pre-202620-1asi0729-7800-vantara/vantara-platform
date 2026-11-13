package com.vantara.platform.veterinaryandhealth.interfaces.rest.resources;

/**
 * Medication of the veterinary catalog returned by the API.
 */
public record MedicationResource(
        String id,
        String name,
        String category,
        String activeIngredient,
        String presentation) {
}
