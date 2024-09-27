package me.kasuki.staffcredits.commands;

import cc.insidious.fethmusmioma.annotation.Command;
import cc.insidious.fethmusmioma.annotation.SubCommand;
import me.kasuki.staffcredits.StaffCreditsPlugin;
import me.kasuki.staffcredits.api.profile.Profile;
import me.kasuki.staffcredits.api.profile.withdrawals.WithdrawRequest;
import me.kasuki.staffcredits.menu.CreditsMenu;
import me.kasuki.staffcredits.utilities.CC;
import me.kasuki.staffcredits.utilities.data.NumFormatter;
import me.kasuki.staffcredits.utilities.date.DateTimeFormats;
import org.bukkit.entity.Player;

public class CommandStaffCredits {
    public StaffCreditsPlugin instance;

    public CommandStaffCredits(StaffCreditsPlugin instance) {
        this.instance = instance;
    }

    @Command(label = "staffcredits", aliases = {"credits", "scredits"}, permission = "staffcredits.credits")
    public void executeStaffCredits(Player sender){
        Profile profile = instance.getStaffCreditsAPI().getProfileHandler().getProfile(sender.getUniqueId());

        if(profile == null){
            sender.sendMessage(CC.chat("&c&l(!) &cUnable to load profile... try again?"));
            return;
        }

        CreditsMenu creditsMenu = new CreditsMenu(instance);
        creditsMenu.openCreditsMenu(sender, profile);
    }

    @SubCommand(label = "history", parent = "staffcredits", permission = "staffcredits.history")
    public void executeStaffCreditsHistory(Player sender){
        Profile profile = instance.getStaffCreditsAPI().getProfileHandler().getProfile(sender.getUniqueId());

        if(profile == null){
            sender.sendMessage(CC.chat("&c&l(!) &cUnable to load profile... try again?"));
            return;
        }

        for(WithdrawRequest request : profile.getWithdrawRequests()){
            sender.sendMessage(CC.chat("&8&m-------------------------------------"));
            sender.sendMessage(CC.chat("&2&l* &a&lAmount:&f " + NumFormatter.formatToUSD(request.getAmount())));
            sender.sendMessage(CC.chat("&2&l* &a&lDate:&f " + DateTimeFormats.convertMillisToDate(request.getDate())));
        }

        sender.sendMessage(CC.chat("&8&m-------------------------------------"));
    }
}
