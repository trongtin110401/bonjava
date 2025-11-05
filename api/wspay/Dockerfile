FROM  eclipse-temurin:8-jdk
WORKDIR /app
COPY ./api/wspay/build/libs .
COPY ./api/wspay/config config/
ADD ./libs libs/
ADD ./api/wspay/libs libs/
EXPOSE 18081
ENTRYPOINT ["java", "-cp", "libs/*:wspay.jar", "com.vinplay.pay.server.JettyServer"]
