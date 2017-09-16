package org.ygrene.demos.synonyms.service.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SynonymsResponse {

    private String word;
    private Set<String> synonyms;
}
