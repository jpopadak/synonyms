package org.ygrene.demos.synonyms.dal.impl.neo4j;

import lombok.*;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.*;
import org.springframework.transaction.annotation.*;
import org.ygrene.demos.synonyms.dal.*;
import org.ygrene.demos.synonyms.dal.api.*;
import org.ygrene.demos.synonyms.dal.model.neo4j.*;
import org.ygrene.demos.synonyms.dal.repositories.neo4j.*;

import java.util.*;
import java.util.stream.*;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional // Allow tests to run in their own transactions
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Neo4jITConfiguration.class)
public class WordStoreDataServiceIT {

    @Autowired
    private WordStoreDataService wordStoreDataService;

    @Autowired
    private WordRepository wordRepository;

    @Test
    public void wordDoesNotExist_getDateRetrieved_returnsEmptyOptional() {
        // Given
        val word = "word";

        // When
        val result = wordStoreDataService.getDateRetrieved(word);

        // Then
        assertThat(result, notNullValue());
        assertThat(result.isPresent(), equalTo(false));
    }

    @Test
    public void wordExistsButNotRetrieved_getDateRetrieved_returnsEmptyOptional() {
        // Given
        val word = "word";

        val wordEntity = new Word(word.toUpperCase());
        wordRepository.save(wordEntity);

        // When
        val result = wordStoreDataService.getDateRetrieved(word);

        // Then
        assertThat(result, notNullValue());
        assertThat(result.isPresent(), equalTo(false));
    }

    @Test
    public void wordExistsAndWasRetrieved_getDateRetrieved_returnsOptionalWithDate() {
        // Given
        val word = "word";
        val dateRetrieved = new Date();
        val wordEntity = new Word(word.toUpperCase());
        wordEntity.setDateRetrievedFromApi(dateRetrieved);

        wordRepository.save(wordEntity);

        // When
        val result = wordStoreDataService.getDateRetrieved(word);

        // Then
        assertThat(result, notNullValue());
        assertThat(result.orElse(null), equalTo(dateRetrieved));
    }

    @Test
    public void wordDoesNotExist_findSynonymsForWord_returnsEmptySet() {
        // Given
        val word = "word";

        // When
        val result = wordStoreDataService.findSynonymsForWord(word);

        // Then
        assertThat(result, empty());
    }

    @Test
    public void wordExistsButNotRetrievedFromApi_findSynonymsForWord_returnsEmptySet() {
        // Given
        val word = "word";
        val wordEntity = new Word(word.toUpperCase());

        wordRepository.save(wordEntity);

        // When
        val result = wordStoreDataService.findSynonymsForWord(word);

        // Then
        assertThat(result, empty());
    }

    @Test
    public void wordExistsAndRetrievedFromApiButNullSynonyms_findSynonymsForWord_returnsEmptySet() {
        // Given
        val word = "word";
        val dateRetrieved = new Date();
        val wordEntity = new Word(word.toUpperCase());
        wordEntity.setDateRetrievedFromApi(dateRetrieved);

        wordRepository.save(wordEntity);

        // When
        val result = wordStoreDataService.findSynonymsForWord(word);

        // Then
        assertThat(result, empty());
    }

    @Test
    public void wordExistsAndRetrievedFromApiWithNoSynonyms_findSynonymsForWord_returnsEmptySet() {
        // Given
        val word = "word";
        val dateRetrieved = new Date();
        val wordEntity = new Word(word.toUpperCase());
        wordEntity.setDateRetrievedFromApi(dateRetrieved);
        wordEntity.setSynonyms(newHashSet());

        wordRepository.save(wordEntity);

        // When
        val result = wordStoreDataService.findSynonymsForWord(word);

        // Then
        assertThat(result, empty());
    }

    @Test
    public void wordExistsAndRetrievedFromApiWithSynonyms_findSynonymsForWord_returnsSetOfSynonyms() {
        // Given
        val word = "retrieve";
        val synonyms = newHashSet("recover", "repair", "reaquire", "fetch");
        val synonymWordEntities = synonyms.stream().map(Word::new).collect(toSet());
        val dateRetrieved = new Date();
        val wordEntity = new Word(word.toUpperCase());
        wordEntity.setDateRetrievedFromApi(dateRetrieved);
        wordEntity.setSynonyms(synonymWordEntities);

        wordRepository.save(wordEntity);

        // When
        val result = wordStoreDataService.findSynonymsForWord(word);

        // Then
        assertThat(result, containsInAnyOrder(synonyms.toArray()));
    }

