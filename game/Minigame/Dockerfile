FROM openjdk:8
WORKDIR /app
COPY ./game/Minigame/build/libs .
COPY ./game/Minigame/config config/
COPY ./game/Minigame/conf conf/
ADD ./libs libs/
ADD ./game/Minigame/libs libs/
EXPOSE 1644
ENTRYPOINT ["java", "-cp", "libs/*:Minigame.jar", "game.MiniGameMain"]
