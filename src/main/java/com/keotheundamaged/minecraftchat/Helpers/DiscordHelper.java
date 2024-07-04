package com.keotheundamaged.minecraftchat.Helpers;

import com.keotheundamaged.minecraftchat.Listeners.DiscordChatListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.Instant;

public class DiscordHelper {
    private static DiscordHelper instance;

    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration config;

    private final JDA jda;
    private String TOKEN;
    private String CHAT_CHANNEL_ID;
    private String REPORT_CHANNEL_ID;

    private DiscordHelper(JavaPlugin plugin) {
        this.plugin = plugin;
        getOrCreateDataFile();
        loadData();

        JDABuilder jdaBuilder = JDABuilder.createDefault(this.TOKEN)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new DiscordChatListener(this.CHAT_CHANNEL_ID));

        this.jda = jdaBuilder.build();
        try {
            this.jda.awaitReady();
        } catch (InterruptedException e) {
            Bukkit.getServer().getLogger().warning(String.format(
                    "Failed to connect to discord: %s",
                    e.getMessage()
            ));
        }
    }

    public static synchronized DiscordHelper getInstance(JavaPlugin plugin) {
        if (instance == null) {
            instance = new DiscordHelper(plugin);
        }
        return instance;
    }

    public void getOrCreateDataFile() {
        String filename = "discord.yml";
        this.file = new File(this.plugin.getDataFolder(), filename);
        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            this.plugin.saveResource(filename, false);
        }
    }

    public void loadData() {
        if (this.file.exists()) {
            this.config = YamlConfiguration.loadConfiguration(this.file);
            this.CHAT_CHANNEL_ID = this.config.getString("CHAT_CHANNEL_ID");
            this.REPORT_CHANNEL_ID = this.config.getString("REPORT_CHANNEL_ID");
            this.TOKEN = this.config.getString("TOKEN");
        }
    }

    public void saveData(boolean reload) {
        try {
            this.config.set("CHAT_CHANNEL_ID", this.CHAT_CHANNEL_ID);
            this.config.set("REPORT_CHANNEL_ID", this.REPORT_CHANNEL_ID);
            this.config.set("TOKEN", this.TOKEN);
            this.config.save(this.file);
            if (reload) {
                loadData();
            }
        } catch (IOException e) {
            this.plugin.getLogger().severe(String.format("Failed to save Banned words config to %s", this.file.getName()));
        }
    }

    public void sendChatMessage(String message) {
        TextChannel channel = jda.getTextChannelById(this.CHAT_CHANNEL_ID);
        if (channel == null) {
            Bukkit.getServer().getLogger().warning("Failed to connect to discord. Channel not found.");
            return;
        }
        try {
        channel.sendMessage(message).queue();
        } catch (Exception e) {
            Bukkit.getServer().getLogger().severe(String.format("Failed to send message to Discord: %s", e.getMessage()));
        }
    }

    public void sendReportMessage(Player player, String message, String textArea, String violation) {
        TextChannel channel = jda.getTextChannelById(this.REPORT_CHANNEL_ID);
        if (channel == null) {
            Bukkit.getServer().getLogger().warning("Failed to connect to discord. Channel not found.");
            return;
        }
        Instant now = Instant.now();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(getEmbedTitle(textArea))
                .setAuthor(player.getName())
                .setDescription(message)
                .setColor(Color.RED)
                .addField("Reason", "Banned word", true)
                .addField("Violation", violation, true)
                .setTimestamp(now)
                .setFooter(String.format("uuid: %s", player.getUniqueId()
                ));
        try {
            channel.sendMessageEmbeds(embed.build()).queue();
        } catch (Exception e) {
            Bukkit.getServer().getLogger().severe(String.format("Failed to send message to Discord: %s", e.getMessage()));
        }
    }

    private String getEmbedTitle(String textArea) {
        switch (textArea) {
            case "chat":
                return String.format("Message deleted from <#%s>", this.CHAT_CHANNEL_ID);
            case "anvil":
                return "Item rename blocked due to banned word";
            case "sign":
                return "Sign change blocked due to banned word";
            case "book":
                return "Book change blocked due to banned word";
            default:
                return "Banned word blocked in Minecraft";
        }
    }
}
