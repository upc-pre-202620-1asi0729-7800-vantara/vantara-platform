package com.vantara.platform.iam.infrastructure.persistence.jpa.converters;

import com.vantara.platform.iam.domain.model.valueobjects.VerificationCodeValue;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Stores the {@link VerificationCodeValue} value object as a plain string column.
 */
@Converter
public class VerificationCodeValuePersistenceConverter implements AttributeConverter<VerificationCodeValue, String> {

    @Override
    public String convertToDatabaseColumn(VerificationCodeValue attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public VerificationCodeValue convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new VerificationCodeValue(dbData);
    }
}
