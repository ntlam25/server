package com.example.crabfood_api.util;

import java.text.Normalizer;
import java.util.Locale;

public class StringHelper {
    public static String toSlug(String name) {
        // Bước 1: Chuyển về chữ thường
        String lower = name.toLowerCase(Locale.ROOT);

        // Bước 2: Loại bỏ dấu tiếng Việt
        String normalized = Normalizer.normalize(lower, Normalizer.Form.NFD);
        String noDiacritics = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Bước 3: Loại bỏ ký tự không phải chữ và số
        String alphanumeric = noDiacritics.replaceAll("[^a-z0-9\\s-]", "");

        // Bước 4: Thay khoảng trắng bằng dấu gạch ngang và gom các dấu gạch
        String slug = alphanumeric.trim().replaceAll("\\s+", "-").replaceAll("-{2,}", "-");

        return slug;
    }
}
