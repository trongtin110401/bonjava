FROM openjdk:8
WORKDIR /app
COPY ./api/BoardService/build/libs .
COPY ./api/BoardService/config config/
COPY ./api/BoardService/db db/
ADD ./libs libs/
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "BoardService-1.0-SNAPSHOT.jar"]
