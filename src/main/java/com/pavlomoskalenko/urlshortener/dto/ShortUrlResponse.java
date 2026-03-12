package com.pavlomoskalenko.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ShortUrlResponse {
    private String originalUrl;
    private String shortUrl;
    private LocalDateTime expirationDate;
}
