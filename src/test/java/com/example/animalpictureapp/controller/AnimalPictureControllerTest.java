package com.example.animalpictureapp.controller;

import com.example.animalpictureapp.domain.AnimalPicture;
import com.example.animalpictureapp.domain.AnimalType;
import com.example.animalpictureapp.service.AnimalPictureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AnimalPictureControllerTest {

    @Mock
    private AnimalPictureService animalPictureService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AnimalPictureController(animalPictureService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void postPicturesReturnsStoredPictures() throws Exception {
        AnimalPicture picture = new AnimalPicture();
        picture.setAnimalType(AnimalType.BEAR);
        picture.setSourceUrl("https://placebear.com/300/300");
        picture.setContentType("image/jpeg");
        picture.setWidth(300);
        picture.setHeight(300);
        picture.setFetchedAt(Instant.parse("2026-03-02T12:00:00Z"));
        picture.setImageData(new byte[]{1, 2, 3});

        var idField = AnimalPicture.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(picture, 7L);

        when(animalPictureService.fetchAndStorePictures(eq(AnimalType.BEAR), eq(1)))
                .thenReturn(List.of(picture));

        mockMvc.perform(post("/api/pictures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "animalType": "bear",
                                  "count": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7))
                .andExpect(jsonPath("$[0].animalType").value("bear"))
                .andExpect(jsonPath("$[0].contentUrl").value("/api/pictures/7/content"));
    }

    @Test
    void getPictureContentStreamsBinaryData() throws Exception {
        AnimalPicture picture = new AnimalPicture();
        picture.setAnimalType(AnimalType.CAT);
        picture.setSourceUrl("https://placekittens.com/320/320");
        picture.setContentType("image/png");
        picture.setWidth(320);
        picture.setHeight(320);
        picture.setFetchedAt(Instant.now());
        picture.setImageData(new byte[]{9, 8, 7});

        var idField = AnimalPicture.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(picture, 11L);

        when(animalPictureService.findPictureById(11L)).thenReturn(picture);

        mockMvc.perform(get("/api/pictures/11/content"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(content().bytes(new byte[]{9, 8, 7}));
    }
}
