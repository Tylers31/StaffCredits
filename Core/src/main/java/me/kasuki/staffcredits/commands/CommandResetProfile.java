package me.kasuki.staffcredits.commands;

import cc.insidious.fethmusmioma.annotation.Command;
import cc.insidious.fethmusmioma.annotation.Parameter;
import me.kasuki.staffcredits.StaffCreditsPlugin;
import me.kasuki.staffcredits.api.profile.Profile;
import me.kasuki.staffcredits.utilities.CC;
import org.bukkit.entity.Player;

public class CommandResetProfile {
    public StaffCreditsPlugin instance;

    public CommandResetProfile(StaffCreditsPlugin instance) {
        this.instance = instance;
    }

    @Command(label = "resetprofile", permission = "debug.resetprofile")
    public void execute(Player sender, @Parameter(name = "target") Player target){
        Profile profile = instance.getStaffCreditsAPI().getProfileHandler().getProfile(target.getUniqueId());

        if(profile == null){
            sender.sendMessage(CC.chat("&c&l(!) &cNo profile found, try again later."));
            return;
        }

        instance.getStaffCreditsAPI().getProfileHandler().removeFromCache(profile);
        instance.getStaffCreditsAPI().getProfileHandler().saveToDatabase(new Profile(target.getUniqueId()));
        instance.getStaffCreditsAPI().getProfileHandler().addToCache(new Profile(target.getUniqueId()));
        sender.sendMessage(CC.chat("&a&l(!) &aReset " + target.getName() + "'s profile successfully!"));
    }
}
