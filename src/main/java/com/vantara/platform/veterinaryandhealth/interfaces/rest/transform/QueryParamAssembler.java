package com.vantara.platform.veterinaryandhealth.interfaces.rest.transform;

import com.vantara.platform.veterinaryandhealth.domain.model.valueobjects.AnimalId;

/**
 * Translates optional query parameters into domain values. Blank parameters are treated as absent.
 */
public final class QueryParamAssembler {

    private QueryParamAssembler() {
    }

    public static AnimalId toAnimalIdOrNull(String value) {
        return value == null || value.isBlank() ? null : new AnimalId(value);
    }

    public static String toTextOrNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
