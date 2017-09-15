package org.ygrene.demos.synonyms.controller;

import lombok.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ygrene.demos.synonyms.service.api.SynonymService;

import java.util.Set;

@RestController
public class WordsController {

    private final SynonymService synonymService;

    public WordsController(
            final SynonymService synonymService
    ) {
        this.synonymService = synonymService;
    }

    /**
     * Gets a set of synonyms for the specified word
     *
     * @param word A word to search for synonyms for
     * @return A non-null set of strings, can be empty
     */
    @GetMapping("synonyms")
    public Set<String> getSynonyms(@RequestParam @NonNull String word) {
        return synonymService.getSynonymsForWord(word);
    }
}
