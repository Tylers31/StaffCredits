package me.kasuki.staffcredits.menu;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.google.common.collect.Lists;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import me.kasuki.staffcredits.StaffCredits;
import me.kasuki.staffcredits.api.profile.Profile;
import me.kasuki.staffcredits.utilities.CC;
import me.kasuki.staffcredits.utilities.num.NumFormatter;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

public class CreditsWithdrawalMenu {
    public StaffCredits instance;

    public CreditsWithdrawalMenu(StaffCredits instance) {
        this.instance = instance;
    }

    // Idea is to have a button that when clicked closes the ui, asks in chat for the amount, when the amount is wsent in chat, it opens the ui back up and puts it as the amount
    // Other button can be set it to 0 or something idk
    // Middle button displays the amount selected and the amount the player has (balance) and then a lore saying left/right click to confirm or cancel

    public void openWithdrawalMenu(Player player, Profile profile, double withdrawalAmount){
        SGMenu creditsWithdrawalMenu = instance.getSpiGUI().create("Credits Withdrawal", 1);

        for(int i = 0; i<9; i++){
            creditsWithdrawalMenu.setButton(i, fillerButton);
        }

        creditsWithdrawalMenu.setButton(2, amountButton);
        creditsWithdrawalMenu.setButton(4, getConfirmButton(withdrawalAmount, profile));

        player.openInventory(creditsWithdrawalMenu.getInventory());
    }

    SGButton fillerButton = new SGButton(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).name("&7").build());

    SGButton amountButton = new SGButton(new ItemBuilder(XMaterial.ANVIL.parseItem())
            .name("&e&lWithdrawal Amount")
            .lore("&7Click to input a withdrawal amount for your request!")
            .build())
            .withListener((InventoryClickEvent event) -> {
                Player player = (Player) event.getWhoClicked();
                instance.pendingRequests.add(player.getUniqueId());
                player.closeInventory();

                player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1, 1);
                player.sendMessage(CC.chat("&a&l(!) &aType in chat how many credits you wish to withdraw!"));
    });

    public SGButton getConfirmButton(double withdrawalAmount, Profile profile){
        SGButton submitButton = new SGButton(new ItemBuilder(XMaterial.EMERALD.parseItem())
                .name("&a&lSubmit Request")
                .lore(Lists.newArrayList(
                        "",
                        "&2&l* &aRequested Amount:&f " + NumFormatter.formatToUSD(withdrawalAmount),
                        "&2&l* &aYour Balance:&f "  + NumFormatter.formatToUSD(profile.getCredits()),
                        "",
                        "&7Click to submit your withdrawal request!"
                ))
                .enchant(Enchantment.LUCK, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build())
                .withListener((InventoryClickEvent event) -> {
                    Player player = (Player) event.getWhoClicked();

                    if(profile.getCredits() < withdrawalAmount){
                        player.sendMessage(CC.chat("&c&l(!) &cInsufficient credits!"));
                        player.playSound(player.getLocation(), XSound.BLOCK_LAVA_POP.parseSound(), 1, 1);
                        return;
                    }
                    if(withdrawalAmount <= 0){
                        player.sendMessage(CC.chat("&c&l(!) &cPlease input a withdrawal amount greater than 0!"));
                        player.playSound(player.getLocation(), XSound.BLOCK_LAVA_POP.parseSound(), 1, 1);
                        return;
                    }


                    player.sendMessage(CC.chat("&a&L(!) &aSubmitted a request for credit withdrawal!"));
                    player.sendMessage(CC.chat("&7&oPlease wait for management to approve your request!"));
                    player.playSound(player.getLocation(), XSound.BLOCK_ANVIL_USE.parseSound(), 1, 1);
                    player.closeInventory();
                });

        return submitButton;
    }
}
