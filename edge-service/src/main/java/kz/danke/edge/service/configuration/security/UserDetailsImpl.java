package kz.danke.edge.service.configuration.security;

import kz.danke.edge.service.document.Authorities;
import kz.danke.edge.service.document.User;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@ToString
public class UserDetailsImpl implements UserDetails {

    private final User user;
    private final Set<? extends GrantedAuthority> authorities;

    private UserDetailsImpl(User user, Set<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public static UserDetailsImpl buildUserDetails(User user) {
        Set<SimpleGrantedAuthority> roles = user.getAuthorities()
                .stream()
                .filter(UserDetailsImpl::validateRole)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new UserDetailsImpl(
                user,
                roles
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getId() {
        return user.getId();
    }

    public User getUser() {
        return user;
    }

    private static boolean validateRole(String role) {
        Authorities[] values = Authorities.values();

        for (Authorities auth : values) {
            if (auth.name().equals(role)) {
                return true;
            }
        }
        return false;
    }
}
