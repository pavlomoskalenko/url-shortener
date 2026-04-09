package com.pavlomoskalenko.urlshortener.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class ShortUrlResponse {
    private final String originalUrl;
    private final String shortUrl;
    private final LocalDateTime expirationDate;
}
