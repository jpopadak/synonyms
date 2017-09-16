package org.ygrene.demos.synonyms.dal.impl.neo4j;

import lombok.*;
import org.apache.commons.collections4.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.ygrene.demos.synonyms.dal.api.*;
import org.ygrene.demos.synonyms.dal.model.neo4j.*;
import org.ygrene.demos.synonyms.dal.repositories.neo4j.*;

import java.util.*;

import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

/**
 * This class stores all values in Neo4j as Upper Cased words
 */
@Component
public class WordStoreDataServiceImpl implements WordStoreDataService {

    private final WordRepository wordRepository;

    public WordStoreDataServiceImpl(
            final WordRepository wordRepository
    ) {
        this.wordRepository = wordRepository;
    }

    @Override
    @Transactional
    public Optional<Date> getDateRetrieved(@NonNull final String word) {
        final String upperWord = word.toUpperCase();
        return Optional.ofNullable(wordRepository.findByWord(upperWord))
                .map(Word::getDateRetrievedFromApi);
    }

    @Override
    @Transactional
    public Set<String> findSynonymsForWord(@NonNull final String word) {
        final String upperWord = word.toUpperCase();
        final Word foundWord = wordRepository.findByWord(upperWord);
        if (foundWord == null) {
            return Collections.emptySet();
        }

        // If we have not retrieved this from the api, do not return its values
        if (foundWord.getDateRetrievedFromApi() == null) {
            return Collections.emptySet();
        }

        final Set<Word> synonyms = foundWord.getSynonyms();
        if (isEmpty(synonyms)) {
            return Collections.emptySet();
        }
        return synonyms.stream()
                .map(Word::getWord)
                .collect(toSet());
    }

    @Override
    @Transactional
    public void setSynonymsForWord(@NonNull final String word, @NonNull final Set<String> synonyms) {
        final String upperWord = word.toUpperCase();
        final Word wordToUpdate = Optional.ofNullable(wordRepository.findByWord(upperWord))
                .orElse(new Word(upperWord));

        final Set<String> upperSynonyms = synonyms.stream().map(String::toUpperCase).collect(toSet());
        final Set<Word> foundSynonyms = wordRepository.findAllByWordIn(upperSynonyms);
        final Set<Word> differences = createMissingWords(upperSynonyms, foundSynonyms);

        final Set<Word> allSynonyms = SetUtils.union(foundSynonyms, differences).toSet();
        wordToUpdate.setSynonyms(allSynonyms);
        wordToUpdate.setDateRetrievedFromApi(new Date()); // Always update the time we updated this value
        wordRepository.save(wordToUpdate);
    }

    private Set<Word> createMissingWords(@NonNull Set<String> synonyms, @NonNull Set<Word> foundSynonyms) {
        Set<String> databaseWords = foundSynonyms.stream()
                .map(Word::getWord)
                .collect(toSet());

        return SetUtils.difference(synonyms, databaseWords)
                .stream()
                .map(Word::new)
                .collect(toSet());
    }
}
