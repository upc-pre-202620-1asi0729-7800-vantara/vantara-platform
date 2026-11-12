package com.vantara.platform.iam.infrastructure.persistence.jpa.converters;

import com.vantara.platform.iam.domain.model.valueobjects.PasswordHash;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Stores the {@link PasswordHash} value object as a plain string column.
 */
@Converter
public class PasswordHashPersistenceConverter implements AttributeConverter<PasswordHash, String> {

    @Override
    public String convertToDatabaseColumn(PasswordHash attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public PasswordHash convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new PasswordHash(dbData);
    }
}
