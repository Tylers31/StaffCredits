package me.kasuki.staffcredits.commands;

import cc.insidious.fethmusmioma.annotation.Parameter;
import cc.insidious.fethmusmioma.annotation.SubCommand;
import me.kasuki.staffcredits.StaffCredits;
import me.kasuki.staffcredits.api.profile.Profile;
import me.kasuki.staffcredits.utilities.CC;
import me.kasuki.staffcredits.utilities.num.NumFormatter;
import org.bukkit.entity.Player;

public class CommandStaffCreditsAdmin {
    public StaffCredits instance;

    public CommandStaffCreditsAdmin(StaffCredits instance) {
        this.instance = instance;
    }

    @SubCommand(parent = "staffcredits", label = "set", permission = "staffcredits.admin.set")
    public void executeStaffCreditsSet(Player sender, @Parameter(name = "target") Player target, @Parameter(name = "amount") double amount){
        Profile profile = instance.getStaffCreditsAPI().getProfileHandler().getProfile(target.getUniqueId());

        if(profile == null){
            sender.sendMessage(CC.chat("&c&l(!) &cNo profile found, try again later."));
            return;
        }

        profile.setCredits(amount);
        sender.sendMessage(CC.chat("&a&l(!) &aSet " + target.getName() + "'s credits to " + NumFormatter.formatToUSD(amount)));
    }
}
