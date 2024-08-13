package com.yoga.backend.common.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.sql.Date;
import java.time.Instant;


@Converter(autoApply = false)
public class InstantToSqlDateConverter implements AttributeConverter<Instant, Date> {

    @Override
    public Date convertToDatabaseColumn(Instant instant) {
        return instant == null ? null : new Date(instant.toEpochMilli());
    }

    @Override
    public Instant convertToEntityAttribute(Date date) {
        return date == null ? null : Instant.ofEpochMilli(date.getTime());
    }
}