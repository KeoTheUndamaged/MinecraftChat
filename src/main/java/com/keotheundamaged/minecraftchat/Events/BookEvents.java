package com.keotheundamaged.minecraftchat.Events;

import com.keotheundamaged.minecraftchat.Helpers.BannedWordsHelper;
import com.keotheundamaged.minecraftchat.Helpers.DiscordHelper;
import com.keotheundamaged.minecraftchat.Helpers.WordExtractorHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Event listener for player book edit events in Minecraft.
 */
public class BookEvents implements Listener {
    private final DiscordHelper discord;
    private final BannedWordsHelper bannedWordsHelper;

    /**
     * Constructor for BookEvents.
     * Initializes the DiscordHelper and BannedWordsHelper instances.
     */
    public BookEvents() {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("MinecraftChat");
        this.discord = DiscordHelper.getInstance(plugin);
        this.bannedWordsHelper = BannedWordsHelper.getInstance(plugin);
    }

    /**
     * Event handler for PlayerEditBookEvent.
     * Checks the book content and title for banned words and cancels the event if any are found.
     * Sends a report to Discord if a banned word is found.
     *
     * @param event The PlayerEditBookEvent.
     */
    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        Player player = event.getPlayer();
        String content = String.join(" ", event.getNewBookMeta().getPages());
        String title = event.getNewBookMeta().getTitle();

        // Check for banned words in the book content and title
        String result = bannedWordsHelper.checkForBannedWords(content);
        String titleResult = bannedWordsHelper.checkForBannedWords(title);

        if (result != null || titleResult != null) {
            String violation = getBannedWord(result, titleResult);
            event.setCancelled(true); // Cancel the event if a banned word is found
            WordExtractorHelper wordExtractor = new WordExtractorHelper();
            String trimmedContent = wordExtractor.getSurroundingWords(content, violation, 10);
            this.discord.sendReportMessage(player, trimmedContent, "book", violation); // Send a report to Discord
        }
    }

    /**
     * Helper method to determine which banned word was found.
     *
     * @param contents The banned word found in the contents.
     * @param title    The banned word found in the title.
     * @return The banned word.
     */
    private String getBannedWord(String contents, String title) {
        if (contents != null) return contents;
        return title;
    }
}