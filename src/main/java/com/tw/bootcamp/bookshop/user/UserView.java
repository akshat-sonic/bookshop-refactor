package com.tw.bootcamp.bookshop.user;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserView {
    private final String id;
    private final String email;

}
