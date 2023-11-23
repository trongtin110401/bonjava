FROM openjdk:8
WORKDIR /app
COPY ./game/slot/build/libs .
COPY ./game/slot/config config/
COPY ./game/slot/conf conf/
ADD ./libs libs/
ADD ./game/slot/libs libs/
EXPOSE 1844
ENTRYPOINT ["java", "-cp", "libs/*:SlotMachine.jar", "game.SlotMain"]
