package com.zespolowka.forms;

import com.zespolowka.entity.user.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by Pitek on 2015-11-29.
 */
@Data
@NoArgsConstructor
public class UserCreateForm {

    @NotBlank
    @Size(min = 3, max = 40)
    private String name;

    @NotBlank
    @Size(min = 3, max = 40)
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")
    @Size(max = 50)
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\\d]){1,})(?=(.*[\\W]){1,})(?!.*\\s).{8,}$")
    private String password;

    @NotBlank
    private String confirmPassword;

    private Role role = Role.USER;

    public UserCreateForm(String name, String lastName, String email, String password, String confirmPassword) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

}
