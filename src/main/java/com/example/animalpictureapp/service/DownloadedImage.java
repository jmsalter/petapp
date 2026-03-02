package com.example.animalpictureapp.service;

public record DownloadedImage(
        String sourceUrl,
        String contentType,
        int width,
        int height,
        byte[] data
) {
}
