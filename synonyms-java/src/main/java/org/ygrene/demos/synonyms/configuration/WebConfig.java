package org.ygrene.demos.synonyms.configuration;

import org.springframework.boot.web.client.*;
import org.springframework.context.annotation.*;
import org.springframework.web.client.*;

@Configuration
public class WebConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
