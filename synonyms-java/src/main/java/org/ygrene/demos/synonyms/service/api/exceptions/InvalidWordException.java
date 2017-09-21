package org.ygrene.demos.synonyms.service.api.exceptions;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.regex.*;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidWordException extends WordException {

    private static final String INVALID_WORD_MESSAGE = "Requested word is invalid or does not conform to the valid word pattern.";

    public InvalidWordException(final String word, @NonNull final Pattern wordPattern) {
        super(word, combineMessageAndPattern(wordPattern));
    }

    public InvalidWordException(final String word, @NonNull final Pattern wordPattern, final Throwable ex) {
        super(word, combineMessageAndPattern(wordPattern), ex);
    }

    public InvalidWordException(final String word, @NonNull final String message) {
        super(word, message);
    }

    public InvalidWordException(final String word, @NonNull final String message, @NonNull final Throwable ex) {
        super(word, message, ex);
    }

    private static String combineMessageAndPattern(final Pattern pattern) {
        return InvalidWordException.INVALID_WORD_MESSAGE + " pattern[" + pattern.pattern() + "]";
    }
}
