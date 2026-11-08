package com.vantara.platform.iam.domain.model.entities;

import com.vantara.platform.iam.domain.model.valueobjects.Roles;

import java.util.Locale;
import java.util.Objects;

/**
 * Role entity granted to users of the platform.
 */
public class Role {

    private Long id;
    private final Roles name;

    public Role(Roles name) {
        if (name == null) {
            throw new IllegalArgumentException("Role name cannot be null");
        }
        this.name = name;
    }

    /**
     * Rebuilds a role from its persisted state.
     */
    public static Role reconstitute(Long id, Roles name) {
        var role = new Role(name);
        role.id = id;
        return role;
    }

    /**
     * @return the role assigned when no role is requested
     */
    public static Role getDefaultRole() {
        return new Role(Roles.ROLE_RANCHER);
    }

    /**
     * Builds a role from its string name.
     *
     * @param name the role name, e.g. {@code ROLE_VETERINARIAN}
     * @return the matching role, or the default role when the name is null or blank
     * @throws IllegalArgumentException when the name is not a known role
     */
    public static Role toRoleFromName(String name) {
        if (name == null || name.isBlank()) {
            return getDefaultRole();
        }
        try {
            return new Role(Roles.valueOf(name.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown role: " + name);
        }
    }

    public String getStringName() {
        return name.name();
    }

    public Long getId() {
        return id;
    }

    public Roles getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Role other && name == other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
