package com.vantara.platform.veterinaryandhealth.domain.model.aggregates;

import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.util.Objects;

/**
 * Medicine of the veterinary catalog. It is reference data maintained outside the API,
 * so the domain only rebuilds it from persistence.
 */
public class Medication extends AbstractDomainAggregateRoot<Medication> {

    private final String id;
    private final String name;
    private final String category;
    private final String activeIngredient;
    private final String presentation;

    private Medication(String id, String name, String category, String activeIngredient, String presentation) {
        this.id = Objects.requireNonNull(id, "Medication id cannot be null");
        this.name = requireText(name, "Medication name");
        this.category = requireText(category, "Category");
        this.activeIngredient = requireText(activeIngredient, "Active ingredient");
        this.presentation = requireText(presentation, "Presentation");
    }

    public static Medication reconstitute(String id, String name, String category, String activeIngredient,
                                          String presentation) {
        return new Medication(id, name, category, activeIngredient, presentation);
    }

    private static String requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " cannot be blank");
        }
        return value.trim();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getActiveIngredient() {
        return activeIngredient;
    }

    public String getPresentation() {
        return presentation;
    }
}
