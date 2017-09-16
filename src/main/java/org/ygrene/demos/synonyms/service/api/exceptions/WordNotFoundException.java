package org.ygrene.demos.synonyms.service.api.exceptions;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class WordNotFoundException extends WordException {

    private static final String WORD_NOT_FOUND_MESSAGE = "Could not find the requested word.";

    public WordNotFoundException(final String word) {
        super(word, WORD_NOT_FOUND_MESSAGE);
    }

    public WordNotFoundException(final String word, final Throwable ex) {
        super(word, WORD_NOT_FOUND_MESSAGE, ex);
    }

    public WordNotFoundException(final String word, @NonNull final String message) {
        super(word, message);
    }

    public WordNotFoundException(final String word, @NonNull final String message, @NonNull final Throwable ex) {
        super(word, message, ex);
    }
}
