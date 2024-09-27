package me.kasuki.staffcredits.profile.listener;

import me.kasuki.staffcredits.StaffCreditsPlugin;
import me.kasuki.staffcredits.api.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class ProfileListener implements Listener {

    private final StaffCreditsPlugin instance;

    public ProfileListener(StaffCreditsPlugin instance) {
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Profile profile = this.instance.getStaffCreditsAPI().getProfileHandler().getProfile(player.getUniqueId());

        if (profile == null) {
            return;
        }

        this.instance.getStaffCreditsAPI().getProfileHandler().removeFromCache(profile);
        this.instance.getStaffCreditsAPI().getProfileHandler().saveToDatabase(profile);
        System.out.println("[DEBUG] Saving data of " + event.getPlayer().getName());
    }
}