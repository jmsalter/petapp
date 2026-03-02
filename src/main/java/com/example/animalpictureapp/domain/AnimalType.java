package com.example.animalpictureapp.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum AnimalType {
    CAT,
    DOG,
    BEAR;

    @JsonCreator
    public static AnimalType from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("animalType must be one of: cat, dog, bear");
        }

        return AnimalType.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }

    @JsonValue
    public String toJson() {
        return name().toLowerCase(Locale.ROOT);
    }
}
