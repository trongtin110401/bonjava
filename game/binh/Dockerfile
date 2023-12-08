FROM openjdk:8
WORKDIR /app
COPY ./game/binh/build/libs .
COPY ./game/binh/config config
COPY ./game/binh/conf conf
COPY ./game/binh/data data
ADD ./data data
ADD ./libs libs
ADD ./game/binh/libs libs
EXPOSE 1243
EXPOSE 1244
EXPOSE 1245
ENTRYPOINT ["java", "-cp", "libs/*:binh.jar", "game.binh.server.BinhMain"]