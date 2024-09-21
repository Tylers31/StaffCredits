package me.kasuki.staffcredits.commands;

import cc.insidious.fethmusmioma.annotation.Command;
import me.kasuki.staffcredits.StaffCredits;
import me.kasuki.staffcredits.api.profile.Profile;
import me.kasuki.staffcredits.menu.CreditsMenu;
import me.kasuki.staffcredits.utilities.CC;
import org.bukkit.entity.Player;

public class CommandStaffCredits {
    public StaffCredits instance;

    public CommandStaffCredits(StaffCredits instance) {
        this.instance = instance;
    }

    @Command(label = "staffcredits", permission = "staffcredits.credits")
    public void executeStaffCredits(Player sender){
        Profile profile = instance.getStaffCreditsAPI().getProfileHandler().getProfile(sender.getUniqueId());

        if(profile == null){
            sender.sendMessage(CC.chat("&c&l(!) &cUnable to load profile... try again?"));
            return;
        }

        CreditsMenu creditsMenu = new CreditsMenu(instance);
        creditsMenu.openCreditsMenu(sender, profile);
    }
}
