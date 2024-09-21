package me.kasuki.staffcredits.api.database.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Collections;

public class MongoHandler {
    private final MongoDatabase mongoDatabase;
    public MongoHandler(String host, int port, boolean auth, String username, String password, String database, boolean uriMode, String uri) {
        MongoClient mongoClient;

        if (uriMode) {
            mongoClient = new MongoClient(new MongoClientURI(uri));
        } else {
            if (auth) {
                mongoClient = new MongoClient(new ServerAddress(host, port), Collections.singletonList(MongoCredential.createCredential(username, database, password.toCharArray())));
            } else {
                mongoClient = new MongoClient(new ServerAddress(host, port));
            }
        }

        this.mongoDatabase = mongoClient.getDatabase(database);
    }

    public MongoCollection<Document> getCollection(String name) {
        return this.mongoDatabase.getCollection(name);
    }
}
