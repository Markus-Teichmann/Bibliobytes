package com.bibliobytes.backend.users.dtos;

import java.io.Serializable;
import java.util.Map;

public interface Confirmable extends Serializable {
    String getEmail();
}
