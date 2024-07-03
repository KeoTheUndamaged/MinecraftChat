package com.keotheundamaged.minecraftchat.Events;

import com.keotheundamaged.minecraftchat.Helpers.BannedWordsHelper;
import com.keotheundamaged.minecraftchat.Helpers.DiscordHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvents implements Listener {
    private final DiscordHelper discord;
    public ChatEvents() {
        this.discord = new DiscordHelper(false);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        BannedWordsHelper bannedWordsHelper = new BannedWordsHelper();
        String result = bannedWordsHelper.checkForBannedWords(message);
        if (result != null) {
            event.setCancelled(true);
            discord.sendReportMessage(player, message, "chat", result);
            return;
        }
        discord.sendChatMessage(String.format("%s >> %s", player.getName(), message));
    }
}
