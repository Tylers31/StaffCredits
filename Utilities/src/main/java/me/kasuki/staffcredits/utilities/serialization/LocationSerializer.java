package me.kasuki.staffcredits.utilities.serialization;

import me.kasuki.staffcredits.utilities.json.JsonBuilder;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Type;
import java.util.UUID;

public class LocationSerializer implements JsonSerializer<Location>, JsonDeserializer<Location> {
    @Override
    public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.isJsonNull() || !jsonElement.isJsonObject()) {
            return null;
        }

        JsonObject object = jsonElement.getAsJsonObject();

        return new Location(Bukkit.getServer().getWorld(UUID.fromString(object.get("world").getAsString())),
                object.get("x").getAsInt(), object.get("y").getAsInt(), object.get("z").getAsInt(),
                object.get("yaw").getAsFloat(), object.get("pitch").getAsFloat());
    }

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {
        if (location == null) {
            return null;
        }

        return new JsonBuilder()
                .addProperty("world", location.getWorld().getUID().toString())
                .addProperty("x", location.getBlockX())
                .addProperty("y", location.getBlockY())
                .addProperty("z", location.getBlockZ())
                .addProperty("yaw", location.getYaw())
                .addProperty("pitch", location.getPitch())
                .toJson();
    }
}
