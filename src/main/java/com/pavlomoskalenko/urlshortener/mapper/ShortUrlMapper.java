package com.pavlomoskalenko.urlshortener.mapper;

import com.pavlomoskalenko.urlshortener.dto.ShortUrlRequest;
import com.pavlomoskalenko.urlshortener.dto.ShortUrlResponse;
import com.pavlomoskalenko.urlshortener.entity.ShortUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ShortUrlMapper {
    @Value("${app.base-url}")
    private String baseUrl;

    public ShortUrl mapToEntity(ShortUrlRequest urlRequest) {
        LocalDateTime expirationDate = urlRequest.getExpireInDays() != null
                ? LocalDateTime.now().plusDays(urlRequest.getExpireInDays())
                : null;

        return new ShortUrl(urlRequest.getOriginalUrl(), urlRequest.getCustomAlias(), expirationDate);
    }

    public ShortUrlResponse mapToResponse(ShortUrl shortUrl) {
        return new ShortUrlResponse(
                shortUrl.getOriginalUrl(),
                baseUrl + "/" + shortUrl.getShortCode(),
                shortUrl.getExpirationDate());
    }
}
