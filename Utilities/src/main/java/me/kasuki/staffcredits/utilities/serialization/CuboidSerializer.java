package me.kasuki.staffcredits.utilities.serialization;

import me.kasuki.staffcredits.utilities.cuboid.Cuboid;
import me.kasuki.staffcredits.utilities.json.JsonBuilder;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Type;
import java.util.UUID;

public class CuboidSerializer implements JsonSerializer<Cuboid>, JsonDeserializer<Cuboid> {

    @Override
    public Cuboid deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.isJsonNull() || !jsonElement.isJsonObject()) {
            return null;
        }

        JsonObject object = jsonElement.getAsJsonObject();

        Location first = new Location(Bukkit.getServer().getWorld(UUID.fromString(object.get("world").getAsString())), object.get("x1").getAsInt(),
                object.get("y1").getAsInt(), object.get("z1").getAsInt());

        Location second = new Location(Bukkit.getServer().getWorld(UUID.fromString(object.get("world").getAsString())), object.get("x2").getAsInt(),
                object.get("y2").getAsInt(), object.get("z2").getAsInt());

        return new Cuboid(first, second);
    }

    @Override
    public JsonElement serialize(Cuboid blocks, Type type, JsonSerializationContext jsonSerializationContext) {

        Location first = blocks.getLocation1();
        Location second = blocks.getLocation2();

        return new JsonBuilder()
                .addProperty("world", first.getWorld().getUID().toString())
                .addProperty("x1", first.getBlockX())
                .addProperty("y1", first.getBlockY())
                .addProperty("z1", first.getBlockZ())
                .addProperty("x2", second.getBlockX())
                .addProperty("y2", second.getBlockY())
                .addProperty("z2", second.getBlockZ())
                .toJson();
    }
}
