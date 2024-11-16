FROM openjdk:8
WORKDIR /app
COPY ./api/BoardService/build/libs .
COPY ./api/BoardService/config config/
ADD ./libs libs/
EXPOSE 8089
EXPOSE 7777
EXPOSE 5701
ENTRYPOINT ["java", "-jar", "BoardService-1.0-SNAPSHOT.jar"]
