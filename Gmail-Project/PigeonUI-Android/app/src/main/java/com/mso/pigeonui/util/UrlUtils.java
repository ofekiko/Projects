package com.mso.pigeonui.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Utility class for URL-related operations
public class UrlUtils {
    // Custom regular expression designed to match common URL patterns
    private static final String CUSTOM_URL_REGEX =
            "(https?:\\/\\/)?(www\\.)?([a-zA-Z0-9-]+\\.)+[a-zA-Z0-9]{2,}(\\/\\S*)?";
    private static final Pattern URL_EXTRACTION_PATTERN = Pattern.compile(CUSTOM_URL_REGEX, Pattern.CASE_INSENSITIVE);
    // Extracts all unique URLs found within the given text.
    public static List<String> extractUrls(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> urls = new HashSet<>(); // Using a Set to ensure unique URLs
        Matcher matcher = URL_EXTRACTION_PATTERN.matcher(text);

        while (matcher.find()) {
            urls.add(matcher.group()); // Add the entire matched URL
        }

        return new ArrayList<>(urls);
    }
}
