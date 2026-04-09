package com.pavlomoskalenko.urlshortener.mapper;

import com.pavlomoskalenko.urlshortener.dto.UserResponse;
import com.pavlomoskalenko.urlshortener.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse mapToResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRoles());
    }
}
