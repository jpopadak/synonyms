package org.ygrene.demos.synonyms.controller;

import lombok.*;
import org.springframework.web.bind.annotation.*;
import org.ygrene.demos.synonyms.service.api.*;
import org.ygrene.demos.synonyms.service.api.exceptions.*;

import java.util.*;

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
     * @throws WordException If input is invalid or service issues occur
     */
    @GetMapping("synonyms")
    public Set<String> getSynonyms(@RequestParam @NonNull String word) throws WordException {
        return synonymService.getSynonymsForWord(word);
    }
}
