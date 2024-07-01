package com.keotheundamaged.minecraftchat.Common.Helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordExtractorHelper {

    /**
     * Gets up to a configurable limit of words on either side of a specific word in a string.
     *
     * @param text       the input text
     * @param targetWord the word to find in the text
     * @param limit      the number of words on either side of the target to return
     * @return a string containing up to the limit of words before and after the target word
     */
    public String getSurroundingWords(String text, String targetWord, int limit) {
        String[] words = text.split("\\s+");

        // Find the index of the target word
        int targetIndex = -1;
        for (int i = 0; i < words.length; i++) {
            if (words[i].equalsIgnoreCase(targetWord)) {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex == -1) {
            return ""; // Target word not found
        }

        // Calculate the start and end indices for surrounding words
        int startIndex = Math.max(0, targetIndex - limit);
        int endIndex = Math.min(words.length - 1, targetIndex + limit);

        // Extract the subarray of words
        List<String> result = new ArrayList<>(Arrays.asList(words).subList(startIndex, endIndex + 1));

        // Join the subarray back into a string
        return String.join(" ", result);
    }
}
