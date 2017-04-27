package com.zespolowka.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangePasswordForm {
	private Long id;

	@NotNull
	@Size(min = 8, max = 25)
	private String password;

	@NotNull
	@Size(min = 8, max = 25)
	private String confirmPassword;

}
