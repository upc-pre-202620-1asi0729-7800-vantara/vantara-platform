package com.vantara.platform.iam.infrastructure.persistence.jpa.converters;

import com.vantara.platform.iam.domain.model.valueobjects.UserId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Stores the {@link UserId} value object as a numeric column.
 */
@Converter
public class UserIdPersistenceConverter implements AttributeConverter<UserId, Long> {

    @Override
    public Long convertToDatabaseColumn(UserId attribute) {
        return attribute == null ? null : attribute.userId();
    }

    @Override
    public UserId convertToEntityAttribute(Long dbData) {
        return dbData == null ? null : new UserId(dbData);
    }
}
