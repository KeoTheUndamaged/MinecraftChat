package com.keotheundamaged.minecraftchat.Events;

import com.keotheundamaged.minecraftchat.Common.Config.BannedWordsConfigManager;
import com.keotheundamaged.minecraftchat.Common.Config.DiscordConfigManager;
import com.keotheundamaged.minecraftchat.Common.Connectors.DiscordConnector;
import com.keotheundamaged.minecraftchat.Common.Helpers.BannedWordsHelper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Handles sign-related events for the Minecraft plugin.
 */
public class SignEvents implements Listener {
    private final BannedWordsConfigManager bannedWordConfigManger;
    private final JavaPlugin plugin;
    private final DiscordConnector discord;
    private final String chatChannelId;
    private final String reportChannelId;

    /**
     * Constructs a SignEvents instance with the specified plugin and banned words configuration manager.
     *
     * @param plugin                  the JavaPlugin instance
     * @param bannedWordConfigManger  the manager for banned words configuration
     */
    public SignEvents(
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
     * Handles the SignChangeEvent to check for banned words in messages set on signs.
     *
     * @param event  the sign change event
     */
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String signContent = String.join(" ", event.getLines()); // get content of sign being changed
        if (signContent.isEmpty()) {return;} // if sign content is empty. Ignore.
        Player player = event.getPlayer(); // get player changing sign content

        FileConfiguration config = bannedWordConfigManger.getConfig();
        List<String> bannedWordsList = config.getStringList("bannedWords");

        BannedWordsHelper bannedWordsHelper = new BannedWordsHelper();
        String result = bannedWordsHelper.checkForBannedWords(signContent, bannedWordsList);
        if (result != null) {
            event.setCancelled(true);
            plugin.getLogger().info(String.format("%s [%s] used a banned word (%s) in %s on a sign",
                    player.getName(), player.getUniqueId(), result, signContent));
            discord.sendModLogToChannel(
                    reportChannelId,
                    player,
                    "sign",
                    result,
                    signContent,
                    chatChannelId);
        }
    }
}
