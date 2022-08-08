package com.tw.bootcamp.bookshop.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    ResponseEntity<UserView> create(@RequestBody CreateUserCommand userCommand) throws InvalidEmailException {
        User user = userService.create(userCommand);
        return new ResponseEntity<>(user.toView(), HttpStatus.CREATED);
    }
}
