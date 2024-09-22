package me.kasuki.staffcredits.profile.listener;

import me.kasuki.staffcredits.StaffCredits;
import me.kasuki.staffcredits.api.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class ProfileListener implements Listener {

    private final StaffCredits instance;

    public ProfileListener(StaffCredits instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        Profile profile = this.instance.getStaffCreditsAPI().getProfileHandler().getProfile(uuid);

        if (profile != null) {
            return;
        }

        Profile found = this.instance.getStaffCreditsAPI().getProfileHandler().getProfileRepository().getFromDatabaseSync(uuid);

        if (found != null) {
            this.instance.getStaffCreditsAPI().getProfileHandler().getProfileRepository().addToCache(uuid, found);
            return;
        }

        Profile newProfile = new Profile(uuid);
        this.instance.getStaffCreditsAPI().getProfileHandler().getProfileRepository().addToCache(uuid, newProfile);
        this.instance.getStaffCreditsAPI().getProfileHandler().saveToDatabase(newProfile);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Profile profile = this.instance.getStaffCreditsAPI().getProfileHandler().getProfile(player.getUniqueId());

        if (profile == null) {
            return;
        }

        this.instance.getStaffCreditsAPI().getProfileHandler().removeFromCache(profile);
    }
}