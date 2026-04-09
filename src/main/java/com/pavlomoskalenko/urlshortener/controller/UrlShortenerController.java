package com.pavlomoskalenko.urlshortener.controller;

import com.pavlomoskalenko.urlshortener.dto.ShortUrlRequest;
import com.pavlomoskalenko.urlshortener.dto.ShortUrlResponse;
import com.pavlomoskalenko.urlshortener.service.ShortUrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/short-url")
@RequiredArgsConstructor
public class UrlShortenerController {

    public final ShortUrlService shortUrlService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShortUrlResponse createShortUrl(@RequestBody @Valid ShortUrlRequest request) {
        return shortUrlService.createShortUrl(request);
    }
}
