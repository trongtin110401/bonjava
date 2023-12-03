FROM openjdk:8
WORKDIR /app
COPY ./game/lieng/build/libs .
COPY ./game/lieng/config config/
COPY ./game/lieng/conf conf/
COPY ./data data/
ADD ./libs libs/
ADD ./game/lieng/libs libs/
EXPOSE 1543
EXPOSE 1544
EXPOSE 1545
ENTRYPOINT ["java", "-cp", "libs/*:lieng.jar", "game.lieng.server.LiengMain"]