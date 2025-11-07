FROM  eclipse-temurin:8-jdk
WORKDIR /app
COPY ./build/libs .
COPY ./config config/
ADD ./libs libs/
ADD ./libs libs/
EXPOSE 8081
ENTRYPOINT ["java", "-cp", "libs/*:VinPlayPortal.jar", "com.vinplay.api.server.JettyServer"]
