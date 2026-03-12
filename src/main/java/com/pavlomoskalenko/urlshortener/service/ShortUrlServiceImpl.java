package com.pavlomoskalenko.urlshortener.service;

import com.pavlomoskalenko.urlshortener.dao.ShortUrlRepository;
import com.pavlomoskalenko.urlshortener.dto.ShortUrlRequest;
import com.pavlomoskalenko.urlshortener.dto.ShortUrlResponse;
import com.pavlomoskalenko.urlshortener.entity.ShortUrl;
import com.pavlomoskalenko.urlshortener.exception.ShortCodeAlreadyTakenException;
import com.pavlomoskalenko.urlshortener.exception.ShortUrlNotFoundException;
import com.pavlomoskalenko.urlshortener.mapper.ShortUrlMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    private final ShortUrlRepository urlRepository;
    private final Base52IdEncoderService encoderService;
    private final ShortUrlMapper mapper;

    public ShortUrlServiceImpl(ShortUrlRepository urlRepository,
                               Base52IdEncoderService encoderService,
                               ShortUrlMapper mapper) {
        this.urlRepository = urlRepository;
        this.encoderService = encoderService;
        this.mapper = mapper;
    }

    @Transactional
    @Override
    public ShortUrlResponse createShortUrl(ShortUrlRequest shortUrlRequest) {
        ShortUrl shortUrl = mapper.mapToEntity(shortUrlRequest);
        if (shortUrl.getShortCode() != null) {
            if (urlRepository.existsShortUrlByShortCode(shortUrl.getShortCode())) {
                throw new ShortCodeAlreadyTakenException("Such custom alias is not allowed. Short code already exists");
            }

            return mapper.mapToResponse(urlRepository.save(shortUrl));
        } else {
            ShortUrl shortUrlSaved = urlRepository.save(shortUrl);
            shortUrlSaved.setShortCode(encoderService.encode(shortUrlSaved.getId()));

            return mapper.mapToResponse(shortUrlSaved);
        }
    }

    @Override
    public String getOriginalUrlByShortCode(String shortCode) {
        return urlRepository
                .findShortUrlByShortCode(shortCode)
                .filter(shortUrl -> shortUrl.getExpirationDate().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new ShortUrlNotFoundException("There is no url with such short code or it expired"))
                .getOriginalUrl();
    }
}
