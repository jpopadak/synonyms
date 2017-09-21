package org.ygrene.demos.synonyms.dal;

import org.neo4j.ogm.session.*;
import org.springframework.boot.test.context.*;
import org.springframework.context.annotation.*;
import org.springframework.data.neo4j.repository.config.*;
import org.springframework.data.neo4j.transaction.*;
import org.springframework.test.context.*;
import org.springframework.test.context.support.*;
import org.springframework.test.context.transaction.*;

@TestConfiguration
@EnableNeo4jRepositories("org.ygrene.demos.synonyms.dal.repositories.neo4j")
@ComponentScan("org.ygrene.demos.synonyms.dal")
@TestExecutionListeners(value = {TransactionalTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
public class Neo4jITConfiguration {

    @Bean
    public SessionFactory sessionFactory() {
        // with domain entity base package(s)
        return new SessionFactory("org.ygrene.demos.synonyms.dal.model");
    }

    @Bean
    public Neo4jTransactionManager transactionManager() {
        return new Neo4jTransactionManager(sessionFactory());
    }
}
