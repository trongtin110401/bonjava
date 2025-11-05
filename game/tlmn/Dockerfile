FROM  eclipse-temurin:8-jdk
WORKDIR /app
COPY ./game/tlmn/build/libs .
COPY ./game/tlmn/config config/
COPY ./game/tlmn/conf conf/
COPY ./data data/
ADD ./libs libs/
ADD ./game/tlmn/libs libs/
EXPOSE 2143
EXPOSE 2144
EXPOSE 2145
ENTRYPOINT ["java", "-cp", "libs/*:tlmn.jar", "game.tienlen.server.TlmnMain"]