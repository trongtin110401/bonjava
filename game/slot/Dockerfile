FROM  eclipse-temurin:8-jdk
WORKDIR /app
COPY ./game/slot/build/libs .
COPY ./game/slot/config config/
COPY ./game/slot/conf conf/
COPY ./data data/
ADD ./data data/
ADD ./libs libs/
ADD ./game/slot/libs libs/
EXPOSE 1844
ENTRYPOINT ["java", "-cp", "libs/*:SlotMachine.jar", "game.SlotMain"]
