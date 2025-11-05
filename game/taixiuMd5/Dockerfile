FROM  eclipse-temurin:8-jdk
WORKDIR /app
COPY ./game/taixiuMd5/build/libs .
COPY ./game/taixiuMd5/config config/
COPY ./game/taixiuMd5/conf conf/
ADD ./libs libs/
ADD ./game/taixiuMd5/libs libs/
EXPOSE 12043
EXPOSE 12044
EXPOSE 12045
ENTRYPOINT ["java", "-cp", "libs/*:taixiuMd5.jar", "game.TaiXiuMiniGameMain"]