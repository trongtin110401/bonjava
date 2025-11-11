FROM  eclipse-temurin:8-jdk
WORKDIR /app
COPY ./build/libs .
COPY ./config config/
ADD ./libs libs/
ADD ./libs libs/
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "BoardService-1.0-SNAPSHOT.jar"]
