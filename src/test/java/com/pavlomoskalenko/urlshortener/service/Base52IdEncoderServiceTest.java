package com.pavlomoskalenko.urlshortener.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base52IdEncoderServiceTest {

    private final Base52IdEncoderService encoderService = new Base52IdEncoderServiceImpl();

    @DisplayName("Test id encoding [id, expectedEncodedId]")
    @ParameterizedTest
    @CsvSource({"0, a", "1, b", "25, z", "51, Z", "52, ba"})
    void testEncode_whenIdProvided_shouldReturnExpectedEncodedId(Long id, String expectedEncodedId) {
        String encodedId = encoderService.encode(id);
        assertEquals(expectedEncodedId, encodedId);
    }

    @DisplayName("Test id decoding [encodedId, expectedDecodedId]")
    @ParameterizedTest
    @CsvSource({"a, 0", "b, 1", "z, 25", "Z, 51", "ba, 52"})
    void testDecode_whenEncodedIdProvided_shouldReturnExpectedDecodedId(String encodedId, Long expectedDecodedId) {
        Long decodedId = encoderService.decode(encodedId);
        assertEquals(expectedDecodedId, decodedId);
    }
}