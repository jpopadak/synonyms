package org.ygrene.demos.synonyms.configuration;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.data.neo4j.transaction.*;

@org.springframework.context.annotation.Configuration
public class Neo4jConfig {

    @Value("${org.ygrene.demos.synonyms.database.neo4j.uri:http://localhost:7474}")
    private String neo4jUri;

    @Bean
    public Configuration configuration() {
        Configuration configuration = new Configuration();
        configuration.driverConfiguration().setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver");
        configuration.driverConfiguration().setURI(neo4jUri);
        return configuration;
    }

    @Bean
    public SessionFactory sessionFactory(Configuration configuration) {
        // Specify the base package for the entities
        return new SessionFactory(configuration, "org.ygrene.demos.synonyms.dal.model.neo4j");
    }

    @Bean
    public Neo4jTransactionManager transactionManager(
            SessionFactory sessionFactory
    ) {
        return new Neo4jTransactionManager(sessionFactory);
    }
}
