package com.keotheundamaged.minecraftchat.Common.Helpers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides helper methods for checking text against banned words.
 */
public class BannedWordsHelper {
    /**
     * Checks the given text for any banned words from the provided list.
     *
     * @param text  the text to be checked for banned words, email addresses or IP addresses
     * @param words the list of banned words
     * @return the first banned word, email address, or IP address found in the text, or null if none are found or if input is invalid
     */
    public String checkForBannedWords(String text, List<String> words) {
        if (text == null || words == null || words.isEmpty()) { return null; }

        // Regex patterns for email and IP addresses
        String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        String ipPattern = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b";

        // Construct the regex pattern for banned words, emails, and IP addresses
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.size(); i++) {
            if (i > 0) {
                builder.append("|");
            }
            builder.append("\\b").append(Pattern.quote(words.get(i))).append("\\b");
        }
        builder.append("|").append(emailPattern);
        builder.append("|").append(ipPattern);

        // Compile the final pattern
        Pattern pattern = Pattern.compile(builder.toString());
        Matcher matcher = pattern.matcher(text);

        // Return the first match found
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
