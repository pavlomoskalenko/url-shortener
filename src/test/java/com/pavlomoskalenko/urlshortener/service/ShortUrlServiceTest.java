package com.pavlomoskalenko.urlshortener.service;

import com.pavlomoskalenko.urlshortener.dao.ShortUrlRepository;
import com.pavlomoskalenko.urlshortener.dto.ShortUrlRequest;
import com.pavlomoskalenko.urlshortener.entity.ShortUrl;
import com.pavlomoskalenko.urlshortener.exception.ShortCodeAlreadyTakenException;
import com.pavlomoskalenko.urlshortener.exception.ShortUrlNotFoundException;
import com.pavlomoskalenko.urlshortener.mapper.ShortUrlMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShortUrlServiceTest {

    @InjectMocks
    private ShortUrlServiceImpl shortUrlService;

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @Mock
    private Base52IdEncoderService encoderService;

    @Mock
    private ShortUrlMapper mapper;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private final String alias = "alias";
    private final String originalUrl = "https://original-url.com/example";
    private final LocalDateTime expirationDate = LocalDateTime.now().plusDays(3);
    private ShortUrlRequest shortUrlRequest;
    private ShortUrl shortUrl;
    private ShortUrl dbShortUrl;

    @BeforeEach
    void setUp() {
        shortUrlRequest = new ShortUrlRequest(
                originalUrl,
                alias,
                3);

        shortUrl = new ShortUrl(
                originalUrl,
                alias,
                expirationDate);

        dbShortUrl = new ShortUrl(
                originalUrl,
                alias,
                expirationDate);
        dbShortUrl.setId(1L);
        dbShortUrl.setCreatedAt(LocalDateTime.now());
    }

    @DisplayName("When existing original url is provided, should return short URL from db")
    @Test
    void testCreateShortUrl_whenExistingOriginalUrlProvided_shouldReturnSavedShortUrl() {
        when(mapper.mapToEntity(any(ShortUrlRequest.class))).thenReturn(shortUrl);
        when(shortUrlRepository.findShortUrlByOriginalUrl(eq(originalUrl))).thenReturn(Optional.of(dbShortUrl));

        shortUrlService.createShortUrl(shortUrlRequest);

        verify(shortUrlRepository).findShortUrlByOriginalUrl(eq(originalUrl));
        ArgumentCaptor<ShortUrl> captor = ArgumentCaptor.forClass(ShortUrl.class);
        verify(mapper).mapToResponse(captor.capture());
        assertNotNull(captor.getValue().getId());
        assertEquals(originalUrl, captor.getValue().getOriginalUrl());
        assertEquals(alias, captor.getValue().getShortCode());
    }

    @DisplayName("When alias is provided, should return short URL response with alias")
    @Test
    void testCreateShortUrl_whenAliasProvided_shouldReturnShortUrlResponseWithAlias() {
        when(mapper.mapToEntity(any(ShortUrlRequest.class))).thenReturn(shortUrl);
        when(shortUrlRepository.findShortUrlByOriginalUrl(eq(originalUrl))).thenReturn(Optional.empty());
        when(shortUrlRepository.existsShortUrlByShortCode(eq(alias))).thenReturn(false);
        when(shortUrlRepository.save(any(ShortUrl.class))).thenReturn(dbShortUrl);

        shortUrlService.createShortUrl(shortUrlRequest);

        verify(shortUrlRepository).existsShortUrlByShortCode(eq(alias));
        verify(shortUrlRepository).save(any(ShortUrl.class));
        ArgumentCaptor<ShortUrl> captor = ArgumentCaptor.forClass(ShortUrl.class);
        verify(mapper).mapToResponse(captor.capture());
        assertEquals(originalUrl, captor.getValue().getOriginalUrl());
        assertEquals(alias, captor.getValue().getShortCode());
    }

    @DisplayName("When existing alias is provided, should throw ShortCodeAlreadyTakenException")
    @Test
    void testCreateShortUrl_whenExistingAliasProvided_shouldThrowShortCodeAlreadyTakenException() {
        when(mapper.mapToEntity(any(ShortUrlRequest.class))).thenReturn(shortUrl);
        when(shortUrlRepository.findShortUrlByOriginalUrl(eq(originalUrl))).thenReturn(Optional.empty());
        when(shortUrlRepository.existsShortUrlByShortCode(eq(alias))).thenReturn(true);

        ShortCodeAlreadyTakenException exc = assertThrows(ShortCodeAlreadyTakenException.class, () -> shortUrlService.createShortUrl(shortUrlRequest));

        verify(shortUrlRepository).existsShortUrlByShortCode(eq(alias));
        verify(shortUrlRepository, never()).save(any(ShortUrl.class));
        assertEquals("Such custom alias is not allowed. Short code already exists", exc.getMessage());
    }

    @DisplayName("When alias is not provided, should return short URL with generated short code")
    @Test
    void testCreateShortUrl_whenAliasIsNotProvided_shouldReturnShortUrlWithGeneratedShortCode() {
        shortUrl.setShortCode(null);
        String expectedEncodedId = "b";

        when(mapper.mapToEntity(any(ShortUrlRequest.class))).thenReturn(shortUrl);
        when(shortUrlRepository.findShortUrlByOriginalUrl(eq(originalUrl))).thenReturn(Optional.empty());
        when(shortUrlRepository.save(any(ShortUrl.class))).thenReturn(dbShortUrl);
        when(encoderService.encode(1L)).thenReturn(expectedEncodedId);

        shortUrlService.createShortUrl(shortUrlRequest);

        verify(encoderService).encode(1L);
        ArgumentCaptor<ShortUrl> captor = ArgumentCaptor.forClass(ShortUrl.class);
        verify(mapper).mapToResponse(captor.capture());
        assertEquals(expectedEncodedId, captor.getValue().getShortCode());
    }

    @DisplayName("When cached short code is provided, should return original URL")
    @Test
    void testGetOriginalUrlByShortCode_whenCachedShortCodeProvided_shouldReturnOriginalUrl() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(eq(alias))).thenReturn(originalUrl);

        String actualOriginalUrl = shortUrlService.getOriginalUrlByShortCode(alias);

        verify(shortUrlRepository, never()).findShortUrlByShortCode(eq(alias));
        assertEquals(originalUrl, actualOriginalUrl);
    }

    @DisplayName("When short code is provided, should return original URL and update cache")
    @Test
    void testGetOriginalUrlByShortCode_whenShortCodeProvided_shouldReturnOriginalUrlAndUpdateCache() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(eq(alias))).thenReturn(null);
        when(shortUrlRepository.findShortUrlByShortCode(eq(alias))).thenReturn(Optional.of(dbShortUrl));

        String actualOriginalUrl = shortUrlService.getOriginalUrlByShortCode(alias);

        verify(valueOperations).get(eq(alias));
        verify(shortUrlRepository).findShortUrlByShortCode(eq(alias));
        verify(valueOperations).set(eq(alias), eq(actualOriginalUrl), any(Duration.class));
        assertEquals(originalUrl, actualOriginalUrl);
    }

    @DisplayName("When short code is expired, should throw ShortUrlNotFoundException")
    @Test
    void testGetOriginalUrlByShortCode_whenShortCodeIsExpired_shouldThrowShortUrlNotFoundException() {
        dbShortUrl.setExpirationDate(LocalDateTime.now().minusDays(1));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(eq(alias))).thenReturn(null);
        when(shortUrlRepository.findShortUrlByShortCode(eq(alias))).thenReturn(Optional.of(dbShortUrl));

        ShortUrlNotFoundException exc = assertThrows(ShortUrlNotFoundException.class, () -> shortUrlService.getOriginalUrlByShortCode(alias));

        verify(shortUrlRepository).findShortUrlByShortCode(eq(alias));
        verify(valueOperations, never()).set(eq(alias), anyString(), any(Duration.class));
        assertEquals("There is no url with such short code or it expired", exc.getMessage());
    }

    @DisplayName("When non-existent short code is provided, should throw ShortUrlNotFoundException")
    @Test
    void testGetOriginalUrlByShortCode_whenShortCodeDoesNotExist_shouldThrowShortUrlNotFoundException() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(eq(alias))).thenReturn(null);
        when(shortUrlRepository.findShortUrlByShortCode(eq(alias))).thenReturn(Optional.empty());

        ShortUrlNotFoundException exc = assertThrows(ShortUrlNotFoundException.class, () -> shortUrlService.getOriginalUrlByShortCode(alias));

        verify(shortUrlRepository).findShortUrlByShortCode(eq(alias));
        verify(valueOperations, never()).set(eq(alias), anyString(), any(Duration.class));
        assertEquals("There is no url with such short code or it expired", exc.getMessage());
    }
}
