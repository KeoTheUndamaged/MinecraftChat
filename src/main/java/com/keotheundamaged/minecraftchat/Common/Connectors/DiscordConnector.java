package com.keotheundamaged.minecraftchat.Common.Connectors;

import com.keotheundamaged.minecraftchat.Discord.ChatListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Manages the connection to Discord and allows sending messages to Discord channels.
 */
public class DiscordConnector {
    private final JDA jda;

    /**
     * Constructs a DiscordConnector with the specified token and channel ID for the listener.
     *
     * @param token             the bot token for authentication
     * @param listenerChannelId the ID of the channel to listen for messages
     */
    public DiscordConnector(String token, String listenerChannelId) {
        jda = JDABuilder.createDefault(token)
                .addEventListeners(new ChatListener(listenerChannelId)) // Adds listener to get messages from Discord
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)           // Allows bot to read messages from Discord
                .build();
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            Bukkit.getServer().getLogger().warning(String.format(
                    "Failed to connect to discord: %s",
                    e.getMessage()
            ));
        }
    }

    /**
     * Sends a message to the specified Discord channel.
     *
     * @param channelId the ID of the channel to send the message to
     * @param message   the message to send
     * @throws NullPointerException if the channel with the specified ID is not found
     */
    public void sendMessageToChannel(String channelId, String message) {
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            throw new NullPointerException(String.format("Channel with ID %s not found", channelId));
        }
        channel.sendMessage(message).queue();
    }

    public void sendModLogToChannel(
            String reportChannelId,
            Player player,
            String textArea,
            String violation,
            String message,
            String channelId
    ) {
        TextChannel channel = jda.getTextChannelById(reportChannelId);
        if (channel == null) {
            throw new NullPointerException(String.format("Channel with ID %s not found", reportChannelId));
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(getEmbedTitle(textArea, channelId))
                .setAuthor(player.getName())
                .setDescription(message)
                .setColor(Color.RED)
                .addField("Reason", "Banned word", true)
                .addField("Violation", violation, true)
                .setFooter("Minecraft Chat Plugin", null);
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    /**
     * Shuts down the JDA instance, disconnecting from Discord.
     */
    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    @Contract(pure = true)
    private @NotNull String getEmbedTitle(String textArea, String channelId) {
        String defaultMessage = "Message deleted from Minecraft";
        if (textArea == null || channelId == null) {
            return defaultMessage;
        }
        switch (textArea) {
            case "chat":
                return String.format("Message deleted from <#%s>", channelId);
            case "anvil":
                return "Item rename blocked due to banned word";
            case "sign":
                return "Sign change blocked due to banned word";
            case "book":
                return "Book edit blocked due to banned word";
        }

        return defaultMessage;
    }
}