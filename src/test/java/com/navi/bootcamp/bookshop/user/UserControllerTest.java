package com.navi.bootcamp.bookshop.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Validator validator;

    @Test
    void shouldCreateUserWhenCredentialsAreValid() throws Exception {
        CreateUserCommand userCredentials = new CreateUserCommandTestBuilder().build();
        User user = new UserTestBuilder().withEmail(userCredentials.getEmail()).build();
        when(userService.create(userCredentials)).thenReturn(user);
        UserView userView = UserView.builder()
                .id(user.getId().toString())
                .email(userCredentials.getEmail())
                .build();

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(userCredentials))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(userView)));

        verify(userService, times(1)).create(userCredentials);
    }

    @Test
    void shouldRespondWithErrorMessageWhenCreateUserFails() throws Exception {
        CreateUserCommand userCredentials = new CreateUserCommandTestBuilder().build();
        when(userService.create(userCredentials)).thenThrow(new InvalidEmailException());

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(userCredentials))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User with same email already created"));
    }

    @Test
    void shouldRespondWithErrorMessageWhenCreateUserValidationFails() throws Exception {
        CreateUserCommand userCredentials = new CreateUserCommandTestBuilder().withEmptyEmail().build();
        Set<ConstraintViolation<User>> violations = validator.validate(User.createFrom(userCredentials));
        when(userService.create(userCredentials)).thenThrow(new ConstraintViolationException(violations));

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(userCredentials))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").value("Email is mandatory"));
    }
}