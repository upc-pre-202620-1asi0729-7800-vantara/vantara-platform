package com.vantara.platform.profile.domain.model.valueobjects;

import java.util.Locale;
import java.util.Set;

/**
 * Display preferences of a profile: user interface theme and interface language.
 *
 * <p>Unknown or blank values fall back to the platform defaults; a value outside the
 * supported set is rejected.</p>
 *
 * @param theme  theme name, either {@code light} or {@code dark}
 * @param locale language code, either {@code es} or {@code en}
 */
public record ProfilePreferences(String theme, String locale) {

    public static final String DEFAULT_THEME = "light";
    public static final String DEFAULT_LOCALE = "es";

    private static final Set<String> THEMES = Set.of("light", "dark");
    private static final Set<String> LOCALES = Set.of("es", "en");

    public ProfilePreferences {
        theme = normalize(theme, DEFAULT_THEME, THEMES, "Theme must be 'light' or 'dark'");
        locale = normalize(locale, DEFAULT_LOCALE, LOCALES, "Locale must be 'es' or 'en'");
    }

    private static String normalize(String value, String fallback, Set<String> allowed, String errorMessage) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        var normalized = value.trim().toLowerCase(Locale.ROOT);
        if (!allowed.contains(normalized)) {
            throw new IllegalArgumentException(errorMessage);
        }
        return normalized;
    }
}
