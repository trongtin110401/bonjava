FROM  eclipse-temurin:8-jdk
WORKDIR /app
COPY ./game/xocdia/build/libs .
COPY ./game/xocdia/config config/
COPY ./game/xocdia/conf conf/
COPY ./data data/
ADD ./libs libs/
ADD ./game/xocdia/libs libs/
EXPOSE 2343
EXPOSE 2344
EXPOSE 2345
ENTRYPOINT ["java", "-cp", "libs/*:xocdia.jar", "game.xocdia.server.XocDiaMain"]