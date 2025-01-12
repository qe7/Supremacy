package com.supremacy.setting;

import com.google.gson.*;
import com.supremacy.setting.type.Setting;
import com.supremacy.setting.type.collection.ModuleMode;

import java.lang.reflect.Type;

// too lazy to make proper deserialization soz
public class SettingTypeAdapter<T> implements JsonSerializer<Setting<T>> {

    @Override
    public JsonElement serialize(Setting<T> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", src.getName());

        if (src.getValue() instanceof ModuleMode) {
            jsonObject.add("value", context.serialize(((ModuleMode) src.getValue()).getName()));
        } else {
            jsonObject.add("value", context.serialize(src.getValue()));
        }

        return jsonObject;
    }
}