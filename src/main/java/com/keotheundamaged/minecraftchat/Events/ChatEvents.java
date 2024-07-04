package com.keotheundamaged.minecraftchat.Events;

import com.keotheundamaged.minecraftchat.Helpers.BannedWordsHelper;
import com.keotheundamaged.minecraftchat.Helpers.DiscordHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatEvents implements Listener {
    private final DiscordHelper discord;
    private final BannedWordsHelper bannedWordsHelper;

    public ChatEvents() {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("MinecraftChat");
        this.discord = DiscordHelper.getInstance(plugin);
        this.bannedWordsHelper = BannedWordsHelper.getInstance(plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        String result = bannedWordsHelper.checkForBannedWords(message);
        if (result != null) {
            event.setCancelled(true);
            discord.sendReportMessage(player, message, "chat", result);
            return;
        }
        discord.sendChatMessage(String.format("%s >> %s", player.getName(), message));
    }
}
