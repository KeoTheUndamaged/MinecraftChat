package com.keotheundamaged.minecraftchat.Events;

import com.keotheundamaged.minecraftchat.Common.Config.BannedWordsConfigManager;
import com.keotheundamaged.minecraftchat.Common.Config.DiscordConfigManager;
import com.keotheundamaged.minecraftchat.Common.Connectors.DiscordConnector;
import com.keotheundamaged.minecraftchat.Common.Helpers.BannedWordsHelper;
import com.keotheundamaged.minecraftchat.Common.Helpers.WordExtractorHelper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Handles events related to book editing for the Minecraft plugin.
 */
public class BookEvents implements Listener {
    private final BannedWordsConfigManager bannedWordConfigManger;
    private final JavaPlugin plugin;
    private final DiscordConnector discord;
    private final String chatChannelId;
    private final String reportChannelId;

    /**
     * Constructs a BookEditEventHandler with the specified plugin instance.
     *
     * @param plugin the JavaPlugin instance
     */
    public BookEvents(
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

    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        Player player = event.getPlayer();
        List<String> pages = event.getNewBookMeta().getPages();
        String pageContent = String.join(" ", pages);

        FileConfiguration config = bannedWordConfigManger.getConfig();
        List<String> bannedWordsList = config.getStringList("bannedWords");

        BannedWordsHelper bannedWordsHelper = new BannedWordsHelper();
        String result = bannedWordsHelper.checkForBannedWords(pageContent, bannedWordsList);
        WordExtractorHelper wordExtractorHelper = new WordExtractorHelper();
        String contentToLog = wordExtractorHelper.getSurroundingWords(pageContent, result, 10);
        if (result != null) {
            event.setCancelled(true);
            plugin.getLogger().info(String.format("%s [%s] used a banned word (%s) in %s on a sign",
                    player.getName(), player.getUniqueId(), result, contentToLog));
            discord.sendModLogToChannel(
                    reportChannelId,
                    player,
                    "book",
                    result,
                    contentToLog,
                    chatChannelId);
        }
    }
}
