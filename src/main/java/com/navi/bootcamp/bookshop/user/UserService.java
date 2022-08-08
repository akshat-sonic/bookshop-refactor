package com.navi.bootcamp.bookshop.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Validator validator;

    public UserService() {
    }

    public User create(CreateUserCommand userCommand) throws InvalidEmailException {
        Optional<User> user = userRepository.findByEmail(userCommand.getEmail());
        if (user.isPresent()) {
            throw new InvalidEmailException();
        }
        User newUser = User.createFrom(userCommand);
        validator.validate(newUser);
        return userRepository.save(newUser);
    }

}
