package org.ygrene.demos.synonyms.dal.api;

import lombok.*;

import java.util.*;

public interface WordStoreDataService {

    Optional<Date> getDateRetrieved(@NonNull String word);

    Set<String> findSynonymsForWord(final String word);

    void setSynonymsForWord(final String word, Set<String> synonyms);
}
