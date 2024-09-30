package me.kasuki.staffcredits.menu;

import com.cryptomorin.xseries.XMaterial;
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
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CreditsHistoryMenu {
    public StaffCreditsPlugin instance;

    public CreditsHistoryMenu(StaffCreditsPlugin instance) {
        this.instance = instance;
    }

    public void openCreditsHistoryMenu(Player player, Profile profile){
        SGMenu menu = instance.getSpiGUI().create("Withdrawal History", 1);

        getHistoryButtons(profile).forEach((menu::addButton));
        menu.setAutomaticPaginationEnabled(true);

        player.openInventory(menu.getInventory());
    }

    public List<SGButton> getHistoryButtons(Profile profile) {
        List<SGButton> buttons = new ArrayList<>();
        int iteration = 1;

        List<WithdrawRequest> withdrawRequests = profile.getWithdrawRequests() != null ? profile.getWithdrawRequests() : new ArrayList<>();
        withdrawRequests.sort(Comparator.comparingLong(WithdrawRequest::getDate).reversed());

        for (WithdrawRequest withdrawRequest : withdrawRequests) {
            SGButton historyButton = new SGButton(
                    new ItemBuilder(XMaterial.EMERALD)
                            .setName("&2&lRequest #" + iteration)
                            .setLore(Lists.newArrayList(
                                    "&8&m-----------------------------",
                                    "&a&lRequester: &f" + Bukkit.getOfflinePlayer(profile.getUniqueId()).getName(),
                                    "&a&lAmount: &f" + NumFormatter.formatToUSD(withdrawRequest.getAmount()),
                                    "&a&lDate: &f" + DateTimeFormats.DAY_MONTH_YEAR_HR_MIN_SECS.format(withdrawRequest.getDate()),
                                    "",
                                    "&a&lState: " + withdrawRequest.getState().display,
                                    "&8&m-----------------------------"
                            ))
                            .build()
            );

            buttons.add(historyButton);
            iteration++;
        }

        return buttons;
    }

}
