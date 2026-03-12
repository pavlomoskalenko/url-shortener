package com.pavlomoskalenko.urlshortener.service;

import org.springframework.stereotype.Service;

@Service
public class Base52IdEncoderServiceImpl implements Base52IdEncoderService {

    private static final int BASE = 52;

    @Override
    public String encode(Long id) {
        if (id == 0) {
            return String.valueOf(convertReminderToLetter(0));
        }

        long tempId = id;
        StringBuilder resultString = new StringBuilder();
        while (tempId != 0) {
            char letter = convertReminderToLetter((int) (tempId % BASE));
            resultString.append(letter);

            tempId = tempId / BASE;
        }

        return resultString.reverse().toString();
    }

    @Override
    public Long decode(String encodedId) {
        long resultId = 0L;

        char[] chars = encodedId.toCharArray();
        for (int i = chars.length - 1; i >= 0; i--) {
            resultId += (long) (Math.pow(BASE, chars.length - 1 - i) * convertLetterToReminder(chars[i]));
        }

        return resultId;
    }

    private char convertReminderToLetter(int reminder) {
        return (char) ((reminder <= BASE / 2 ? 'a' : 'A') + (reminder % (BASE / 2)));
    }

    private int convertLetterToReminder(char letter) {
        return (letter >= 'a' ? letter - 'a' : letter - 'A' + BASE / 2);
    }
}
