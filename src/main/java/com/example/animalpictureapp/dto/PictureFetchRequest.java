package com.example.animalpictureapp.dto;

import com.example.animalpictureapp.domain.AnimalType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PictureFetchRequest(
        @NotNull AnimalType animalType,
        @Min(1) @Max(10) int count
) {
}
