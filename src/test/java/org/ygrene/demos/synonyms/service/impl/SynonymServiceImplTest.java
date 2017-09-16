package org.ygrene.demos.synonyms.service.impl;

import io.vavr.control.*;
import lombok.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import org.springframework.http.*;
import org.springframework.web.client.*;
import org.ygrene.demos.synonyms.configuration.*;
import org.ygrene.demos.synonyms.dal.api.*;
import org.ygrene.demos.synonyms.service.api.*;
import org.ygrene.demos.synonyms.service.api.exceptions.*;
import org.ygrene.demos.synonyms.service.model.*;

import java.net.*;
import java.util.*;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.endsWith;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SynonymServiceImplTest {

    private static final String API_TOKEN = UUID.randomUUID().toString();
    private static final String URL = "url";
    private static final String PATH = "path";
    private static final String validWordPattern = "^[A-z]+(-?[A-z]+)*$";
    private URI uri;

    @InjectMocks
    private SynonymServiceImpl synonymService;

    @Mock
    private RequestBuilder requestBuilder;

    @Mock
    private SynonymsConfig synonymsConfig;

    @Mock
    private WordStoreDataService wordStoreDataService;

    @Before
    public void setUp() throws URISyntaxException {
        uri = new URI("http://localhost");

        given(synonymsConfig.getValidWordPattern()).willReturn(validWordPattern);
        given(synonymsConfig.getMashapeToken()).willReturn(API_TOKEN);
        given(synonymsConfig.getMashapeApiWordsUrl()).willReturn(URL);
        given(synonymsConfig.getMashapeApiWordsSynonymsPath()).willReturn(PATH);
        given(requestBuilder.buildRequestPath(eq(URL), endsWith(PATH))).willReturn(uri);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullWordPattern_initialize_throwsException() {
        // Given
        given(synonymsConfig.getValidWordPattern()).willReturn(null);

        // When
        synonymService.initialize();

        // Then - Throws IllegalArgumentException
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidWordPattern_initialize_throwsException() {
        // Given
        given(synonymsConfig.getValidWordPattern()).willReturn("(");

        // When
        synonymService.initialize();

        // Then - Throws IllegalArgumentException
    }

    @Test
    public void validWordPattern_initialize_throwsException() {
        // Given
        given(synonymsConfig.getValidWordPattern()).willReturn(validWordPattern);

        // When
        synonymService.initialize();

        // Then - No Exception Thrown
    }

    @Test(expected = InvalidWordException.class)
    public void emptyWord_getSynonymsForWord_throwsException() throws Exception {
        // Given
        val word = "";

        synonymService.initialize();

        // When
        synonymService.getSynonymsForWord(word);

        // Then - Throws InvalidWordException
    }

    @Test(expected = InvalidWordException.class)
    public void invalidWordAndNoRetrieveDateAndServiceThrowsExDueToMissing_getSynonymsForWord_throwsException() throws Exception {
        // Given
        val word = "invalid word here";

        given(wordStoreDataService.getDateRetrieved(word)).willReturn(Optional.empty());
        given(requestBuilder.getFromService(eq(uri), any(HttpHeaders.class), eq(SynonymsResponse.class)))
                .willReturn(Try.failure(new RestClientException("failed")));

        synonymService.initialize();

        // When
        synonymService.getSynonymsForWord(word);

        // Then - Throws InvalidWordException
    }

    @Test
    public void validWordAndNoRetrieveDateAndEmptySynonymsResponse_getSynonymsForWord_returnsEmptySet() throws Exception {
        // Given
        val word = "retrieve";
        val synonymsResponse = new SynonymsResponse();
        synonymsResponse.setWord(word);

        given(wordStoreDataService.getDateRetrieved(word)).willReturn(Optional.empty());
        given(requestBuilder.getFromService(eq(uri), any(HttpHeaders.class), eq(SynonymsResponse.class)))
                .willReturn(Try.success(synonymsResponse));

        synonymService.initialize();

        // When
        val result = synonymService.getSynonymsForWord(word);

        // Then
        assertThat(result, hasSize(0));
    }

    @Test
    public void validWordAndNoRetrieveDateAndEmptySynonymsResponse_getSynonymsForWord_updatesDatabase() throws Exception {
        // Given
        val word = "retrieve";
        val synonymsResponse = new SynonymsResponse();
        synonymsResponse.setWord(word);

        given(wordStoreDataService.getDateRetrieved(word)).willReturn(Optional.empty());
        given(requestBuilder.getFromService(eq(uri), any(HttpHeaders.class), eq(SynonymsResponse.class)))
                .willReturn(Try.success(synonymsResponse));

        synonymService.initialize();

        // When
        synonymService.getSynonymsForWord(word);

        // Then
        verify(wordStoreDataService).setSynonymsForWord(eq(word), anySetOf(String.class));
    }

    @Test
    public void validWordAndNoRetrieveDateAndValidSynonymsResponse_getSynonymsForWord_updatesDatabase() throws Exception {
        // Given
        val word = "retrieve";
        val synonyms = newHashSet("recover", "repair", "reaquire", "fetch");
        val synonymsResponse = new SynonymsResponse();
        synonymsResponse.setWord(word);
        synonymsResponse.setSynonyms(synonyms);

        given(wordStoreDataService.getDateRetrieved(word)).willReturn(Optional.empty());
        given(requestBuilder.getFromService(eq(uri), any(HttpHeaders.class), eq(SynonymsResponse.class)))
                .willReturn(Try.success(synonymsResponse));

        synonymService.initialize();

        // When
        synonymService.getSynonymsForWord(word);

        // Then
        verify(wordStoreDataService).setSynonymsForWord(eq(word), anySetOf(String.class));
    }

    @Test
    public void validWordAndHasNewerRetrieveDate_getSynonymsForWord_callsToDataStoreForWordsAndReturnsResult() throws Exception {
        // Given
        val word = "retrieve";
        val synonyms = newHashSet("recover", "repair", "reaquire", "fetch");
        val currentDate = new Date();
        val hours = 1;

        given(wordStoreDataService.getDateRetrieved(word)).willReturn(Optional.of(currentDate));
        given(synonymsConfig.getDataRefreshLimitInHours()).willReturn(hours);
        given(wordStoreDataService.findSynonymsForWord(word)).willReturn(synonyms);

        synonymService.initialize();

        // When
        val result = synonymService.getSynonymsForWord(word);

        // Then
        assertThat(result, equalTo(synonyms));
    }
}