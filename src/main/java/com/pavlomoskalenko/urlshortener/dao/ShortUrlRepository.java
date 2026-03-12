package com.pavlomoskalenko.urlshortener.dao;

import com.pavlomoskalenko.urlshortener.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    boolean existsShortUrlByShortCode(String shortCode);
    Optional<ShortUrl> findShortUrlByShortCode(String shortCode);
}
