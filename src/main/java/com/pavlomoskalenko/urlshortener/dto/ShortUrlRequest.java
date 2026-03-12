package com.pavlomoskalenko.urlshortener.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortUrlRequest {
    @NotBlank(message = "originalUrl is required")
    private String originalUrl;

    @Size(min = 1, message = "customAlias cannot be blank")
    private String customAlias;

    @Min(value = 1, message = "expireInDays must be at least 1")
    @Max(value = 365, message = "expireInDays must be at most 365")
    private Integer expireInDays;
}
