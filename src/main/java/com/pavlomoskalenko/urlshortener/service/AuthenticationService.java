package com.pavlomoskalenko.urlshortener.service;

import com.pavlomoskalenko.urlshortener.dto.*;

public interface AuthenticationService {
    UserResponse register(RegistrationRequest registrationRequest);
    TokenResponse login(LoginRequest loginRequest);
    TokenResponse refresh(RefreshRequest refreshRequest);
}
