package org.ygrene.demos.synonyms.dal.model.neo4j;

import lombok.*;
import org.neo4j.ogm.annotation.*;

import java.util.*;

@NodeEntity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "word")
public class Word {

    @GraphId
    private Long id;
    @Index(unique = true, primary = true)
    @Property
    private String word;
    @Relationship(type = "SYNONYM", direction = Relationship.UNDIRECTED)
    private Set<Word> synonyms = new HashSet<>();
    /**
     * The date this word was retrieved from the API.
     * Helps us know if we should remove it from the cache.
     * If null, we did not directly retrieve this word from the API.
     */
    @Property
    private Date dateRetrievedFromApi;

    public Word(String word) {
        this.word = word;
    }
}
