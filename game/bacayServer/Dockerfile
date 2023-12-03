FROM openjdk:8
WORKDIR /app
COPY ./game/bacayServer/build/libs .
COPY ./game/bacayServer/config config/
COPY ./game/bacayServer/conf conf/
COPY ./data .
ADD ./libs libs/
ADD ./game/bacayServer/libs libs/
EXPOSE 1143
EXPOSE 1144
EXPOSE 1145
ENTRYPOINT ["java", "-cp", "libs/*:bacayServer.jar", "game.bacayServer.server.BacayMain"]