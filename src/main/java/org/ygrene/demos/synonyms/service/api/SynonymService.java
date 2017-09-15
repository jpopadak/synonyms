package org.ygrene.demos.synonyms.service.api;

import java.util.Set;

public interface SynonymService {

    /**
     * Gets a set of synonyms for the specified word
     *
     * @param word A word to search for synonyms for
     * @return A non-null set of strings, can be empty
     */
    Set<String> getSynonymsForWord(final String word);
}
