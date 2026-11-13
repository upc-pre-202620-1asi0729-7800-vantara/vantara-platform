package com.vantara.platform.iam.infrastructure.authorization.sfs.model;

import com.vantara.platform.iam.domain.model.aggregates.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Spring Security view of a {@link User}: email as username, hash as password, roles as authorities.
 */
public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(
            Long id, String username, String password, boolean enabled,
            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getStringName()))
                .toList();
        return new UserDetailsImpl(
                user.getId(),
                user.getEmail().address(),
                user.getPasswordHash().value(),
                user.canSignIn(),
                authorities);
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
