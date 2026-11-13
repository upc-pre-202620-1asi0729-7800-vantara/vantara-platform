
package com.vantara.platform.livestockmanagement.domain.model.valueobjects;

import java.util.Objects;

public record EarTag(String value) {

    public EarTag {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Ear tag cannot be blank");
        }

        value = value.trim();
    }

    public boolean equalsValue(EarTag other) {
        return other != null && Objects.equals(value, other.value);
    }
}
