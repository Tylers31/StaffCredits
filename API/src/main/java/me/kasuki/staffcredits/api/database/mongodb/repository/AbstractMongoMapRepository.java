package me.kasuki.staffcredits.api.database.mongodb.repository;

import me.kasuki.staffcredits.utilities.cuboid.Cuboid;
import me.kasuki.staffcredits.utilities.serialization.CuboidSerializer;
import me.kasuki.staffcredits.utilities.serialization.ItemStackSerializer;
import me.kasuki.staffcredits.utilities.serialization.LocationSerializer;
import me.kasuki.staffcredits.utilities.serialization.PotionEffectSerializer;
import com.google.gson.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class AbstractMongoMapRepository<K, V> {

    private final Type type;

    private final MongoCollection<Document> collection;

    private final Gson gson;

    private final Map<K, V> cache;

    public AbstractMongoMapRepository(Type type, MongoCollection<Document> collection) {
        this.type = type;
        this.collection = collection;
        this.gson = new GsonBuilder()
                .serializeNulls()
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectSerializer())
                .registerTypeHierarchyAdapter(Location.class, new LocationSerializer())
                .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackSerializer())
                .registerTypeAdapter(Cuboid.class, new CuboidSerializer())
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();
        this.cache = new ConcurrentHashMap<>();
    }

    public void addToCache(K key, V value) {
        this.cache.put(key, value);
    }

    public void removeFromCache(K key) {
        this.cache.remove(key);
    }

    public void saveToDatabase(K key, V value) {
        CompletableFuture.runAsync(() ->
                this.collection.replaceOne(Filters.eq("_id", key.toString()),
                Document.parse(this.gson.toJson(value)), new ReplaceOptions().upsert(true)));
    }

    public void removeFromDatabase(K key) {
        CompletableFuture.runAsync(() ->
                this.collection.deleteOne(Filters.eq("_id", key.toString())));
    }

    public V getFromCache(K key) {
        return this.cache.getOrDefault(key, null);
    }

    public boolean hasKey(K key) {
        return this.cache.containsKey(key);
    }
    public CompletableFuture<List<V>> getAllEntriesFromDatabase() {
        return CompletableFuture.supplyAsync(() -> {
            List<V> list = new ArrayList<>();

            for (Document document : this.collection.find()) {
                if (document == null) {
                    continue;
                }

                list.add(this.gson.fromJson(document.toJson(), this.type));
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

    public V getFromDatabaseSync(K key) {
        Document document = this.collection.find(Filters.eq("_id", key.toString())).first();

        if (document == null) {
            return null;
        }

        return this.gson.fromJson(document.toJson(), type);
    }

    public void saveAllToDatabase(List<V> values) {
        CompletableFuture.runAsync(() -> {
        List<Document> documents = new ArrayList<>();

        for (V value : values) {
            Document document = Document.parse(this.gson.toJson(value));
            documents.add(document);
        }

        this.collection.insertMany(documents);
        });
    }

    public void removeAllFromDatabase(List<K> keys) {
        CompletableFuture.runAsync(() -> this.collection.deleteMany(Filters.in("_id", keys)));
    }

    public CompletableFuture<V> getFromDatabase(K key) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = this.collection.find(Filters.eq("_id", key.toString())).first();

            if (document == null) {
                return null;
            }

            return this.gson.fromJson(document.toJson(), type);
        });
    }
}