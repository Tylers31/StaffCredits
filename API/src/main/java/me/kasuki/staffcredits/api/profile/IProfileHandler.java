package me.kasuki.staffcredits.api.profile;

import me.kasuki.staffcredits.api.profile.repository.ProfileRepository;

import java.util.Collection;
import java.util.UUID;

public interface IProfileHandler {

    ProfileRepository getProfileRepository();

    Profile getProfile(UUID uniqueId);

    Collection<Profile> getProfiles();

    void addToCache(Profile profile);
    void removeFromCache(Profile profile);

    void saveToDatabase(Profile profile);

    void unload();

}