package me.kasuki.staffcredits.api.database.mongodb.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import org.bson.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class MongoMapRepository<K, V> {

    private final Type type;

    private final MongoCollection<Document> collection;

    private final Gson gson;

    private final Map<K, V> cache;

    public MongoMapRepository(Type type, MongoCollection<Document> collection) {
        this.type = type;
        this.collection = collection;
        this.gson = new GsonBuilder()
                .serializeNulls()
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
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

    public V getFromCache(K key) {
        return this.cache.getOrDefault(key, null);
    }

    public Collection<V> getCachedEntries() {
        return this.cache.values();
    }

    public void saveToDatabase(K key, V value) {
        CompletableFuture.runAsync(() -> this.collection.replaceOne(Filters.eq("_id", key.toString()),
                Document.parse(this.gson.toJson(value)), new UpdateOptions().upsert(true)));
    }

    public void removeFromDatabase(K key) {
        CompletableFuture.runAsync(() -> {
            this.collection.deleteOne(Filters.eq("_id", key.toString()));
        });
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

    public V getFromDatabaseSync(K key) {
        Document document = this.collection.find(Filters.eq("_id", key.toString())).first();

        if (document == null) {
            return null;
        }

        return this.gson.fromJson(document.toJson(), type);
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