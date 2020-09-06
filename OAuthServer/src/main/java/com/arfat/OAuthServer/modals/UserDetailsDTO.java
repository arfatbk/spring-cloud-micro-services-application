package com.arfat.OAuthServer.modals;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Arfat Bin Kileb
 * Created at 06-09-2020 02:02 PM
 */
public class UserDetailsDTO extends User implements UserDetails {
    public UserDetailsDTO(User user) {
        super(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();
        getRoles().stream().map(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));

            role.getPermissions()
                    .stream()
                    .map(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));

            return role;
        });
        return authorities;
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }
}
