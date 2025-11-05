FROM  eclipse-temurin:8-jdk
WORKDIR /app
COPY ./api/VinPlayPortal/build/libs .
COPY ./api/VinPlayPortal/config config/
ADD ./libs libs/
ADD ./api/VinPlayPortal/libs libs/
EXPOSE 8081
ENTRYPOINT ["java", "-cp", "libs/*:VinPlayPortal.jar", "com.vinplay.api.server.JettyServer"]
