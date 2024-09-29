package me.kasuki.staffcredits.redis.packet.impl;

import com.cryptomorin.xseries.XSound;
import lombok.RequiredArgsConstructor;
import me.kasuki.staffcredits.redis.packet.AbstractPacket;
import me.kasuki.staffcredits.utilities.CC;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class NotificationPacket extends AbstractPacket {
    private final String message;


    @Override
    public void onSend() {

    }

    @Override
    public void onReceive() {
        for(Player player : Bukkit.getServer().getOnlinePlayers()){
            if(!player.isOp()){
                return;
            }

            player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1, 1);
            player.sendMessage(CC.chat(message));
        }
    }
}
