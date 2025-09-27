package com.bibliobytes.backend.items.books.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateIsbnRequest {
    @NotBlank
    private String isbn;
}
