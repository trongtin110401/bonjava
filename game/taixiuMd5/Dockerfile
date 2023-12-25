FROM openjdk:8
WORKDIR /app
COPY ./game/taixiuMini/build/libs .
COPY ./game/taixiuMini/config config/
COPY ./game/taixiuMini/conf conf/
ADD ./libs libs/
ADD ./game/taixiuMini/libs libs/
EXPOSE 2043
EXPOSE 2044
EXPOSE 2045
ENTRYPOINT ["java", "-cp", "libs/*:taixiuMd5.jar", "game.TaiXiuMiniGameMain"]