package com.keotheundamaged.minecraftchat.Common.Config;

import com.keotheundamaged.minecraftchat.Common.Helpers.BannedWordsHelper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Manages the banned words configuration for the Minecraft plugin.
 */
public class BannedWordsConfigManager {
    private final JavaPlugin plugin;
    private final String fileName = "banned-words.yml";
    private File dateFile;
    private FileConfiguration config;
    private String bannedWordsRegex;


    /**
     * Constructs a BannedWordsConfigManager with the specified plugin.
     *
     * @param plugin the JavaPlugin instance
     */
    public BannedWordsConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        createDataFile();
    }

    /**
     * Creates the data file for banned words configuration if it does not exist
     * and loads the configuration.
     */
    private void createDataFile() {
        dateFile = new File(plugin.getDataFolder(), fileName);
        if (!dateFile.exists()) {
            dateFile.getParentFile().mkdirs();
            plugin.saveResource(fileName, false);
        }
        config = YamlConfiguration.loadConfiguration(dateFile);
        BannedWordsHelper bannedWordsHelper = new BannedWordsHelper();
        bannedWordsRegex = bannedWordsHelper.generateBannedWordRegex(config.getStringList("bannedWords"));
    }

    /**
     * Loads the configuration data from the banned words file.
     * Logs a warning if the file does not exist.
     */
    public void loadData() {
        if (dateFile.exists()) {
            config = YamlConfiguration.loadConfiguration(dateFile);
            BannedWordsHelper bannedWordsHelper = new BannedWordsHelper();
            bannedWordsRegex = bannedWordsHelper.generateBannedWordRegex(config.getStringList("bannedWords"));
        } else {
            plugin.getLogger().warning("Failing Loading Banned Words: File does not exist.");
        }
    }

    /**
     * Saves the configuration data to the banned words file.
     * Logs a severe error if an IOException occurs.
     */
    public void saveData(boolean reload) {
        try {
            config.save(dateFile);
            if (reload) {
                loadData();
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failing Saving Banned Words", e);
        }
    }

    /**
     * Gets the current configuration object.
     *
     * @return the current FileConfiguration object
     */
    public FileConfiguration getConfig() {
        return this.config;
    }

    public String getBannedWordsRegex() {
        return this.bannedWordsRegex;
    }
}
