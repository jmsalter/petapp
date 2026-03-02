package com.example.animalpictureapp.service;

import com.example.animalpictureapp.domain.AnimalPicture;
import com.example.animalpictureapp.domain.AnimalType;
import com.example.animalpictureapp.persistence.AnimalPictureRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnimalPictureService {

    private final AnimalImageClient animalImageClient;
    private final AnimalPictureRepository animalPictureRepository;

    public AnimalPictureService(AnimalImageClient animalImageClient, AnimalPictureRepository animalPictureRepository) {
        this.animalImageClient = animalImageClient;
        this.animalPictureRepository = animalPictureRepository;
    }

    @Transactional
    public List<AnimalPicture> fetchAndStorePictures(AnimalType animalType, int count) {
        List<AnimalPicture> pictures = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            DownloadedImage downloadedImage = animalImageClient.fetchRandomImage(animalType);
            AnimalPicture picture = new AnimalPicture();
            picture.setAnimalType(animalType);
            picture.setSourceUrl(downloadedImage.sourceUrl());
            picture.setContentType(downloadedImage.contentType());
            picture.setWidth(downloadedImage.width());
            picture.setHeight(downloadedImage.height());
            picture.setImageData(downloadedImage.data());
            picture.setFetchedAt(Instant.now());
            pictures.add(animalPictureRepository.save(picture));
        }

        return pictures;
    }

    @Transactional(readOnly = true)
    public AnimalPicture findLatestPicture(AnimalType animalType) {
        return animalPictureRepository.findTopByAnimalTypeOrderByFetchedAtDescIdDesc(animalType)
                .orElseThrow(() -> new EntityNotFoundException("No stored picture found for animal type: " + animalType.toJson()));
    }

    @Transactional(readOnly = true)
    public AnimalPicture findPictureById(long id) {
        return animalPictureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No stored picture found for id: " + id));
    }
}
