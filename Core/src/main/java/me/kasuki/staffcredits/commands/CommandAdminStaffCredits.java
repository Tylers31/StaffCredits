package me.kasuki.staffcredits.commands;

import cc.insidious.fethmusmioma.annotation.Parameter;
import cc.insidious.fethmusmioma.annotation.SubCommand;
import me.kasuki.staffcredits.StaffCreditsPlugin;
import me.kasuki.staffcredits.api.profile.Profile;
import me.kasuki.staffcredits.utilities.CC;
import me.kasuki.staffcredits.utilities.data.NumFormatter;
import org.bukkit.entity.Player;

public class CommandAdminStaffCredits {
    public StaffCreditsPlugin instance;

    public CommandAdminStaffCredits(StaffCreditsPlugin instance) {
        this.instance = instance;
    }

    @SubCommand(parent = "staffcredits", label = "set", permission = "staffcredits.admin.set")
    public void executeStaffCreditsSet(Player sender, @Parameter(name = "target") Player target, @Parameter(name = "amount") double amount){
        if(target == null || target.getUniqueId() == null){
            sender.sendMessage(CC.chat("&c&l(!) &cInvalid target, try again?"));
            return;
        }

        Profile profile = instance.getStaffCreditsAPI().getProfileHandler().getProfile(target.getUniqueId());

        if(profile == null){
            sender.sendMessage(CC.chat("&c&l(!) &cNo profile found, try again later."));
            return;
        }

        profile.setCredits(amount);
        sender.sendMessage(CC.chat("&a&l(!) &aSet " + target.getName() + "'s credits to " + NumFormatter.formatToUSD(amount)));
    }
}
