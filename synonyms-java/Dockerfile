FROM openjdk:8
MAINTAINER Jim Popadak <james.popadak@outlook.com>
ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/synonyms/synonyms.jar"]

# Add Maven dependencies (not shaded into the artifact; Docker-cached)
ADD target/lib             /usr/share/synonyms/lib
# Add the service itself
ADD target/synonyms.jar    /usr/share/synonyms/synonyms.jar

