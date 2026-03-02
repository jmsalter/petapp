package com.example.animalpictureapp.service;

import com.example.animalpictureapp.domain.AnimalPicture;
import com.example.animalpictureapp.domain.AnimalType;
import com.example.animalpictureapp.persistence.AnimalPictureRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnimalPictureServiceTest {

    @Mock
    private AnimalImageClient animalImageClient;

    @Mock
    private AnimalPictureRepository animalPictureRepository;

    @InjectMocks
    private AnimalPictureService animalPictureService;

    @Test
    void fetchAndStorePicturesSavesEveryDownloadedImage() {
        when(animalImageClient.fetchRandomImage(AnimalType.CAT))
                .thenReturn(new DownloadedImage("https://example.com/1", "image/jpeg", 300, 300, new byte[]{1}))
                .thenReturn(new DownloadedImage("https://example.com/2", "image/jpeg", 320, 320, new byte[]{2}));

        when(animalPictureRepository.save(any(AnimalPicture.class))).thenAnswer(invocation -> {
            AnimalPicture picture = invocation.getArgument(0);
            return picture;
        });

        List<AnimalPicture> pictures = animalPictureService.fetchAndStorePictures(AnimalType.CAT, 2);

        ArgumentCaptor<AnimalPicture> captor = ArgumentCaptor.forClass(AnimalPicture.class);
        verify(animalPictureRepository, times(2)).save(captor.capture());

        assertThat(pictures).hasSize(2);
        assertThat(captor.getAllValues())
                .extracting(AnimalPicture::getAnimalType)
                .containsExactly(AnimalType.CAT, AnimalType.CAT);
        assertThat(captor.getAllValues())
                .extracting(AnimalPicture::getSourceUrl)
                .containsExactly("https://example.com/1", "https://example.com/2");
        assertThat(captor.getAllValues())
                .extracting(AnimalPicture::getFetchedAt)
                .allMatch(Instant.class::isInstance);
    }

    @Test
    void findLatestPictureThrowsWhenNothingStored() {
        when(animalPictureRepository.findTopByAnimalTypeOrderByFetchedAtDescIdDesc(AnimalType.DOG))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> animalPictureService.findLatestPicture(AnimalType.DOG))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("dog");
    }
}