    @Test
    public void wordDoesNotExistAndNoSynonyms_setSynonymsForWord_savesWord() {
        // Given
        val word = "retrieve";
        final Set<String> synonyms = emptySet();

        // When
        wordStoreDataService.setSynonymsForWord(word, synonyms);

        // Then
        val savedWord = wordRepository.findByWord(word.toUpperCase());
        assertThat(savedWord, notNullValue());
    }

    @Test
    public void wordDoesNotExistAndNoSynonyms_setSynonymsForWord_savesWordWithNoSynonyms() {
        // Given
        val word = "retrieve";
        final Set<String> synonyms = emptySet();

        // When
        wordStoreDataService.setSynonymsForWord(word, synonyms);

        // Then
        val savedWord = wordRepository.findByWord(word.toUpperCase());
        assertThat(savedWord, notNullValue());
        assertThat(savedWord.getSynonyms(), empty());
    }

    @Test
    public void wordDoesNotExistAndNoSynonyms_setSynonymsForWord_savesWordWithProperWord() {
        // Given
        val word = "retrieve";
        final Set<String> synonyms = emptySet();

        // When
        wordStoreDataService.setSynonymsForWord(word, synonyms);

        // Then
        val savedWord = wordRepository.findByWord(word.toUpperCase());
        assertThat(savedWord, notNullValue());
        assertThat(savedWord.getWord(), equalTo(word.toUpperCase()));
    }

    @Test
    public void wordDoesNotExistAndNoSynonyms_setSynonymsForWord_savesWordWithDate() {
        // Given
        val word = "retrieve";
        final Set<String> synonyms = emptySet();

        // When
        wordStoreDataService.setSynonymsForWord(word, synonyms);

        // Then
        val savedWord = wordRepository.findByWord(word.toUpperCase());
        assertThat(savedWord, notNullValue());
        assertThat(savedWord.getDateRetrievedFromApi(), notNullValue());
    }

    @Test
    public void wordExistAndNoSynonyms_setSynonymsForWord_savesWordWithDate() {
        // Given
        val word = "retrieve";
        final Set<String> synonyms = emptySet();

        val wordEntity = new Word(word.toUpperCase());
        wordEntity.setSynonyms(emptySet());
        wordRepository.save(wordEntity);

        // When
        wordStoreDataService.setSynonymsForWord(word, synonyms);

        // Then
        val savedWord = wordRepository.findByWord(word.toUpperCase());
        assertThat(savedWord, notNullValue());
        assertThat(savedWord.getDateRetrievedFromApi(), notNullValue());
    }

    @Test
    public void wordExistAndNoSynonymsAddingNewSynonyms_setSynonymsForWord_savesWordWithNewSynonyms() {
        // Given
        val word = "retrieve";
        val synonyms = newHashSet("recover", "repair", "reaquire", "fetch");

        val wordEntity = new Word(word.toUpperCase());
        wordEntity.setSynonyms(emptySet());
        wordRepository.save(wordEntity);

        // When
        wordStoreDataService.setSynonymsForWord(word, synonyms);

        // Then
        val savedWord = wordRepository.findByWord(word.toUpperCase());
        assertThat(savedWord, notNullValue());
        assertThat(savedWord.getSynonyms(), notNullValue());
        val savedSynonyms = savedWord.getSynonyms().stream().map(Word::getWord).map(String::toUpperCase).collect(toSet());
        assertThat(savedSynonyms, containsInAnyOrder("REAQUIRE", "FETCH", "RECOVER", "REPAIR"));
    }

    @Test
    public void wordExistAndSomeSynonymsAddingNewSynonyms_setSynonymsForWord_savesWordWithNewAndOldSynonyms() {
        // Given
        val word = "retrieve";
        val synonyms = newHashSet("recover", "repair", "reaquire", "fetch");

        val wordEntity = new Word(word.toUpperCase());
        val synonymWordEntities = Stream.of("reaquire", "fetch").map(String::toUpperCase).map(Word::new).collect(toSet());
        wordEntity.setSynonyms(synonymWordEntities);
        wordRepository.save(wordEntity);

        // When
        wordStoreDataService.setSynonymsForWord(word, synonyms);

        // Then
        val savedWord = wordRepository.findByWord(word.toUpperCase());
        assertThat(savedWord, notNullValue());
        assertThat(savedWord.getSynonyms(), notNullValue());
        val savedSynonyms = savedWord.getSynonyms().stream().map(Word::getWord).map(String::toUpperCase).collect(toSet());
        assertThat(savedSynonyms, containsInAnyOrder("REAQUIRE", "FETCH", "RECOVER", "REPAIR"));
    }
}