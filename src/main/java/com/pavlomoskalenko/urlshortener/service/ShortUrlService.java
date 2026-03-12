package com.pavlomoskalenko.urlshortener.service;

import com.pavlomoskalenko.urlshortener.dto.ShortUrlRequest;
import com.pavlomoskalenko.urlshortener.dto.ShortUrlResponse;

public interface ShortUrlService {
    ShortUrlResponse createShortUrl(ShortUrlRequest shortUrlRequest);
    String getOriginalUrlByShortCode(String shortCode);
}
