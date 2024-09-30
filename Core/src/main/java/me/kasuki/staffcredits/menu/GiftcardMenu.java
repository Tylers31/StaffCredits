package me.kasuki.staffcredits.menu;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.Lists;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import me.kasuki.staffcredits.StaffCreditsPlugin;
import me.kasuki.staffcredits.api.profile.Profile;
import me.kasuki.staffcredits.api.profile.withdrawals.GiftCard;
import me.kasuki.staffcredits.api.profile.withdrawals.WithdrawRequest;
import me.kasuki.staffcredits.utilities.data.NumFormatter;
import me.kasuki.staffcredits.utilities.date.DateTimeFormats;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class GiftcardMenu {
    public StaffCreditsPlugin instance;

    public GiftcardMenu(StaffCreditsPlugin instance) {
        this.instance = instance;
    }

    public void openGiftcardMenu(Player player, Profile profile){

        SGMenu sgMenu = instance.getSpiGUI().create("Your Giftcards", 1);
        sgMenu.setAutomaticPaginationEnabled(true);

        List<GiftCard> giftCards = profile.getGiftcards() != null ? profile.getGiftcards() : new ArrayList<>();
        giftCards.sort(Comparator.comparing(GiftCard::getReceivedDate, Comparator.comparingLong(Date::getTime)).reversed());

        giftCards.forEach(giftCard -> {
            sgMenu.addButton(getGiftcardButton(player, giftCard));
        });

        player.openInventory(sgMenu.getInventory());
    }

    public SGButton getGiftcardButton(Player player, GiftCard giftCard){
        Profile profile = instance.getStaffCreditsAPI().getProfileHandler().getProfile(player.getUniqueId());

        if(profile == null){
            return null;
        }

        return new SGButton(new ItemBuilder(XMaterial.PAPER.parseItem())
                .name("&2&lGiftcard")
                .lore(Lists.newArrayList(
                        "&8&m-----------------------------",
                        "&a&lCode: &f" + giftCard.getCode(),
                        "&a&lAmount: &f" + NumFormatter.formatToUSD(giftCard.getAmount()),
                        "&a&lAmount Left: &f" + NumFormatter.formatToUSD(giftCard.getAmountLeft()),
                        "&a&lReceived Date: &f" + DateTimeFormats.DAY_MONTH_YEAR_HR_MIN_SECS.format(giftCard.getReceivedDate()),
                        "&a&lExpiration Date: &f" + DateTimeFormats.DAY_MONTH_YEAR_HR_MIN_SECS.format(giftCard.getExpiryDate()),
                        "",
                        "&7Contact management if you have any questions!",
                        "&8&m-----------------------------"
                ))
                .enchant(XEnchantment.KNOCKBACK.getEnchant(), 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build());
    }
}
