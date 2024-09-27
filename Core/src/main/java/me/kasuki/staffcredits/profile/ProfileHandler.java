package me.kasuki.staffcredits.profile;

import lombok.Getter;
import me.kasuki.staffcredits.StaffCreditsPlugin;
import me.kasuki.staffcredits.api.profile.IProfileHandler;
import me.kasuki.staffcredits.api.profile.Profile;
import me.kasuki.staffcredits.api.profile.repository.ProfileRepository;

import java.util.Collection;
import java.util.UUID;

@Getter
public class ProfileHandler implements IProfileHandler {

    private final StaffCreditsPlugin instance;

    private final ProfileRepository profileRepository;

    public ProfileHandler(StaffCreditsPlugin instance) {
        this.instance = instance;
        this.profileRepository = new ProfileRepository(this.instance.getMongoHandler().getCollection("profiles"));
    }

    @Override
    public Profile getProfile(UUID uniqueId) {
        return this.profileRepository.getFromCache(uniqueId);
    }

    @Override
    public void addToCache(Profile profile) {
        this.profileRepository.addToCache(profile.getUniqueId(), profile);
    }

    @Override
    public void removeFromCache(Profile profile) {
        this.profileRepository.removeFromCache(profile.getUniqueId());
    }

    @Override
    public void saveToDatabase(Profile profile) {
        this.getProfileRepository().saveToDatabase(profile.getUniqueId(), profile);
    }

    @Override
    public Collection<Profile> getProfiles() {
        return this.profileRepository.getCachedEntries();
    }

    @Override
    public void unload() {
        for (Profile profile : this.profileRepository.getCachedEntries()) {
            System.out.println("[DEBUG] Unloading the profile of " + profile.getUniqueId());
            this.saveToDatabase(profile);
        }
    }
}