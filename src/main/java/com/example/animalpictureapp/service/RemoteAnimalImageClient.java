package com.example.animalpictureapp.service;

import com.example.animalpictureapp.domain.AnimalType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class RemoteAnimalImageClient implements AnimalImageClient {

    private static final int MIN_SIZE = 300;
    private static final int MAX_SIZE = 600;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public DownloadedImage fetchRandomImage(AnimalType animalType) {
        int width = ThreadLocalRandom.current().nextInt(MIN_SIZE, MAX_SIZE + 1);
        int height = ThreadLocalRandom.current().nextInt(MIN_SIZE, MAX_SIZE + 1);
        String url = buildUrl(animalType, width, height);

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(20))
                .build();

        try {
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Image provider returned HTTP " + response.statusCode() + " for " + animalType.toJson());
            }

            String contentType = response.headers()
                    .firstValue("Content-Type")
                    .orElse("image/jpeg");

            return new DownloadedImage(url, contentType, width, height, response.body());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to download image for " + animalType.toJson(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Image download was interrupted for " + animalType.toJson(), e);
        }
    }

    private String buildUrl(AnimalType animalType, int width, int height) {
        return switch (animalType) {
            case CAT -> "https://placekittens.com/" + width + "/" + height;
            case DOG -> "https://place.dog/" + width + "/" + height;
            case BEAR -> "https://placebear.com/" + width + "/" + height;
        };
    }
}
