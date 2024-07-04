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

    private BannedWordsHelper(JavaPlugin plugin) {
        this.plugin = plugin;
        getOrCreateDataFile();
        loadData();
        createPatterns();
    }

    public static synchronized BannedWordsHelper getInstance(JavaPlugin plugin) {
        if (instance == null) {
            instance = new BannedWordsHelper(plugin);
        }
        return instance;
    }

    private void getOrCreateDataFile() {
        String filename = "banned-words.yml";
        this.file = new File(this.plugin.getDataFolder(), filename);
        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            this.plugin.saveResource(filename, false);
        }
    }

    private void loadData() {
        if (this.file.exists()) {
            this.config = YamlConfiguration.loadConfiguration(this.file);
            this.EXACT_BANNED_WORDS = this.config.getStringList("EXACT_BANNED_WORDS");
            this.WILDCARD_BANNED_WORDS = this.config.getStringList("WILDCARD_BANNED_WORDS");
            createPatterns();
        }
    }

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

    public void saveData() {
        try {
            this.config.set("EXACT_BANNED_WORDS", this.EXACT_BANNED_WORDS);
            this.config.set("WILDCARD_BANNED_WORDS", this.WILDCARD_BANNED_WORDS);
            this.config.save(this.file);
        } catch (IOException e) {
            this.plugin.getLogger().severe(String.format("Failed to save banned words config to %s", this.file.getName()));
        }
    }

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

        if (this.MASKED_LINK_PATTERN.matcher(message).find()) {
            return "Masked link";
        }

        return null;
    }

    public void addToExactBannedWords(String word) throws Exception {
        if (this.EXACT_BANNED_WORDS.contains(word)) throw new Exception(String.format("%s already exists", word));
        this.EXACT_BANNED_WORDS.add(word);
        saveData();
        createPatterns(); // Recreate patterns
    }

    public void addToWildcardBannedWords(String word) throws Exception {
        if (this.WILDCARD_BANNED_WORDS.contains(word)) throw new Exception(String.format("%s already exists", word));
        this.WILDCARD_BANNED_WORDS.add(word);
        saveData();
        createPatterns(); // Recreate patterns
    }

    public void removeFromExactBannedWords(String word) throws Exception {
        if (!this.EXACT_BANNED_WORDS.contains(word)) throw new Exception(String.format("%s not found", word));
        this.EXACT_BANNED_WORDS.remove(word);
        saveData();
        createPatterns(); // Recreate patterns
    }

    public void removeFromWildcardBannedWords(String word) throws Exception {
        if (!this.WILDCARD_BANNED_WORDS.contains(word)) throw new Exception(String.format("%s not found", word));
        this.WILDCARD_BANNED_WORDS.remove(word);
        saveData();
        createPatterns(); // Recreate patterns
    }
}