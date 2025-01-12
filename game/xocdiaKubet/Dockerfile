FROM openjdk:8
WORKDIR /app
COPY ./game/xocdia/build/libs .
COPY ./game/xocdia/config config/
COPY ./game/xocdia/conf conf/
COPY ./data data/
ADD ./libs libs/
ADD ./game/xocdia/libs libs/
EXPOSE 22343
EXPOSE 22344
EXPOSE 22345
ENTRYPOINT ["java", "-cp", "libs/*:xocdiaKubet.jar", "game.xocdia.server.XocDiaMain"]