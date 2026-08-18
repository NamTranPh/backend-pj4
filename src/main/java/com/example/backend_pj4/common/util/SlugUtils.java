package com.example.backend_pj4.common.util;

import java.text.Normalizer;

public class SlugUtils {

    public static String generateSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        // Normalize Vietnamese diacritics: Ả → A
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");  // Remove diacritics

        return normalized
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")  // Replace non-alphanumeric with hyphen
                .replaceAll("^-+|-+$", "");     // Trim leading/trailing hyphens
    }
}
