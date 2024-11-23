package game.modules.minigame.game79;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainModelDeserializer extends JsonDeserializer<MainModel> {

    @Override
    public MainModel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode root = p.getCodec().readTree(p);
        MainModel mainModel = new MainModel();

        mainModel.setC(root.get("C").asText());

        List<Message> messages = new ArrayList<>();
        for (JsonNode messageNode : root.get("M")) {
            Message message = new Message();
            message.setHub(messageNode.get("H").asText());
            message.setMethod(messageNode.get("M").asText());

            List<Object> arguments = new ArrayList<>();
            for (JsonNode arg : messageNode.get("A")) {
                switch (message.getMethod()) {
                    case "sessionInfo":
                        arguments.add(p.getCodec().treeToValue(arg, SessionInfo.class));
                        break;
                    case "gameHistory":
                        arguments.add(p.getCodec().treeToValue(arg, GameHistory[].class));
                        break;
                    case "changeDealer":
                        arguments.add(new ChangeDealer(arg.asText()));
                        break;
                }
            }

            message.setArguments(arguments);
            messages.add(message);
        }

        mainModel.setM(messages);
        return mainModel;
    }
}

