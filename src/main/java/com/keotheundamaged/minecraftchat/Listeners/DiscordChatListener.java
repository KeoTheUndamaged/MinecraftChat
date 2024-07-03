package com.keotheundamaged.minecraftchat.Listeners;

import com.keotheundamaged.minecraftchat.Helpers.BannedWordsHelper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

/**
 * Listens for messages in a specific Discord channel and broadcasts them to the Minecraft server chat.
 */
public class DiscordChatListener extends ListenerAdapter {
    private final String channelId;

    /**
     * Constructs a ChatListener with the specified channel ID.
     *
     * @param channelId the ID of the Discord channel to listen for messages
     */
    public DiscordChatListener(String channelId) {
        this.channelId = channelId;
    }

    /**
     * Handles the MessageReceivedEvent.
     * Checks if the message is from the specified channel and not from a bot, then broadcasts it to the Minecraft server chat.
     *
     * @param event the event triggered when a message is received
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getChannel().getId().equals(channelId)) {
            return; // If not correct channel: Ignore
        }
        if (event.getAuthor().isBot()) {
            return; // If sender is a bot: Ignore
        }
        if (event.isFromType(ChannelType.TEXT)) {
            Message message = event.getMessage();
            String content = message.getContentDisplay();
            System.out.println(content);

            BannedWordsHelper bannedWordsHelper = new BannedWordsHelper();
            String result = bannedWordsHelper.checkForBannedWords(content);
            if (result != null) return;
            Bukkit.getServer().broadcastMessage(String.format("[%s] %s", event.getAuthor().getEffectiveName(), content));
        }
    }
}