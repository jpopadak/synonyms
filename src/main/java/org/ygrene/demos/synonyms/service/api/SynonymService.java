package org.ygrene.demos.synonyms.service.api;

import org.ygrene.demos.synonyms.service.api.exceptions.*;

import java.util.*;

public interface SynonymService {

    /**
     * Gets a set of synonyms for the specified word
     *
     * @param word A word to search for synonyms for.
     *             Can be any casing, can have one or more dashes <code>-</code> in the word.
     *             Cannot be empty or null
     * @return A non-null set of strings, can be empty, can be upper-case, lower-case, or mixed casing.
     * @throws WordException If input is invalid or service issues occur
     */
    Set<String> getSynonymsForWord(final String word) throws WordException;
}
