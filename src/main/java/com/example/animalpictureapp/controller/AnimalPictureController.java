package com.example.animalpictureapp.controller;

import com.example.animalpictureapp.domain.AnimalPicture;
import com.example.animalpictureapp.domain.AnimalType;
import com.example.animalpictureapp.dto.PictureFetchRequest;
import com.example.animalpictureapp.dto.PictureResponse;
import com.example.animalpictureapp.service.AnimalPictureService;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pictures")
public class AnimalPictureController {

    private final AnimalPictureService animalPictureService;

    public AnimalPictureController(AnimalPictureService animalPictureService) {
        this.animalPictureService = animalPictureService;
    }

    @PostMapping
    public List<PictureResponse> fetchPictures(@Valid @RequestBody PictureFetchRequest request) {
        return animalPictureService.fetchAndStorePictures(request.animalType(), request.count()).stream()
                .map(PictureResponse::from)
                .toList();
    }

    @GetMapping("/{animalType}/latest")
    public PictureResponse getLatestPicture(@PathVariable String animalType) {
        AnimalPicture picture = animalPictureService.findLatestPicture(AnimalType.from(animalType));
        return PictureResponse.from(picture);
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<byte[]> getPictureContent(@PathVariable long id) {
        AnimalPicture picture = animalPictureService.findPictureById(id);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"%d\"".formatted(picture.getId()))
                .contentType(MediaType.parseMediaType(picture.getContentType()))
                .body(picture.getImageData());
    }
}
