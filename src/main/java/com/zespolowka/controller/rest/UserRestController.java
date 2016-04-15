package com.zespolowka.controller.rest;

import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;
import com.zespolowka.service.inteface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public List<User> getUsers(@RequestParam(value = "like", required = true) String like) {
        return (List<User>) userService.findByEmailIgnoreCaseContainingOrNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(like);
    }

    @CrossOrigin
    @RequestMapping(value = "/roles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getRoles(@RequestParam(value = "like", required = true) String like) {
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
