package com.bibliobytes.backend.items.books.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePublisherRequest {
    @NotBlank
    private String publisher;
}
