package org.ygrene.demos.synonyms.service.impl;

import io.vavr.control.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.apache.commons.collections4.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.ygrene.demos.synonyms.configuration.*;
import org.ygrene.demos.synonyms.dal.api.*;
import org.ygrene.demos.synonyms.service.api.*;
import org.ygrene.demos.synonyms.service.api.exceptions.*;
import org.ygrene.demos.synonyms.service.model.*;

import javax.annotation.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
@Component
public class SynonymServiceImpl implements SynonymService {

    private static final String HEADER_X_MASHAPE_KEY = "X-Mashape-Key";
    private final RequestBuilder requestBuilder;
    private final SynonymsConfig synonymsConfig;
    private final WordStoreDataService wordStoreDataService;
    private Pattern validWordPattern;

    public SynonymServiceImpl(
            final RequestBuilder requestBuilder,
            final SynonymsConfig synonymsConfig,
            final WordStoreDataService wordStoreDataService
    ) {
        this.requestBuilder = requestBuilder;
        this.synonymsConfig = synonymsConfig;
        this.wordStoreDataService = wordStoreDataService;
    }

    @PostConstruct
    public void initialize() {
        final String wordPattern = this.synonymsConfig.getValidWordPattern();
        try {
            validWordPattern = Pattern.compile(wordPattern);
        } catch (NullPointerException | IllegalArgumentException ex) {
            throw new IllegalArgumentException("Failed to compile pattern for valid words", ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getSynonymsForWord(@NonNull final String word) throws WordException {
        if (isEmpty(word) || !validWordPattern.matcher(word).matches()) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Returning empty value due to invalid or empty word. word[" + word + "]");
            }
            throw new InvalidWordException(word, validWordPattern);
        }

        if (!shouldForceRetrieve(word)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Returning data store values. word[" + word + "]");
            }
            return wordStoreDataService.findSynonymsForWord(word);
        }

        final Try<Set<String>> requestAttempt = requestFromService(word);
        if (requestAttempt.isFailure()) {
            final Throwable failureReason = requestAttempt.failed().get();
            throw new WordNotFoundException(word, "Failed to find word", failureReason);
        }

        final Set<String> synonyms = requestAttempt.get();
        wordStoreDataService.setSynonymsForWord(word, synonyms);
        return synonyms;
    }

    private boolean shouldForceRetrieve(@NonNull String word) {
        final Date dateRetrieved = wordStoreDataService.getDateRetrieved(word).orElse(null);
        if (dateRetrieved == null) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Date retrieved is null. word[" + word + "]");
            }
            return true;
        }

        final int refreshLimit = synonymsConfig.getDataRefreshLimitInHours(); // Never Negative
        final Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR, -refreshLimit); // Subtract the refresh limit
        boolean shouldForceRetrieve = instance.getTime().after(dateRetrieved);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Date retrieved and checked limit. word[" + word + "], dateRetrieved[" + dateRetrieved + "], refreshLimitTime[" + refreshLimit + "]");
        }
        return shouldForceRetrieve;
    }

    private Try<Set<String>> requestFromService(@NonNull String word) {
        final String baseUrl = synonymsConfig.getMashapeApiWordsUrl();
        final String postfixPath = synonymsConfig.getMashapeApiWordsSynonymsPath();
        final String path = word + postfixPath; // word will be URL encoded in `buildRequestPath`

        final URI compiledUri = requestBuilder.buildRequestPath(baseUrl, path);
        return getResponseFromService(compiledUri, SynonymsResponse.class)
                .map(SynonymsResponse::getSynonyms)
                .map(SetUtils::emptyIfNull);
    }

    private <T> Try<T> getResponseFromService(@NonNull URI uri, @NonNull Class<T> classType) {
        final String apiToken = synonymsConfig.getMashapeToken();

        // Setup the headers for the request
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(singletonList(MediaType.APPLICATION_JSON));
        headers.put(HEADER_X_MASHAPE_KEY, singletonList(apiToken));
        headers.put("User-Agent", singletonList("Java"));

        return requestBuilder.getFromService(uri, headers, classType);
    }
}
