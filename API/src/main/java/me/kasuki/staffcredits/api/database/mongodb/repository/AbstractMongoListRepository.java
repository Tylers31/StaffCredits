package me.kasuki.staffcredits.api.database.mongodb.repository;

import me.kasuki.staffcredits.utilities.cuboid.Cuboid;
import me.kasuki.staffcredits.utilities.serialization.CuboidSerializer;
import me.kasuki.staffcredits.utilities.serialization.ItemStackSerializer;
import me.kasuki.staffcredits.utilities.serialization.LocationSerializer;
import me.kasuki.staffcredits.utilities.serialization.PotionEffectSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
public abstract class AbstractMongoListRepository<K, V> {

    private final Type type;

    private final List<V> cache;

    private final Gson gson;

    private final MongoCollection<Document> collection;

    public AbstractMongoListRepository(Type type, MongoCollection<Document> collection) {
        this.type = type;
        this.cache = new ArrayList<>();
        this.gson = new GsonBuilder()
                .serializeNulls()
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .registerTypeHierarchyAdapter(PotionEffectType.class, new PotionEffectSerializer())
                .registerTypeHierarchyAdapter(Location.class, new LocationSerializer())
                .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackSerializer())
                .registerTypeAdapter(Cuboid.class, new CuboidSerializer())
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();
        this.collection = collection;
    }

    public abstract V getFromCache(K key);

    public CompletableFuture<V> getFromDatabase(K key) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = this.collection.find(Filters.eq("_id", key.toString())).first();

            if (document == null) {
                return null;
            }

            return this.gson.fromJson(document.toJson(), this.type);
        });
    }

    public CompletableFuture<List<V>> getAllEntriesFromDatabase() {
        return CompletableFuture.supplyAsync(() -> {
            List<V> list = new ArrayList<>();

            for (Document document : this.collection.find()) {
                if (document == null) {
                    continue;
                }

                V value = this.gson.fromJson(document.toJson(), this.type);
                list.add(value);
            }
            return list;
        });
    }

    public List<V> getAllEntriesFromDatabaseSync() {
        List<V> list = new ArrayList<>();
        for (Document document : this.collection.find()) {
            if (document == null) {
                continue;
            }

            list.add(this.gson.fromJson(document.toJson(), this.type));
        }
        return list;
    }

    public void addToCache(V value) {
        this.cache.add(value);
    }

    public void removeFromCache(V value) {
        this.cache.remove(value);
    }

    public abstract void saveToDatabase(V value);

    public abstract void removeFromDatabase(V value);

    public abstract void saveAllToDatabase(List<V> values);
    public abstract void removeAllFromDatabase(List<K> values);

}
