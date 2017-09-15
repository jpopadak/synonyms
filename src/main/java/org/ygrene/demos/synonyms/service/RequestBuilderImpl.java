package org.ygrene.demos.synonyms.service;

import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.ygrene.demos.synonyms.service.api.RequestBuilder;

import java.net.URI;
import java.util.Optional;

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
    public URI buildRequestPath(@NonNull final String baseUrl, @NonNull final String path, @NonNull final MultiValueMap<String, String> queryParams) {
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
    public <T> Optional<T> getFromService(@NonNull final URI uri, @NonNull HttpHeaders headers, @NonNull Class<T> resultType) {
        // Setup the headers for our request
        final RequestEntity<T> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);
        final ResponseEntity<T> responseEntity = restTemplate.exchange(requestEntity, resultType);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return Optional.ofNullable(responseEntity.getBody());
        }
        return Optional.empty();
    }
}
