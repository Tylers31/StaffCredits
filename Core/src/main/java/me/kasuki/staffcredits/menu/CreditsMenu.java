package me.kasuki.staffcredits.menu;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.Lists;
import com.samjakob.spigui.SpiGUI;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import me.kasuki.staffcredits.StaffCredits;
import me.kasuki.staffcredits.api.profile.Profile;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

public class CreditsMenu {
    public StaffCredits instance;

    public CreditsMenu(StaffCredits instance) {
        this.instance = instance;
    }

    public void openCreditsMenu(Player player, Profile profile){
        SGMenu creditsMenu = instance.getSpiGUI().create("Credits", 1);

        for(int i = 0; i<9; i++){
            creditsMenu.setButton(i, fillerButton);
        }

        creditsMenu.setButton(4, getPlayerButton(player, profile));
        creditsMenu.setButton(2, helpButton);
        creditsMenu.setButton(6, withdrawButton);

        player.openInventory(creditsMenu.getInventory());
    }


    SGButton fillerButton = new SGButton(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial()).name("&7").build());

    SGButton withdrawButton = new SGButton(new ItemBuilder(XMaterial.BARRIER.parseMaterial())
            .name("&c&lWithdraw Credits")
            .lore(Lists.newArrayList(
                    "&7Click to input a request to withdraw staff credits"
            ))
            .enchant(Enchantment.LUCK, 1)
            .flag(ItemFlag.HIDE_ENCHANTS)
            .build())
            .withListener((InventoryClickEvent event) -> {
                // TODO: Implement withdrawal logic
            });

    SGButton helpButton = new SGButton(new ItemBuilder(XMaterial.WRITABLE_BOOK.parseItem())
            .name("&a&lStaff Credits")
            .lore(Lists.newArrayList(
                    "&7Staff credits are an ingame currency",
                    "&7for staff to use in order to be compensated",
                    "&7for their hard work moderating the server!",
                    "",
                    "&7To redeem your staff credits, simply",
                    "&7select the redeem button in this menu,",
                    "&7input the amount you wish to redeem,",
                    "&7and then wait for management to respond",
                    "&7to your request!"
                    ))
            .enchant(Enchantment.LUCK, 1)
            .flag(ItemFlag.HIDE_ENCHANTS)
            .build());

    public SGButton getPlayerButton(Player player, Profile profile){
        return new SGButton(new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem())
                        .skullOwner(player.getName())
                        .name("&2&l" + player.getName() + "'s Staff Credits")
                        .lore(Lists.newArrayList(
                                "",
                                "&2&l* &aCurrent Credits: &f" + profile.getCredits(),
                                "&2&l* &aLifetime Credits: &f" + profile.getLifetimeCredits(),
                                "",
                                "&7Click to view the leaderboard"
                        ))
                        .build());
    }
}
