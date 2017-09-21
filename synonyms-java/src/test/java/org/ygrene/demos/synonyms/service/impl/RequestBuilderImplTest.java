package org.ygrene.demos.synonyms.service.impl;

import com.google.common.collect.*;
import io.vavr.control.*;
import lombok.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import org.springframework.http.*;
import org.springframework.util.*;
import org.springframework.web.client.*;

import java.net.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class RequestBuilderImplTest {

    @InjectMocks
    private RequestBuilderImpl requestBuilder;

    @Mock
    private RestTemplate restTemplate;

    @Test
    public void validBaseUrl_buildRequestPath_returnsSameUrl() {
        // Given
        val baseUrl = "http://localhost/randomPath";

        // When
        val result = requestBuilder.buildRequestPath(baseUrl);

        // Then
        assertThat(result, notNullValue());
        assertThat(result, hasToString(baseUrl));
    }

    @Test
    public void validBaseUrlAndPath_buildRequestPath_returnsBaseUrlWithPathAppended() {
        // Given
        val baseUrl = "https://wordsapiv1.p.mashape.com/words/";
        val path = "/synonyms";

        // When
        val result = requestBuilder.buildRequestPath(baseUrl, path);

        // Then
        assertThat(result, notNullValue());
        assertThat(result, hasToString(both(startsWith(baseUrl)).and(endsWith(path))));
    }

    @Test
    public void validBaseUrlAndPathQueryParams_buildRequestPath_returnsBaseUrlWithPathAppendedAndBothQueryParams() {
        // Given
        val baseUrl = "https://wordsapiv1.p.mashape.com/words/";
        val path = "/synonyms";
        val param1Name = "param1Name";
        val param1Value1 = "param1Value1";
        val param1Value2 = "param1Value2";
        val param2Name = "param2Name";
        val param2Value = "param2Value";
        val queryParams = new LinkedMultiValueMap<String, String>();
        queryParams.put(param1Name, ImmutableList.of(param1Value1, param1Value2));
        queryParams.put(param2Name, ImmutableList.of(param2Value));

        // When
        val result = requestBuilder.buildRequestPath(baseUrl, path, queryParams);

        // Then
        assertThat(result, notNullValue());
        assertThat(result, hasToString(both(startsWith(baseUrl)).and(containsString(path))));
        assertThat(result, hasToString(containsString(param1Name + "=" + param1Value1)));
        assertThat(result, hasToString(containsString(param1Name + "=" + param1Value2)));
        assertThat(result, hasToString(containsString(param2Name + "=" + param2Value)));
    }

    @Test
    public void validUriHeadersAndResultTypeButServiceError_getFromService_returnsEmptyOptional() throws URISyntaxException {
        // Given
        val uri = new URI("localhost:8080");
        val headers = new HttpHeaders();
        val resultType = String.class;

        given(restTemplate.exchange(Matchers.any(RequestEntity.class), eq(resultType)))
                .willThrow(RestClientException.class);

        // When
        val result = requestBuilder.getFromService(uri, headers, resultType);

        // Then
        assertThat(result, notNullValue());
        assertThat(result, instanceOf(Try.Failure.class));
    }

    @Test
    public void validUriHeadersAndResultType_getFromService_returnsOptionalWithValue() throws URISyntaxException {
        // Given
        val uri = new URI("localhost:8080");
        val headers = new HttpHeaders();
        val resultType = String.class;
        val resultBody = "This is the body";

        val mockResponseEntity = (ResponseEntity<String>) mock(ResponseEntity.class);

        given(restTemplate.exchange(Matchers.any(RequestEntity.class), eq(resultType)))
                .willReturn(mockResponseEntity);
        given(mockResponseEntity.getStatusCode()).willReturn(HttpStatus.OK);
        given(mockResponseEntity.getBody()).willReturn(resultBody);

        // When
        val result = requestBuilder.getFromService(uri, headers, resultType);

        // Then
        assertThat(result, notNullValue());
        assertThat(result, instanceOf(Try.Success.class));
        assertThat(result.get(), equalTo(resultBody));
    }
}