package com.bibliobytes.backend.users.requests;

import com.bibliobytes.backend.validation.registered.Registered;
import lombok.Data;

@Data
public class RegisteredUserRequest {
    @Registered()
    String email;
}
