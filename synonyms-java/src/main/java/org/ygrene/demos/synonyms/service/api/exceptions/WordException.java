package org.ygrene.demos.synonyms.service.api.exceptions;

import lombok.*;
import org.apache.commons.lang3.*;

public abstract class WordException extends Exception {

    protected WordException(final String word) {
        super(createParamArray(word));
    }

    protected WordException(final String word, final Throwable ex) {
        super(createParamArray(word), ex);
    }

    protected WordException(final String word, @NonNull final String message) {
        super(formatStringWithPeriod(message, word));
    }

    protected WordException(final String word, @NonNull final String message, @NonNull final Throwable ex) {
        super(formatStringWithPeriod(message, word), ex);
    }

    private static String formatStringWithPeriod(final String message, final String word) {
        String formatted = StringUtils.defaultString(message);
        if (!StringUtils.endsWith(formatted, ".")) {
            formatted += ".";
        }
        if (!StringUtils.endsWith(formatted, " ")) {
            formatted += " ";
        }
        return formatted + createParamArray(word);
    }

    private static String createParamArray(final String word) {
        return "word[" + word + "]";
    }
}
