package org.ygrene.demos.synonyms.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.ygrene.demos.synonyms.configuration.SynonymsConfig;
import org.ygrene.demos.synonyms.service.api.RequestBuilder;
import org.ygrene.demos.synonyms.service.api.SynonymService;
import org.ygrene.demos.synonyms.service.model.SynonymsResponse;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.singletonList;

@Slf4j
@Component
public class SynonymServiceImpl implements SynonymService {

    private static final String HEADER_X_MASHAPE_KEY = "X-Mashape-Key";
    private final RequestBuilder requestBuilder;
    private final SynonymsConfig synonymsConfig;

    public SynonymServiceImpl(
            final RequestBuilder requestBuilder,
            final SynonymsConfig synonymsConfig
    ) {
        this.requestBuilder = requestBuilder;
        this.synonymsConfig = synonymsConfig;
    }

    @Override
    public Set<String> getSynonymsForWord(@NonNull final String word) {
        final String baseUrl = synonymsConfig.getMashapeApiWordsUrl();
        final String postfixPath = synonymsConfig.getMashapeApiWordsSynonymsPath();
        final String path = word + postfixPath;

        final URI compiledUri = requestBuilder.buildRequestPath(baseUrl, path);

        try {
            return getResponseFromService(compiledUri, SynonymsResponse.class)
                    .map(SynonymsResponse::getSynonyms)
                    .orElse(Collections.emptySet());
        } catch (RestClientException ex) {
            LOGGER.warn("Failed to retrieve synonyms.\nword[" + word + "] url[" + compiledUri.toString() + "]", ex);
            return Collections.emptySet();
        }
    }

    private <T> Optional<T> getResponseFromService(@NonNull URI uri, @NonNull Class<T> classType) {
        final String apiToken = synonymsConfig.getMashapeToken();

        // Setup the headers for the request
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(singletonList(MediaType.APPLICATION_JSON));
        headers.put(HEADER_X_MASHAPE_KEY, singletonList(apiToken));
        headers.put("User-Agent", singletonList("Swagger/Java/0.0.5/#DOCAMTO_RELEASE_TYPE#"));

        return requestBuilder.getFromService(uri, headers, classType);
    }
}
