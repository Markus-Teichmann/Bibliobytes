package com.bibliobytes.backend.users.requests;

import com.bibliobytes.backend.validation.taken.Taken;
import lombok.Data;

@Data
public class UsedEmailRequest {
    @Taken()
    String email;
}
