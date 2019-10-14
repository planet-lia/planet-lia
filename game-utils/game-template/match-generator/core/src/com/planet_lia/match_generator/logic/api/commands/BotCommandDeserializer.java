package com.planet_lia.match_generator.logic.api.commands;

import com.google.gson.*;
import com.planet_lia.match_generator.libs.BotCommand;

import java.lang.reflect.Type;

public class BotCommandDeserializer implements JsonDeserializer<BotCommand> {

    private Class[] supportedCommandClasses;

    public BotCommandDeserializer(Class[] supportedCommandClasses) {
        this.supportedCommandClasses = supportedCommandClasses;
    }

    @Override
    public BotCommand deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jObject = (JsonObject) json;

        String commandType;
        try {
            commandType = jObject.get("__type").getAsString();
        } catch (Exception e) {
            System.out.printf("Bot response didn't have '__type' field: %s\n", e.getMessage());
            return null;
        }

        for (Class commandClass : supportedCommandClasses) {
            if (commandType.equals(commandClass.getSimpleName())) {
                return context.deserialize(json, commandClass);
            }
        }

        return null;
    }
}