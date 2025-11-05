FROM  eclipse-temurin:8-jdk
WORKDIR /app
COPY ./game/bacayServer/build/libs .
COPY ./game/bacayServer/config config/
COPY ./game/bacayServer/conf conf/
COPY ./data .
ADD ./libs libs/
ADD ./game/bacayServer/libs libs/
EXPOSE 1043
EXPOSE 1044
EXPOSE 1045
ENTRYPOINT ["java", "-cp", "libs/*:bacayServer.jar", "game.bacay.server.BacayMain"]