FROM  eclipse-temurin:8-jdk
WORKDIR /app
COPY ./game/taixiuKubet/build/libs .
COPY ./game/taixiuKubet/config config/
COPY ./game/taixiuKubet/conf conf/
ADD ./libs libs/
ADD ./game/taixiuKubet/libs libs/
EXPOSE 22043
EXPOSE 22044
EXPOSE 22045
ENTRYPOINT ["java", "-cp", "libs/*:taixiuKubet.jar", "game.TaiXiuMiniGameMain"]