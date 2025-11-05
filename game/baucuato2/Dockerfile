FROM  eclipse-temurin:8-jdk
WORKDIR /app
COPY ./game/baucuato2/build/libs .
COPY ./game/baucuato2/config config/
COPY ./game/baucuato2/conf conf/
COPY ./data .
ADD ./libs libs/
ADD ./game/baucuato2/libs libs/
EXPOSE 3643
EXPOSE 3644
EXPOSE 3645
ENTRYPOINT ["java", "-cp", "libs/*:baucuato2.jar", "game.BauCuaTo2Main"]
