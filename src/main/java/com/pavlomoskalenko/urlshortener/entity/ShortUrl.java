package com.pavlomoskalenko.urlshortener.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "short_url")
@Data
@NoArgsConstructor
public class ShortUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Original Url is required")
    @Column(name = "original_url", nullable = false)
    private String originalUrl;

    @Column(name = "short_code")
    private String shortCode;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    public ShortUrl( String originalUrl, String shortCode, LocalDateTime expirationDate) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.expirationDate = expirationDate;
    }

    public boolean hasExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDateTime.now());
    }

}
