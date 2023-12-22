FROM openjdk:8
WORKDIR /app
COPY ./game/poker/build/libs .
COPY ./game/poker/config config/
COPY ./game/poker/conf conf/
COPY ./data data/
ADD ./libs libs/
ADD ./game/poker/libs libs/
EXPOSE 1743
EXPOSE 1744
EXPOSE 1745
ENTRYPOINT ["java", "-cp", "libs/*:poker.jar", "game.poker.server.PokerMain"]