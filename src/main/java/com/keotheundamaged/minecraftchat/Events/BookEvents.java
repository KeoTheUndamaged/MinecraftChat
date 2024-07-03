package com.keotheundamaged.minecraftchat.Events;

import com.keotheundamaged.minecraftchat.Helpers.BannedWordsHelper;
import com.keotheundamaged.minecraftchat.Helpers.DiscordHelper;
import com.keotheundamaged.minecraftchat.Helpers.WordExtractorHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;

public class BookEvents implements Listener {
    private final DiscordHelper discord;
    public BookEvents() {
        this.discord = new DiscordHelper(false);
    }
    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        Player player = event.getPlayer();
        String content = String.join(" ", event.getNewBookMeta().getPages());
        String title = event.getNewBookMeta().getTitle();

        BannedWordsHelper bannedWordsHelper = new BannedWordsHelper();
        String result = bannedWordsHelper.checkForBannedWords(content);
        String titleResult = bannedWordsHelper.checkForBannedWords(title);

        if (result != null || titleResult != null) {
            String violation = getBannedWord(result, titleResult);
            event.setCancelled(true);
            WordExtractorHelper wordExtractor = new WordExtractorHelper();
            String trimmedContent = wordExtractor.getSurroundingWords(content, violation, 10);
            this.discord.sendReportMessage(player, trimmedContent, "book", violation);
        }
    }

    private String getBannedWord(String contents, String title) {
        if (contents != null) return contents;
        return title;
    }
}
