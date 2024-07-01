package com.keotheundamaged.minecraftchat.Events;

import com.keotheundamaged.minecraftchat.Common.Config.BannedWordsConfigManager;
import com.keotheundamaged.minecraftchat.Common.Config.DiscordConfigManager;
import com.keotheundamaged.minecraftchat.Common.Connectors.DiscordConnector;
import com.keotheundamaged.minecraftchat.Common.Helpers.BannedWordsHelper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Handles chat-related events for the Minecraft plugin.
 */
public class ChatEvents implements Listener {
    private final BannedWordsConfigManager bannedWordConfigManger;
    private final JavaPlugin plugin;
    private final DiscordConnector discord;
    private final String chatChannelId;
    private final String reportChannelId;

    /**
     * Constructs a ChatEvents instance with the specified plugin and banned words configuration manager.
     *
     * @param plugin                  the JavaPlugin instance
     * @param bannedWordConfigManger  the manager for banned words configuration
     */
    public ChatEvents(
            JavaPlugin plugin,
            BannedWordsConfigManager bannedWordConfigManger,
            DiscordConfigManager discordConfigManager,
            DiscordConnector discordConnector
    ) {
        this.plugin = plugin;
        this.bannedWordConfigManger = bannedWordConfigManger;
        this.discord = discordConnector;
        this.chatChannelId = discordConfigManager.getConfig().getString("chatChannel");
        this.reportChannelId = discordConfigManager.getConfig().getString("reportChannel");
    }

    /**
     * Handles the AsyncPlayerChatEvent to check for banned words in player messages.
     *
     * @param event  the asynchronous player chat event
     */
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer(); // get player sending chat message.
        String message = event.getMessage(); // get sent chat message.

        FileConfiguration config = bannedWordConfigManger.getConfig();
        List<String> bannedWordsList = config.getStringList("bannedWords");

        BannedWordsHelper bannedWordsHelper = new BannedWordsHelper();
        String result = bannedWordsHelper.checkForBannedWords(message, bannedWordsList);
        if (result != null) {
            event.setCancelled(true);
            String report = String.format("%s [%s] used a banned word (%s) in %s in chat",
                    player.getName(), player.getUniqueId(), result, message);

            plugin.getLogger().info(report);

            discord.sendModLogToChannel(
                    reportChannelId,
                    player,
                    "chat",
                    result,
                    message,
                    chatChannelId);
        }
        if (result == null) {
            try {
                discord.sendMessageToChannel(
                        chatChannelId,
                        String.format("%s: %s", player.getName(), message)
                );
            } catch (Exception e) {
                plugin.getLogger().warning(String.format(
                        "encountered an error sending message to Discord: %s",
                        e.getMessage()
                ));
        }
        }
    }
}
