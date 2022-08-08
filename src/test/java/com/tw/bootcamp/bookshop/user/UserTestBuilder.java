package com.tw.bootcamp.bookshop.user;

import com.tw.bootcamp.bookshop.user.User.UserBuilder;

public class UserTestBuilder {
    private final UserBuilder userBuilder;

    public UserTestBuilder() {
        userBuilder = User.builder()
                .id(1L)
                .email("testemail@test.com")
                .password("foobar");
    }

    public static CreateUserCommand buildCreateUserCommand() {
        return new CreateUserCommandTestBuilder().build();
    }

    public User build() {
        return userBuilder.build();
    }

    public UserTestBuilder withEmail(String email) {
        userBuilder.email(email);
        return this;
    }
}
