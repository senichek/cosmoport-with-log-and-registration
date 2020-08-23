package com.space.controller;

import com.space.model.Ship;
import com.space.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    /*@RequestMapping(path = "/rest/login", method = RequestMethod.GET)
    public String login() {

        return "login.jsp";
    }*/

    @RequestMapping(path = "/rest/login", method = RequestMethod.POST)
    public ResponseEntity<User> createUser (@RequestBody User user) {

        User userToAddtoDB = new User();
        userToAddtoDB.setUsername(user.getUsername());
        userToAddtoDB.setPassword(user.getPassword());

            return new ResponseEntity<>(userToAddtoDB, HttpStatus.OK);
        }
}
