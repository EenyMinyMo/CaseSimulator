package ru.somber.casesimulator.util.gson;

import com.google.gson.*;
import ru.somber.casesimulator.ItemDrop;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GsonItemDeserializer implements JsonDeserializer<ItemDrop[]> {
    @Override
    public ItemDrop[] deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        List<ItemDrop> itemDropList = new ArrayList<>();

        JsonObject jobject = json.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> elementSet = jobject.entrySet();

        for (Map.Entry<String, JsonElement> itemEntry : elementSet) {
            String name = null;
            String pathToTexture = null;
            String colorName = null;
            float percentDrop = 0;

            name = itemEntry.getKey();

            JsonObject colorProperties = itemEntry.getValue().getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> colorPropertiesSet = colorProperties.entrySet();

            for (Map.Entry<String, JsonElement> colorPropertyEntry : colorPropertiesSet) {
                String propertyName = colorPropertyEntry.getKey();
                JsonElement jsonElement = colorPropertyEntry.getValue();

                switch (propertyName) {
                    case "drop_percent" : {
                        percentDrop = jsonElement.getAsFloat();
                    } break;

                    case "color" : {
                        colorName = jsonElement.getAsString();
                    }

                    case "texture" : {
                        pathToTexture = jsonElement.getAsString();
                    }
                }
            }

            ItemDrop itemDrop = new ItemDrop(name, colorName, pathToTexture, percentDrop);
            itemDropList.add(itemDrop);
        }

        return itemDropList.toArray(new ItemDrop[0]);
    }
}
