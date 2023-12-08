FROM openjdk:8
WORKDIR /app
COPY ./game/sam/build/libs .
COPY ./game/sam/config config/
COPY ./game/sam/conf conf/
COPY ./data data/
ADD ./libs libs/
ADD ./game/sam/libs libs/
EXPOSE 1943
EXPOSE 1944
EXPOSE 1945
ENTRYPOINT ["java", "-cp", "libs/*:sam.jar", "game.sam.server.SamMain"]