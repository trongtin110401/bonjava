FROM openjdk:8
WORKDIR /app
COPY ./game/xizach/build/libs .
COPY ./game/xizach/config config/
COPY ./game/xizach/conf conf/
COPY ./data data/
ADD ./libs libs/
ADD ./game/xizach/libs libs/
EXPOSE 2243
EXPOSE 2244
EXPOSE 2245
ENTRYPOINT ["java", "-cp", "libs/*:xizach.jar", "game.xizach.server.XiZachMain"]