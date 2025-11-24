FROM  eclipse-temurin:8-jdk
WORKDIR /app
COPY ./build/libs .
COPY ./config config/
COPY ./conf conf/
ADD ./libs libs/
ENTRYPOINT ["java", "-cp", "libs/*:vbee-1.0.jar", "com.vinplay.vbee.main.VBeeMain"]
