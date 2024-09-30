package me.kasuki.staffcredits.menu;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.google.common.collect.Lists;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.menu.SGMenu;
import me.kasuki.staffcredits.StaffCreditsPlugin;
import me.kasuki.staffcredits.api.profile.Profile;
import me.kasuki.staffcredits.api.profile.withdrawals.GiftCard;
import me.kasuki.staffcredits.api.profile.withdrawals.WithdrawRequest;
import me.kasuki.staffcredits.utilities.CC;
import me.kasuki.staffcredits.utilities.data.NumFormatter;
import me.kasuki.staffcredits.utilities.date.DateTimeFormats;
import me.kasuki.staffcredits.utilities.enums.WithdrawalStates;
import me.kasuki.staffcredits.utilities.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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

        withdrawRequests.sort(Comparator.comparingLong(WithdrawRequest::getDate).reversed());

        withdrawRequests.forEach(withdrawRequest -> {
            managementMenu.addButton(getRequestButton(player, withdrawRequest));
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
                        "&a&lState: " + request.getState().display,
                        "",
                        "&7Right-Click to accept this request",
                        "&7Left-Click to decline this request",
                        "&8&m-----------------------------"
                ))
                .build()).withListener((InventoryClickEvent event) -> {
                    Profile profile = instance.getStaffCreditsAPI().getProfileHandler().getProfile(player.getUniqueId());

                    if(!request.getState().equals(WithdrawalStates.PENDING)){
                        return;
                    }

                    if(event.getClick().equals(ClickType.LEFT)){
                        profile.setCredits(profile.getCredits() + request.getAmount());

                        request.setState(WithdrawalStates.DECLINED);
                        player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1, 1);
                        player.sendMessage(CC.chat("&a&l(!) &aYou have &c&ndeclined&a " + player.getName() + "'s withdrawal request!"));

                        openManagementMenu(player);
                    }

                    if(event.getClick().equals(ClickType.RIGHT)){
                        String giftcardCode = null;

                        try {
                            giftcardCode = instance.getStaffCreditsAPI().getTebexAPI().createVoucher(request.getAmount(), 30, "StaffCredits Plugin");
                        }catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(giftcardCode == null){
                            return;
                        }

                        request.setState(WithdrawalStates.ACCEPTED);
                        player.sendMessage(CC.chat("&a&l(!) &aYou have &naccepted&a " + player.getName() + "'s withdrawal request!"));
                        player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1, 1);
                        openManagementMenu(player);

                        GiftCard giftCard = new GiftCard(giftcardCode, request.getAmount(), request.getOwner());
                        profile.getGiftcards().add(giftCard);
                    }
        });
    }
}
