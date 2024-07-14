package com.keotheundamaged.minecraftchat.Helpers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to manage banned words in Minecraft chat.
 */
public class BannedWordsHelper {
    private static BannedWordsHelper instance;

    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration config;

    private List<String> EXACT_BANNED_WORDS;
    private List<String> WILDCARD_BANNED_WORDS;
    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
    private static final String IP_PATTERN = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b";
    private static final String MASK_LINK_PATTERN = "\\[(.*?)\\]\\(\\s*(https?:\\/\\/[^\\s)]+)\\s*\\)";

    private final List<Pattern> EXACT_BANNED_PATTERNS = new ArrayList<>();
    private final Map<Pattern, String> WILDCARD_BANNED_PATTERNS = new HashMap<>();
    private final Pattern EMAIL_COMPILED_PATTERN = Pattern.compile(EMAIL_PATTERN);
    private final Pattern IP_COMPILED_PATTERN = Pattern.compile(IP_PATTERN);
    private final Pattern MASKED_LINK_PATTERN = Pattern.compile(MASK_LINK_PATTERN);

    /**
     * Private constructor to initialize the BannedWordsHelper.
     *
     * @param plugin The JavaPlugin instance.
     */
    private BannedWordsHelper(JavaPlugin plugin) {
        this.plugin = plugin;
        getOrCreateDataFile();
        loadData();
        createPatterns();
    }

    /**
     * Gets the singleton instance of the BannedWordsHelper.
     *
     * @param plugin The JavaPlugin instance.
     * @return The BannedWordsHelper instance.
     */
    public static synchronized BannedWordsHelper getInstance(JavaPlugin plugin) {
        if (instance == null) {
            instance = new BannedWordsHelper(plugin);
        }
        return instance;
    }

    /**
     * Creates or loads the data file for banned words configuration.
     */
    private void getOrCreateDataFile() {
        String filename = "banned-words.yml";
        this.file = new File(this.plugin.getDataFolder(), filename);
        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            this.plugin.saveResource(filename, false);
        }
    }

    /**
     * Loads data from the configuration file.
     */
    private void loadData() {
        if (this.file.exists()) {
            this.config = YamlConfiguration.loadConfiguration(this.file);
            this.EXACT_BANNED_WORDS = this.config.getStringList("EXACT_BANNED_WORDS");
            this.WILDCARD_BANNED_WORDS = this.config.getStringList("WILDCARD_BANNED_WORDS");
            createPatterns();
        }
    }

    /**
     * Creates regex patterns for exact and wildcard banned words.
     */
    private void createPatterns() {
        this.EXACT_BANNED_PATTERNS.clear();
        for (String word : this.EXACT_BANNED_WORDS) {
            String regex = "\\b" + Pattern.quote(word) + "\\b";
            this.EXACT_BANNED_PATTERNS.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
        }

        this.WILDCARD_BANNED_PATTERNS.clear();
        for (String word : this.WILDCARD_BANNED_WORDS) {
            String regex = Pattern.quote(word) + ".*";
            this.WILDCARD_BANNED_PATTERNS.put(Pattern.compile(regex, Pattern.CASE_INSENSITIVE), word);
        }
    }

    /**
     * Saves data to the configuration file.
     */
    public void saveData() {
        try {
            this.config.set("EXACT_BANNED_WORDS", this.EXACT_BANNED_WORDS);
            this.config.set("WILDCARD_BANNED_WORDS", this.WILDCARD_BANNED_WORDS);
            this.config.save(this.file);
        } catch (IOException e) {
            this.plugin.getLogger().severe(String.format("Failed to save banned words config to %s", this.file.getName()));
        }
    }

    /**
     * Checks a message for any banned words or patterns.
     *
     * @param message The message to check.
     * @return The banned word or pattern found, or null if none are found.
     */
    public String checkForBannedWords(String message) {
        // Check exact matches
        for (Pattern pattern : this.EXACT_BANNED_PATTERNS) {
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                return matcher.group();
            }
        }

        // Check wildcard matches
        for (Map.Entry<Pattern, String> entry : this.WILDCARD_BANNED_PATTERNS.entrySet()) {
            Pattern pattern = entry.getKey();
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                return entry.getValue();
            }
        }

        // Check for email
        if (this.EMAIL_COMPILED_PATTERN.matcher(message).find()) {
            return "email";
        }

        // Check for IP address
        if (this.IP_COMPILED_PATTERN.matcher(message).find()) {
            return "IP address";
        }

        // Check for masked link
        if (this.MASKED_LINK_PATTERN.matcher(message).find()) {
            return "Masked link";
        }

        return null;
    }

    /**
     * Adds a word to the exact banned words list.
     *
     * @param word The word to add.
     * @throws Exception if the word already exists in the list.
     */
    public void addToExactBannedWords(String word) throws Exception {
        if (this.EXACT_BANNED_WORDS.contains(word)) throw new Exception(String.format("%s already exists", word));
        this.EXACT_BANNED_WORDS.add(word);
        saveData();
        createPatterns(); // Recreate patterns
    }

    /**
     * Adds a word to the wildcard banned words list.
     *
     * @param word The word to add.
     * @throws Exception if the word already exists in the list.
     */
    public void addToWildcardBannedWords(String word) throws Exception {
        if (this.WILDCARD_BANNED_WORDS.contains(word)) throw new Exception(String.format("%s already exists", word));
        this.WILDCARD_BANNED_WORDS.add(word);
        saveData();
        createPatterns(); // Recreate patterns
    }

    /**
     * Removes a word from the exact banned words list.
     *
     * @param word The word to remove.
     * @throws Exception if the word is not found in the list.
     */
    public void removeFromExactBannedWords(String word) throws Exception {
        if (!this.EXACT_BANNED_WORDS.contains(word)) throw new Exception(String.format("%s not found", word));
        this.EXACT_BANNED_WORDS.remove(word);
        saveData();
        createPatterns(); // Recreate patterns
    }

    /**
     * Removes a word from the wildcard banned words list.
     *
     * @param word The word to remove.
     * @throws Exception if the word is not found in the list.
     */
    public void removeFromWildcardBannedWords(String word) throws Exception {
        if (!this.WILDCARD_BANNED_WORDS.contains(word)) throw new Exception(String.format("%s not found", word));
        this.WILDCARD_BANNED_WORDS.remove(word);
        saveData();
        createPatterns(); // Recreate patterns
    }

    /**
     * Gets the exact banned words as a comma-separated string.
     *
     * @return The exact banned words.
     */
    public String getExactBannedWords() {
        return String.join(", ", this.EXACT_BANNED_WORDS);
    }

    /**
     * Gets the wildcard banned words as a comma-separated string.
     *
     * @return The wildcard banned words.
     */
    public String getWildcardBannedWords() {
        return String.join(", ", this.WILDCARD_BANNED_WORDS);
    }
}