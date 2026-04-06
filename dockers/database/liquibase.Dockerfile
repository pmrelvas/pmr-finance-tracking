FROM liquibase/liquibase:5.0.2

# Download the MongoDB extension and the Java driver directly into the lib folder
# This is more reliable than lpm for specific environment setups
USER root
RUN wget -P /liquibase/lib https://repo1.maven.org/maven2/org/liquibase/ext/liquibase-mongodb/5.0.2/liquibase-mongodb-5.0.2.jar && \
    wget -P /liquibase/lib https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-sync/5.6.4/mongodb-driver-sync-5.6.4.jar && \
    wget -P /liquibase/lib https://repo1.maven.org/maven2/org/mongodb/bson/5.6.4/bson-5.6.4.jar && \
    wget -P /liquibase/lib https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-core/5.6.4/mongodb-driver-core-5.6.4.jar

USER liquibase
