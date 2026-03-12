package com.pavlomoskalenko.urlshortener.dao;

import com.pavlomoskalenko.urlshortener.entity.ShortUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ShortUrlRepositoryTest {

    @Autowired
    private ShortUrlRepository urlRepository;

    @Autowired
    private TestEntityManager entityManager;

    private ShortUrl shortUrl;

    @BeforeEach
    void setUp() {
        shortUrl = new ShortUrl("https://example.com/originalLongUrl", "shortCode", LocalDateTime.now());
    }

    @DisplayName("When existing short code is provided, should return true")
    @Test
    void testExistsByShortCode_whenActualShortCodeProvided_shouldReturnTrue() {
        entityManager.persistAndFlush(shortUrl);

        boolean exists = urlRepository.existsShortUrlByShortCode(shortUrl.getShortCode());

        assertTrue(exists);
    }

    @DisplayName("When non-existent short code is provided, should return false")
    @Test
    void testExistsByShortCode_whenNonExistentShortCodeProvided_shouldReturnFalse() {
        String nonExistentShortCode = "noSuchShortCode";

        boolean exists = urlRepository.existsShortUrlByShortCode(nonExistentShortCode);

        assertFalse(exists);
    }

    @DisplayName("when existing short code is provided, should return short URL")
    @Test
    void testFindByShortCode_whenActualShortCodeProvided_shouldReturnShortUrl() {
        entityManager.persistAndFlush(shortUrl);

        Optional<ShortUrl> optionalShortUrl = urlRepository.findShortUrlByShortCode(shortUrl.getShortCode());

        assertTrue(optionalShortUrl.isPresent());
        ShortUrl savedShortUrl = optionalShortUrl.get();
        assertEquals(shortUrl.getShortCode(), savedShortUrl.getShortCode());
        assertEquals(shortUrl.getOriginalUrl(), savedShortUrl.getOriginalUrl());
        assertEquals(shortUrl.getExpirationDate(), savedShortUrl.getExpirationDate());
    }

    @DisplayName("when non-existent short code is provided, should return empty result")
    @Test
    void testFindByShortCode_whenNonExistentShortCodeProvided_shouldReturnEmptyOptional() {
        String nonExistentShortCode = "noSuchShortCode";

        Optional<ShortUrl> optionalShortUrl = urlRepository.findShortUrlByShortCode(nonExistentShortCode);
        assertTrue(optionalShortUrl.isEmpty());
    }
}
