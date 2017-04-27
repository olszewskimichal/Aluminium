package com.zespolowka.controller.rest;

import java.util.ArrayList;
import java.util.List;

import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;
import com.zespolowka.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
	private final UserService userService;

	@Autowired
	public UserRestController(UserService userService) {
		this.userService = userService;
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<User> getUsers(@RequestParam(value = "like") String like) {
		return (List<User>) userService.findUsersByEmailIgnoreCaseContainingOrNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(like);
	}

	@CrossOrigin
	@RequestMapping(value = "/roles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getRoles(@RequestParam(value = "like") String like) {
		List<String> result = new ArrayList<>();
		Role[] roles = Role.values();
		for (Role role : roles) {
			if (role.toString().toLowerCase().contains(like.toLowerCase())) {
				result.add(role.toString());
			}
		}
		return result;
	}
}
