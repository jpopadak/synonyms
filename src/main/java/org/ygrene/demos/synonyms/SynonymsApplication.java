package org.ygrene.demos.synonyms;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.data.neo4j.repository.config.*;

@SpringBootApplication
@EnableNeo4jRepositories(basePackages = "org.ygrene.demos.synonyms.dal.repositories.neo4j")
public class SynonymsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SynonymsApplication.class, args);
    }
}
