package game.xocdia.kubet;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XocDia88MainModelDeserializer extends JsonDeserializer<MainModel> {

    @Override
    public MainModel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode root = p.getCodec().readTree(p);
        MainModel mainModel = new MainModel();

        mainModel.setC(root.get("C").asText());

        List<Message> messages = new ArrayList<>();
        for (JsonNode messageNode : root.get("M")) {
            Message message = new Message();
            message.setH(messageNode.get("H").asText());
            message.setM(messageNode.get("M").asText());

            List<Object> arguments = new ArrayList<>();
            for (JsonNode arg : messageNode.get("A")) {
                switch (message.getM()) {
                    case "updateRoomTime":
                        arguments.add(p.getCodec().treeToValue(arg, Integer.class));
                        break;
                    case "startActionTimer":
                        try {
                            arguments.add(p.getCodec().treeToValue(arg, ActionArgument.class));
                            break;
                        } catch (Exception ignored) {
                        }
                    case "sessionInfo":
                        try {
                            arguments.add(p.getCodec().treeToValue(arg, ActionArgument.class));
                            break;
                        } catch (Exception ignored) {
                        }
                }
            }
            message.setA(arguments);
            messages.add(message);
        }

        mainModel.setM(messages);
        return mainModel;
    }
}

