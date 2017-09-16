package org.ygrene.demos.synonyms.service.impl;

import io.vavr.control.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;
import org.springframework.web.client.*;
import org.springframework.web.util.*;
import org.ygrene.demos.synonyms.service.api.*;

import java.net.*;

@Component
public class RequestBuilderImpl implements RequestBuilder {

    private final RestTemplate restTemplate;

    public RequestBuilderImpl(
            final RestTemplate restTemplate
    ) {
        this.restTemplate = restTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI buildRequestPath(@NonNull final String baseUrl) {
        return buildRequestPath(baseUrl, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI buildRequestPath(@NonNull final String baseUrl, @NonNull final String path) {
        return buildRequestPath(baseUrl, path, new LinkedMultiValueMap<>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI buildRequestPath(@NonNull final String baseUrl,
                                @NonNull final String path,
                                @NonNull final MultiValueMap<String, String> queryParams) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path(path)
                .queryParams(queryParams)
                .build()
                .toUri();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Try<T> getFromService(@NonNull final URI uri,
                                     @NonNull HttpHeaders headers,
                                     @NonNull Class<T> resultType) {
        // Setup the headers for our request
        final RequestEntity<T> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);

        return Try.of(() -> restTemplate.exchange(requestEntity, resultType))
                .map(HttpEntity::getBody);
    }
}
