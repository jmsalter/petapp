package com.example.animalpictureapp.service;

import com.example.animalpictureapp.domain.AnimalType;

public interface AnimalImageClient {
    DownloadedImage fetchRandomImage(AnimalType animalType);
}
