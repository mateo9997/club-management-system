web: java $JAVA_OPTS -Dserver.port=$PORT -jar target/club-management-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod --flyway.migrate
release: java $JAVA_OPTS -Dserver.port=$PORT -jar target/club-management-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod --flyway.migrate
