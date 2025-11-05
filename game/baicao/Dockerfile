FROM  eclipse-temurin:8-jdk
WORKDIR /app
COPY ./game/baicao/build/libs .
COPY ./game/baicao/config config/
COPY ./game/baicao/conf conf/
COPY ./data .
ADD ./libs libs/
ADD ./game/baicao/libs libs/
EXPOSE 1143
EXPOSE 1144
EXPOSE 1145
ENTRYPOINT ["java", "-cp", "libs/*:baicao.jar", "game.baicao.server.BaiCaoMain"]