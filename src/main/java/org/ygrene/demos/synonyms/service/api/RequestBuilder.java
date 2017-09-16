package org.ygrene.demos.synonyms.service.api;

import io.vavr.control.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.util.*;

import java.net.*;

public interface RequestBuilder {

    /**
     * Builds a request
     *
     * @param baseUrl A base url for the URI
     * @return A URI built with token and query parameters
     */
    URI buildRequestPath(@NonNull String baseUrl);

    /**
     * Builds a request
     *
     * @param baseUrl A base url for the URI
     * @param path    A path for the URI
     * @return A URI built with token and query parameters
     */
    URI buildRequestPath(@NonNull String baseUrl, @NonNull String path);

    /**
     * Builds a request
     *
     * @param baseUrl     A base url for the URI
     * @param path        A path for the URI
     * @param queryParams Query parameters to insert into the URI
     * @return A URI built with token and query parameters
     */
    URI buildRequestPath(@NonNull String baseUrl, @NonNull String path, @NonNull MultiValueMap<String, String> queryParams);

    /**
     * Retrieves an object from a service
     *
     * @param <T>        The type of object to map the results to
     * @param uri        URI to do a {@link org.springframework.http.HttpMethod#GET}
     * @param headers    Headers to apply to the request
     * @param resultType The class type of the requested object
     * @return An optional of empty, or the object requested
     */
    <T> Try<T> getFromService(@NonNull URI uri, @NonNull HttpHeaders headers, @NonNull Class<T> resultType);
}
