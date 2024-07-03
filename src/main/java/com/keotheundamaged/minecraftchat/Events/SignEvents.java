package com.keotheundamaged.minecraftchat.Events;

import com.keotheundamaged.minecraftchat.Helpers.BannedWordsHelper;
import com.keotheundamaged.minecraftchat.Helpers.DiscordHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignEvents implements Listener {
    private final DiscordHelper discord;
    public SignEvents() {
        this.discord = new DiscordHelper(false);
    }
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        String signContent = String.join(" ", event.getLines()); // get content of sign being changed

        BannedWordsHelper bannedWordsHelper = new BannedWordsHelper();
        String result = bannedWordsHelper.checkForBannedWords(signContent);

        if (result != null) {
            event.setCancelled(true);
            this.discord.sendReportMessage(player, signContent, "sign", result);
        }
    }
}
