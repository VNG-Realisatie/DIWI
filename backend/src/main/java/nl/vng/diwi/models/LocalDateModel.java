package nl.vng.diwi.models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

@Data
public class LocalDateModel {

    private final int year;
    private final int month;
    private final int day;
    @JsonValue
    private final String date;

    public LocalDateModel(LocalDate date) {
        year = date.getYear();
        month = date.getMonthValue();
        day = date.getDayOfMonth();
        this.date = String.format("%04d-%02d-%02d", year, month, day);
    }
}
