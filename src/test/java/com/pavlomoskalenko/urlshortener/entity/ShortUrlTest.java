package com.pavlomoskalenko.urlshortener.entity;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ShortUrlTest {

    @Autowired
    private TestEntityManager entityManager;

    private ShortUrl shortUrl;

    @BeforeEach
    void setUp() {
        shortUrl = new ShortUrl("https://example.com/originalLongUrl","shortCode", LocalDateTime.now());
    }

    @DisplayName("When valid short URL is provided, should persist and return stored values")
    @Test
    void testShortUrlEntity_whenValidShortUrlProvided_shouldReturnStoredShortUrl() {
        ShortUrl savedShortUrl = entityManager.persistAndFlush(shortUrl);

        assertTrue(savedShortUrl.getId() > 0);
        assertEquals(shortUrl.getShortCode(), savedShortUrl.getShortCode());
        assertEquals(shortUrl.getOriginalUrl(), savedShortUrl.getOriginalUrl());
        assertEquals(shortUrl.getExpirationDate(), savedShortUrl.getExpirationDate());
    }

    @DisplayName("When original URL is empty, should throw ConstraintViolationException")
    @Test
    void testShortUrlEntity_whenOriginalUrlIsEmpty_shouldThrowException() {
        shortUrl.setOriginalUrl("");
        assertThrows(ConstraintViolationException.class, () -> entityManager.persistAndFlush(shortUrl));
    }
}
