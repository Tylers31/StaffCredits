package me.kasuki.staffcredits.commands;

import cc.insidious.fethmusmioma.annotation.Command;
import cc.insidious.fethmusmioma.annotation.Optional;
import cc.insidious.fethmusmioma.annotation.SubCommand;
import me.kasuki.staffcredits.StaffCreditsPlugin;
import me.kasuki.staffcredits.api.profile.Profile;
import me.kasuki.staffcredits.api.profile.withdrawals.GiftCard;
import me.kasuki.staffcredits.api.profile.withdrawals.WithdrawRequest;
import me.kasuki.staffcredits.menu.CreditsHistoryMenu;
import me.kasuki.staffcredits.menu.CreditsMenu;
import me.kasuki.staffcredits.menu.CreditsWithdrawalMenu;
import me.kasuki.staffcredits.menu.GiftcardMenu;
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
    public void executeStaffCreditsHistory(Player sender, @Optional(value = "self") Player target){
        if(target == null){
            sender.sendMessage(CC.chat("&c&l(!) &cInvalid target, try again?"));
            return;
        }

        Profile profile = instance.getStaffCreditsAPI().getProfileHandler().getProfile(target.getUniqueId());

        if(profile == null){
            sender.sendMessage(CC.chat("&c&l(!) &cUnable to load profile, try again?"));
            return;
        }

        CreditsHistoryMenu creditsHistoryMenu = new CreditsHistoryMenu(instance);
        creditsHistoryMenu.openCreditsHistoryMenu(sender, profile);
    }

    @SubCommand(label = "giftcards", parent = "staffcredits", aliases = {"giftcard"}, permission = "staffcredits.giftcards")
    public void executeStaffCreditsGiftcards(Player sender){
        Profile profile = instance.getStaffCreditsAPI().getProfileHandler().getProfile(sender.getUniqueId());

        if(profile == null){
            sender.sendMessage(CC.chat("&c&l(!) &cUnable to load profile, try again?"));
            return;
        }

        GiftcardMenu menu = new GiftcardMenu(instance);
        menu.openGiftcardMenu(sender, profile);
    }

    @SubCommand(label = "withdraw", parent = "staffcredits", aliases = {"redeem"}, permission = "staffcredits.giftcards")
    public void executeStaffCreditsWithdraw(Player sender){
        Profile profile = instance.getStaffCreditsAPI().getProfileHandler().getProfile(sender.getUniqueId());

        if(profile == null){
            sender.sendMessage(CC.chat("&c&l(!) &cUnable to load profile, try again?"));
            return;
        }

        CreditsWithdrawalMenu menu = new CreditsWithdrawalMenu(instance);
        menu.openWithdrawalMenu(sender, profile, 0);
    }
}
