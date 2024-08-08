package com.yoga.backend.common.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Converter(autoApply = false)
public class InstantToSqlTimeConverter implements AttributeConverter<Instant, Time> {

    @Override
    public Time convertToDatabaseColumn(Instant instant) {
        return instant == null ? null : Time.valueOf(LocalTime.ofInstant(instant, ZoneId.systemDefault()));
    }

    @Override
    public Instant convertToEntityAttribute(Time time) {
        return time == null ? null : time.toLocalTime().atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant();
    }
}
