package org.ygrene.demos.synonyms.configuration;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Getter
@Configuration
public class SynonymsConfig {

    @Value("${org.ygrene.demos.synonyms.mashape.api.words.url:https://wordsapiv1.p.mashape.com/words/}")
    private String mashapeApiWordsUrl;

    @Value("${org.ygrene.demos.synonyms.mashape.api.words.synonyms.path:/synonyms}")
    private String mashapeApiWordsSynonymsPath;

    @Value("${org.ygrene.demos.synonyms.mashape.token}")
    private String mashapeToken;

    @Value("${org.ygrene.demos.synonyms.validWordPattern:^[A-z]+((-| )?[A-z]+)*$}")
    private String validWordPattern;

    @Value("${org.ygrene.demos.synonyms.data.refresh.hours:12}")
    private int dataRefreshLimitInHours;

    /**
     * Number of hours that a word can be good for in our database
     *
     * @return A never negative number
     */
    public int getDataRefreshLimitInHours() {
        if (dataRefreshLimitInHours < 0) {
            dataRefreshLimitInHours = -dataRefreshLimitInHours;
        }
        return dataRefreshLimitInHours;
    }
}
