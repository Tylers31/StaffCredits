package me.kasuki.staffcredits.api.database.mongodb.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import org.bson.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public abstract class MongoListRepository<K, V> {

    private final Type type;

    private final List<V> cache;

    private final Gson gson;

    private final MongoCollection<Document> collection;

    public MongoListRepository(Type type, MongoCollection<Document> collection) {
        this.type = type;
        this.cache = new CopyOnWriteArrayList<>();
        this.gson = new GsonBuilder()
                .serializeNulls()
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
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

}