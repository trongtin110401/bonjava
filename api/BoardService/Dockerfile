FROM  eclipse-temurin:8-jdk
WORKDIR /app
COPY ./build/libs .
COPY ./config config/
COPY ./db db/
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "BoardService-1.0-SNAPSHOT.jar"]
