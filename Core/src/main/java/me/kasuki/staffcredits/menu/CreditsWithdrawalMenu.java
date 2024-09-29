package me.kasuki.staffcredits.menu;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.google.common.collect.Lists;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import me.kasuki.staffcredits.StaffCreditsPlugin;
import me.kasuki.staffcredits.api.profile.Profile;
import me.kasuki.staffcredits.api.profile.withdrawals.WithdrawRequest;
import me.kasuki.staffcredits.redis.packet.impl.NotificationPacket;
import me.kasuki.staffcredits.utilities.CC;
import me.kasuki.staffcredits.utilities.data.NumFormatter;
import me.kasuki.staffcredits.utilities.enums.WithdrawalStates;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

public class CreditsWithdrawalMenu {
    public StaffCreditsPlugin instance;

    public CreditsWithdrawalMenu(StaffCreditsPlugin instance) {
        this.instance = instance;
    }

    // Last button will be withdrawal history

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
                    profile.setCredits(profile.getCredits() - withdrawalAmount);
                    profile.getWithdrawRequests().add(new WithdrawRequest(player.getUniqueId(), WithdrawalStates.PENDING, withdrawalAmount, System.currentTimeMillis()));

                    instance.getRedisHandler().publish(new NotificationPacket("&4&l[MANAGEMENT]&a " + player.getName() + " has submitted a credit withdrawal request!"));

                    player.closeInventory();
                });

        return submitButton;
    }
}
