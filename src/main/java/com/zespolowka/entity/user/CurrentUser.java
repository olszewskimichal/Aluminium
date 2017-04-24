package com.zespolowka.entity.user;

import lombok.Data;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * Created by Pitek on 2015-12-11.
 */
@Data
public class CurrentUser extends org.springframework.security.core.userdetails.User {

    private User user;

    public CurrentUser(User user) {
        super(user.getEmail(), user.getPasswordHash(), user.isEnabled(), true, true, user.isAccountNonLocked(),
                AuthorityUtils.createAuthorityList(user.getRole().name()));
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    public Role getRole() {
        return user.getRole();
    }
}