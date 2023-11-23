FROM openjdk:8
WORKDIR /app
COPY ./api/VinPlayBackend/build/libs .
COPY ./api/VinPlayBackend/config config/
ADD ./libs libs/
ADD ./api/VinPlayBackend/libs libs/
EXPOSE 8082
ENTRYPOINT ["java", "-cp", "libs/*:VinPlayBackend-1.0.jar", "com.vinplay.api.backend.server.VinPlayBackendMain"]
