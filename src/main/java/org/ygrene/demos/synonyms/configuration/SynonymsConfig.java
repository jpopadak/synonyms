package org.ygrene.demos.synonyms.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class SynonymsConfig {

    @Value("${org.ygrene.demos.synonyms.mashape.api.words.url:https://wordsapiv1.p.mashape.com/words/}")
    private String mashapeApiWordsUrl;

    @Value("${org.ygrene.demos.synonyms.mashape.api.words.synonyms.path:/synonyms}")
    private String mashapeApiWordsSynonymsPath;

    @Value("${org.ygrene.demos.synonyms.mashape.token}")
    private String mashapeToken;
}
