package org.ygrene.demos.synonyms.dal.repositories.neo4j;

import org.springframework.data.neo4j.repository.*;
import org.ygrene.demos.synonyms.dal.model.neo4j.*;

import java.util.*;

public interface WordRepository extends Neo4jRepository<Word, Long> {

    Word findByWord(final String word);

    Set<Word> findAllByWordIn(final Set<String> words);
}
