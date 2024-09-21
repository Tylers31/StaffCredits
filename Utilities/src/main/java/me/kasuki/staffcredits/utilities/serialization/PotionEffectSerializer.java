package me.kasuki.staffcredits.utilities.serialization;

import com.google.gson.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Type;

public class PotionEffectSerializer implements JsonSerializer<PotionEffect>, JsonDeserializer<PotionEffect> {

    @Override
    public PotionEffect deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return fromJson(jsonElement);
    }

    @Override
    public JsonElement serialize(PotionEffect potionEffect, Type type, JsonSerializationContext jsonSerializationContext) {
        return toJson(potionEffect);
    }

    public static JsonObject toJson(final PotionEffect potionEffect) {
        if (potionEffect == null) {
            return null;
        }
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", potionEffect.getType().getName());
        jsonObject.addProperty("duration", potionEffect.getDuration());
        jsonObject.addProperty("amplifier", potionEffect.getAmplifier());
        jsonObject.addProperty("ambient", potionEffect.isAmbient());
        return jsonObject;
    }

    public static PotionEffect fromJson(final JsonElement jsonElement) {
        if (jsonElement == null || !jsonElement.isJsonObject()) {
            return null;
        }
        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        final PotionEffectType effectType = PotionEffectType.getByName(jsonObject.get("id").getAsString());
        final int duration = jsonObject.get("duration").getAsInt();
        final int amplifier = jsonObject.get("amplifier").getAsInt();
        final boolean ambient = jsonObject.get("ambient").getAsBoolean();
        return new PotionEffect(effectType, duration, amplifier, ambient);
    }

}
