package com.example.animalpictureapp.persistence;

import com.example.animalpictureapp.domain.AnimalPicture;
import com.example.animalpictureapp.domain.AnimalType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnimalPictureRepository extends JpaRepository<AnimalPicture, Long> {
    Optional<AnimalPicture> findTopByAnimalTypeOrderByFetchedAtDescIdDesc(AnimalType animalType);
}
