package com.vantara.platform.profile.domain.model.aggregates;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileTest {

    private static final Instant NOW = Instant.parse("2026-11-11T12:00:00Z");

    @Test
    void createsAProfileWithDefaultsAndNormalization() {
        var profile = Profile.create(7L, "  Ana Pérez  ", " ", "", null, null, null, NOW);

        assertThat(profile.getId()).isNull();
        assertThat(profile.getUserId()).isEqualTo(7L);
        assertThat(profile.getFullName()).isEqualTo("Ana Pérez");
        assertThat(profile.getOrganizationName()).isNull();
        assertThat(profile.getPhone()).isNull();
        assertThat(profile.getPhotoUrl()).isNull();
        assertThat(profile.getPreferences().theme()).isEqualTo("light");
        assertThat(profile.getPreferences().locale()).isEqualTo("es");
        assertThat(profile.getCreatedAt()).isEqualTo(NOW);
        assertThat(profile.getUpdatedAt()).isEqualTo(NOW);
        assertThat(profile.domainEvents()).isEmpty();
    }

    @Test
    void rejectsABlankFullName() {
        assertThatThrownBy(() -> Profile.create(7L, "   ", null, null, null, null, null, NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsAnInvalidUserId() {
        assertThatThrownBy(() -> Profile.create(0L, "Ana Pérez", null, null, null, null, null, NOW))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Profile.create(null, "Ana Pérez", null, null, null, null, null, NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsAnUnknownTheme() {
        assertThatThrownBy(() -> Profile.create(7L, "Ana Pérez", null, null, null, "blue", null, NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsAnUnknownLocale() {
        assertThatThrownBy(() -> Profile.create(7L, "Ana Pérez", null, null, null, null, "pt", NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updatesOnlyTheEditableData() {
        var profile = Profile.create(7L, "Ana Pérez", "Agrosur", "+51 999 999 999",
                "https://cdn.example/ana.png", "light", "es", NOW);

        var later = NOW.plusSeconds(60);
        profile.update("Ana Quispe", " ", "", "", "dark", "en", later);

        assertThat(profile.getUserId()).isEqualTo(7L);
        assertThat(profile.getFullName()).isEqualTo("Ana Quispe");
        assertThat(profile.getOrganizationName()).isNull();
        assertThat(profile.getPhone()).isNull();
        assertThat(profile.getPhotoUrl()).isNull();
        assertThat(profile.getPreferences().theme()).isEqualTo("dark");
        assertThat(profile.getPreferences().locale()).isEqualTo("en");
        assertThat(profile.getCreatedAt()).isEqualTo(NOW);
        assertThat(profile.getUpdatedAt()).isEqualTo(later);
    }

    @Test
    void reconstitutesAPersistedProfile() {
        var profile = Profile.reconstitute(3L, 7L, "Ana Pérez", "Agrosur", "+51 999 999 999",
                "https://cdn.example/ana.png", "dark", "en", NOW, NOW.plusSeconds(5));

        assertThat(profile.getId()).isEqualTo(3L);
        assertThat(profile.getUserId()).isEqualTo(7L);
        assertThat(profile.getFullName()).isEqualTo("Ana Pérez");
        assertThat(profile.getPreferences().theme()).isEqualTo("dark");
        assertThat(profile.getPreferences().locale()).isEqualTo("en");
        assertThat(profile.getCreatedAt()).isEqualTo(NOW);
        assertThat(profile.getUpdatedAt()).isEqualTo(NOW.plusSeconds(5));
        assertThat(profile.domainEvents()).isEmpty();
    }

    @Test
    void reconstituteRejectsAnInvalidIdentity() {
        assertThatThrownBy(() -> Profile.reconstitute(0L, 7L, "Ana Pérez", null, null, null, null, null, NOW, NOW))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Profile.reconstitute(3L, null, "Ana Pérez", null, null, null, null, null, NOW, NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
