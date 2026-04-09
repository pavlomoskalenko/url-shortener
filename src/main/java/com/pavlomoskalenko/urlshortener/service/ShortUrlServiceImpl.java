package com.pavlomoskalenko.urlshortener.service;

import com.pavlomoskalenko.urlshortener.dao.ShortUrlRepository;
import com.pavlomoskalenko.urlshortener.dto.ShortUrlRequest;
import com.pavlomoskalenko.urlshortener.dto.ShortUrlResponse;
import com.pavlomoskalenko.urlshortener.entity.ShortUrl;
import com.pavlomoskalenko.urlshortener.exception.ShortCodeAlreadyTakenException;
import com.pavlomoskalenko.urlshortener.exception.ShortUrlNotFoundException;
import com.pavlomoskalenko.urlshortener.mapper.ShortUrlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShortUrlServiceImpl implements ShortUrlService {

    private final ShortUrlRepository urlRepository;
    private final Base52IdEncoderService encoderService;
    private final ShortUrlMapper mapper;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    @Override
    public ShortUrlResponse createShortUrl(ShortUrlRequest shortUrlRequest) {
        ShortUrl shortUrl = mapper.mapToEntity(shortUrlRequest);

        Optional<ShortUrl> shortUrlDbOpt = urlRepository.findShortUrlByOriginalUrl(shortUrl.getOriginalUrl());
        if (shortUrlDbOpt.isPresent()) {
            ShortUrl shortUrlDb = shortUrlDbOpt.get();
            if (shortUrlDb.hasExpired()) {
                shortUrlDb.setExpirationDate(LocalDateTime.now().plusDays(30));
            }

            return mapper.mapToResponse(shortUrlDb);
        }

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
        String cachedOriginalUrl = redisTemplate.opsForValue().get(shortCode);
        if (cachedOriginalUrl != null) {
            return cachedOriginalUrl;
        }

        String dbOriginalUrl = urlRepository
                .findShortUrlByShortCode(shortCode)
                .filter(shortUrl -> !shortUrl.hasExpired())
                .orElseThrow(() -> new ShortUrlNotFoundException("There is no url with such short code or it expired"))
                .getOriginalUrl();

        redisTemplate.opsForValue().set(shortCode, dbOriginalUrl, Duration.ofHours(1));

        return dbOriginalUrl;
    }
}
