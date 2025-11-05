FROM  eclipse-temurin:8-jdk
WORKDIR /app
COPY ./game/xocdiaKubet/build/libs .
COPY ./game/xocdiaKubet/config config/
COPY ./game/xocdiaKubet/conf conf/
COPY ./data data/
ADD ./libs libs/
ADD ./game/xocdiaKubet/libs libs/
EXPOSE 22343
EXPOSE 22344
EXPOSE 22345
ENTRYPOINT ["java", "-cp", "libs/*:xocdiaKubet.jar", "game.xocdia.server.XocDiaMain"]