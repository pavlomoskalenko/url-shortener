package com.pavlomoskalenko.urlshortener.service;

public interface Base52IdEncoderService {
    String encode(Long id);
    Long decode(String encodedId);
}
