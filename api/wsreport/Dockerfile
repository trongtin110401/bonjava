FROM  eclipse-temurin:8-jdk
WORKDIR /app
COPY ./api/wsreport/build/libs .
COPY ./api/wsreport/config config/
ADD ./libs libs/
EXPOSE 8089
EXPOSE 7777
EXPOSE 5701
ENTRYPOINT ["java", "-jar", "wsreport-1.0-SNAPSHOT.jar"]
