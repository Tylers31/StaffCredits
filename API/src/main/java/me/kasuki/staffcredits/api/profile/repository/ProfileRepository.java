package me.kasuki.staffcredits.api.profile.repository;

import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoCollection;
import me.kasuki.staffcredits.api.database.mongodb.repository.MongoMapRepository;
import me.kasuki.staffcredits.api.profile.Profile;
import org.bson.Document;

import java.util.UUID;

public class ProfileRepository extends MongoMapRepository<UUID, Profile> {

    public ProfileRepository(MongoCollection<Document> collection) {
        super(new TypeToken<Profile>(){}.getType(), collection);
    }
}