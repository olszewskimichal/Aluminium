package com.zespolowka.forms;

import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class UserEditForm {

    private Long id;

    @Size(min = 3, max = 40)
    private String name;

    @Size(min = 3, max = 40)
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")
    private String email;

    private Role role = Role.USER;

    private String password;

    private String confirmPassword;


    public UserEditForm(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.password = user.getPasswordHash();
        this.confirmPassword = this.password;
    }

}