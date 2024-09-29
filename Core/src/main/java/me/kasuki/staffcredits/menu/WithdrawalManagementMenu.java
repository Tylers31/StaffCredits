package me.kasuki.staffcredits.menu;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.google.common.collect.Lists;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.menu.SGMenu;
import me.kasuki.staffcredits.StaffCreditsPlugin;
import me.kasuki.staffcredits.api.profile.Profile;
import me.kasuki.staffcredits.api.profile.withdrawals.WithdrawRequest;
import me.kasuki.staffcredits.utilities.data.NumFormatter;
import me.kasuki.staffcredits.utilities.date.DateTimeFormats;
import me.kasuki.staffcredits.utilities.enums.WithdrawalStates;
import me.kasuki.staffcredits.utilities.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WithdrawalManagementMenu {
    public StaffCreditsPlugin instance;

    public WithdrawalManagementMenu(StaffCreditsPlugin instance) {
        this.instance = instance;
    }

    public void openManagementMenu(Player player){
        SGMenu managementMenu = instance.getSpiGUI().create("Withdrawal Management", 5);
        managementMenu.setAutomaticPaginationEnabled(true);

        Collection<Profile> activeProfiles = instance.getStaffCreditsAPI().getProfileHandler().getProfiles();
        List<WithdrawRequest> withdrawRequests = new ArrayList<>();

        activeProfiles.forEach(profile -> {
            withdrawRequests.addAll(profile.getWithdrawRequests());
        });

        withdrawRequests.forEach(withdrawRequest -> {
            if(withdrawRequest.getState().equals(WithdrawalStates.PENDING)){
                managementMenu.addButton(getRequestButton(player, withdrawRequest));
            }
        });

        player.openInventory(managementMenu.getInventory());
    }

    public SGButton getRequestButton(Player player, WithdrawRequest request){
        OfflinePlayer requester = Bukkit.getOfflinePlayer(request.getOwner());

        return new SGButton(new ItemBuilder(XMaterial.BOOK)
                .setName("&2&l" + requester.getName() + "'s Withdrawal Request")
                .setLore(Lists.newArrayList(
                        "&8&m-----------------------------",
                        "&a&lAmount: &f" + NumFormatter.formatToUSD(request.getAmount()),
                        "&a&lDate: &f" + DateTimeFormats.DAY_MTH_HR_MIN_SECS.format(request.getDate()),
                        "&a&lState: " + request.getState(),
                        "",
                        "&7Right-Click to accept this request",
                        "&7Left-Click to decline this request",
                        "&8&m-----------------------------"
                ))
                .build()).withListener((InventoryClickEvent event) -> {
                    if(event.getClick().equals(ClickType.LEFT)){
                        request.setState(WithdrawalStates.DECLINED);
                        openManagementMenu(player);
                        player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1, 1);
                    }

                    if(event.getClick().equals(ClickType.RIGHT)){
                        // Handle
                    }
        });
    }
}
