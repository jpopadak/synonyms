# synonyms
Uses wordsapi.com to retrieve and store synonyms of words

## Setup

### Database Setup

#### Neo4j
Ensure to setup a docker container for Neo4j: `docker create --name <CONTAINER_NAME> neo4j`
Once the container is created, use the following docker switch to disable Neo4j authentication: `--env=NEO4J_AUTH=none`

```
docker run \
    --publish=7474:7474 --publish=7687:7687 \
    --volume=$HOME/neo4j/data:/data \
    --volume=$HOME/neo4j/logs:/logs \
    --env=NEO4J_AUTH=none \
    <CONTAINER_NAME>
```

#### MySQL
If implementing, a possible table schema would be as follows:

```
CREATE TABLE IF NOT EXISTS WORDS (
    ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    NAME VARCHAR NOT NULL UNIQUE,
    PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS SYNONYMS (
    FROM_WORD BIGINT NOT NULL,
    TO_WORD BIGINT NOT NULL,
    UNIQUE INDEX (FROM_WORD, TO_WORD)
);
```

NOTE:
 1. I did not test these scripts... a good possibility that I have the syntax incorrect.
 2. If doing this, it might be wise to create two mapping rows, one for TO and one for FROM. 


### Application Setup

1. Inside of `application.properties`, you will find a property named `org.ygrene.demos.synonyms.mashape.token`. This property must be set in order to access the `wordsapi.com` API.
2. Run `mvn spring-boot:run` to run the service.

## Code Setup

The code is formatted in a way where one can swap out the different database implementations.

For example, one would need to implement:
 - Add database specific dependencies to the POM file
 - The interface `org.ygrene.demos.synonyms.dal.api.WordStoreDataService`
 - Create a `Repository` for the specific database type
 - Create model entities for the specific database type
 - Modifying the spring configuration to point to the new interface


## Validation

### Neo4j
1. Start the Docker Neo4j container.
2. In your browser, go to `localhost:7474`
3. In the top bar, type the following: `match (n) return n`
4. Click the `Right Arrow`, or hit `Ctrl + Enter` (Windows) or `Command + Enter` (Mac)
5. You can use the `Graph`, `Table`, or `Text` buttons to view the results
6. If you want to clear your database, you can enter the following: `match (n) detach delete n`

## Things to do in the future
1. Use `pact` for the JVM for Contract Management of for external users. This is very important for a micro-service.
2. Use `WireMock` for mocking out the Words API requests to do full `Controller -> Service -> DB` tests.
3. Create valid exception / error pages instead of using the standard Spring Boot error page.
4. Use `Spring Boot Security` in order to make secure endpoints.
5. Add `Spring Eureka Discovery` for micro-service auto discovery.
6. Implement `Spring Cache` mechanism so we don't need to go to the database every time.  

**NOTE: If I had more time, I would have at least done #2 as I have used WireMock before. I would like to try #1, but I have never used Pact. I just read about this last week and it looks promising and well constructed.**
