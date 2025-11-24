FROM  eclipse-temurin:8-jdk
WORKDIR /app
COPY ./build/libs .
COPY ./config config/
COPY ./conf conf/
ADD ./libs libs/
EXPOSE 1644
ENTRYPOINT ["java", "-cp", "libs/*:Minigame.jar", "game.MiniGameMain"]
