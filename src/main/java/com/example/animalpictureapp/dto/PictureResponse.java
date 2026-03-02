package com.example.animalpictureapp.dto;

import com.example.animalpictureapp.domain.AnimalPicture;
import com.example.animalpictureapp.domain.AnimalType;

import java.time.Instant;

public record PictureResponse(
        Long id,
        AnimalType animalType,
        String sourceUrl,
        String contentType,
        int width,
        int height,
        Instant fetchedAt,
        String contentUrl
) {
    public static PictureResponse from(AnimalPicture picture) {
        return new PictureResponse(
                picture.getId(),
                picture.getAnimalType(),
                picture.getSourceUrl(),
                picture.getContentType(),
                picture.getWidth(),
                picture.getHeight(),
                picture.getFetchedAt(),
                "/api/pictures/" + picture.getId() + "/content"
        );
    }
}
