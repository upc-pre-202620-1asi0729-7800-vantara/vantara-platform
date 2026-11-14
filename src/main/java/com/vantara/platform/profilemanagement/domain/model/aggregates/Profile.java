package com.vantara.platform.profilemanagement.domain.model.aggregates;

import com.vantara.platform.profilemanagement.domain.model.valueobjects.ProfilePreferences;
import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate root with the personal data and display preferences of an account.
 *
 * <p>The identity of the account lives in the IAM bounded context; a profile only keeps
 * a logical reference to it through {@code userId}. The email address is never stored
 * here: it is owned by IAM.</p>
 */
public class Profile extends AbstractDomainAggregateRoot<Profile> {

    private static final int FULL_NAME_MAX_LENGTH = 120;
    private static final int ORGANIZATION_NAME_MAX_LENGTH = 120;
    private static final int PHONE_MAX_LENGTH = 30;
    private static final int PHOTO_URL_MAX_LENGTH = 500;

    private Long id;
    private final Long userId;
    private String fullName;
    private String organizationName;
    private String phone;
    private String photoUrl;
    private ProfilePreferences preferences;
    private final Instant createdAt;
    private Instant updatedAt;

    private Profile(Long id, Long userId, String fullName, String organizationName, String phone,
                    String photoUrl, ProfilePreferences preferences, Instant createdAt, Instant updatedAt) {
        requireUserId(userId);
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.organizationName = organizationName;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.preferences = preferences;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Creates the profile of an account with the data provided at sign-up time.
     *
     * <p>Blank optional values are stored as {@code null}, and blank preferences fall
     * back to the platform defaults.</p>
     *
     * @param userId            identifier of the account in the IAM context
     * @param fullName          personal name of the account holder
     * @param organizationName  optional organization the account belongs to
     * @param phone             optional contact phone
     * @param photoUrl          optional profile photo URL
     * @param theme             optional theme name; blank falls back to {@code light}
     * @param locale            optional language code; blank falls back to {@code es}
     * @param now               creation instant
     * @return the new profile
     */
    public static Profile create(Long userId, String fullName, String organizationName, String phone,
                                 String photoUrl, String theme, String locale, Instant now) {
        Objects.requireNonNull(now, "Creation instant cannot be null");
        return new Profile(
                null,
                userId,
                requireText(fullName, "Full name cannot be null or blank", FULL_NAME_MAX_LENGTH),
                optionalText(organizationName, ORGANIZATION_NAME_MAX_LENGTH, "Organization name is too long"),
                optionalText(phone, PHONE_MAX_LENGTH, "Phone is too long"),
                optionalText(photoUrl, PHOTO_URL_MAX_LENGTH, "Photo URL is too long"),
                new ProfilePreferences(theme, locale),
                now,
                now);
    }

    /**
     * Rebuilds a profile from its persisted state. It does not record any domain event.
     */
    public static Profile reconstitute(Long id, Long userId, String fullName, String organizationName,
                                       String phone, String photoUrl, String theme, String locale,
                                       Instant createdAt, Instant updatedAt) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid profile id");
        }
        Objects.requireNonNull(createdAt, "Creation instant cannot be null");
        return new Profile(
                id,
                userId,
                requireText(fullName, "Full name cannot be null or blank", FULL_NAME_MAX_LENGTH),
                optionalText(organizationName, ORGANIZATION_NAME_MAX_LENGTH, "Organization name is too long"),
                optionalText(phone, PHONE_MAX_LENGTH, "Phone is too long"),
                optionalText(photoUrl, PHOTO_URL_MAX_LENGTH, "Photo URL is too long"),
                new ProfilePreferences(theme, locale),
                createdAt,
                updatedAt == null ? createdAt : updatedAt);
    }

    /**
     * Replaces the editable personal data and preferences of the profile.
     *
     * @param fullName         new personal name
     * @param organizationName new organization, or blank to clear it
     * @param phone            new phone, or blank to clear it
     * @param photoUrl         new photo URL, or blank to clear it
     * @param theme            new theme name
     * @param locale           new language code
     * @param now              update instant
     */
    public void update(String fullName, String organizationName, String phone, String photoUrl,
                       String theme, String locale, Instant now) {
        Objects.requireNonNull(now, "Update instant cannot be null");
        this.fullName = requireText(fullName, "Full name cannot be null or blank", FULL_NAME_MAX_LENGTH);
        this.organizationName = optionalText(organizationName, ORGANIZATION_NAME_MAX_LENGTH, "Organization name is too long");
        this.phone = optionalText(phone, PHONE_MAX_LENGTH, "Phone is too long");
        this.photoUrl = optionalText(photoUrl, PHOTO_URL_MAX_LENGTH, "Photo URL is too long");
        this.preferences = new ProfilePreferences(theme, locale);
        this.updatedAt = now;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public ProfilePreferences getPreferences() {
        return preferences;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private static void requireUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User id must be a positive number");
        }
    }

    private static String requireText(String value, String errorMessage, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
        var trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(errorMessage);
        }
        return trimmed;
    }

    private static String optionalText(String value, int maxLength, String errorMessage) {
        if (value == null || value.isBlank()) {
            return null;
        }
        var trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(errorMessage);
        }
        return trimmed;
    }
}
