FROM openjdk:8
WORKDIR /app
COPY ./api/vbee/build/libs .
COPY ./api/vbee/config config/
COPY ./api/vbee/conf conf/
ADD ./libs libs/
ADD ./api/vbee/libs libs/
ENTRYPOINT ["java", "-cp", "libs/*:vbee-1.0.jar", "com.vinplay.vbee.main.VBeeMain"]
