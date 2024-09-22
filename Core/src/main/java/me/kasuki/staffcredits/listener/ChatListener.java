package me.kasuki.staffcredits.listener;

import com.cryptomorin.xseries.XSound;
import me.kasuki.staffcredits.StaffCredits;
import me.kasuki.staffcredits.api.profile.Profile;
import me.kasuki.staffcredits.menu.CreditsWithdrawalMenu;
import me.kasuki.staffcredits.utilities.CC;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    public StaffCredits instance;

    public ChatListener(StaffCredits instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        Profile profile = instance.getStaffCreditsAPI().getProfileHandler().getProfile(player.getUniqueId());

        if(!instance.pendingRequests.contains(player.getUniqueId())){
            return;
        }

        event.setCancelled(true);
        instance.pendingRequests.remove(player.getUniqueId());

        if(profile == null){
            player.sendMessage(CC.chat("&c&l(!) &cNo profile found, try again later."));
            player.playSound(player.getLocation(), XSound.BLOCK_LAVA_POP.parseSound(), 1, 1);
            return;
        }

        Double amount = parseDouble(event.getMessage());

        if(amount == null){
            player.sendMessage(CC.chat("&c&l(!) &cInvalid input, expected a number."));
            player.playSound(player.getLocation(), XSound.BLOCK_LAVA_POP.parseSound(), 1, 1);
            return;
        }

        if(amount < 0){
            player.sendMessage(CC.chat("&c&l(!) &cInvalid input, expected a positive number."));
            player.playSound(player.getLocation(), XSound.BLOCK_LAVA_POP.parseSound(), 1, 1);
            return;
        }

        player.sendMessage(CC.chat("&a&l(!) &aSuccessfully updated withdrawal amount!"));
        player.playSound(player.getLocation(), XSound.ENTITY_PLAYER_LEVELUP.parseSound(), 1, 1);

        Bukkit.getScheduler().runTask(instance, () -> {
            CreditsWithdrawalMenu withdrawalMenu = new CreditsWithdrawalMenu(instance);
            withdrawalMenu.openWithdrawalMenu(player, profile, amount);
        });
    }

    public Double parseDouble(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
