package com.bibliobytes.backend.items.dtos;

import com.bibliobytes.backend.donations.entities.Condition;
import com.bibliobytes.backend.items.entities.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class DonationRequest {
    private Long itemId;
    private Condition condition;
    @NotEmpty(message = "Titel is required")
    //@Size(max=200)
    private String titel;
    //@Size(max=100)
    private String place;
    @NotBlank(message = "Topic is required")
    //@Size(max=100)
    private String topic;
    //@Size(max=500)
    private String note;
    //@NotBlank(message = "type is required") -- no validator
    //@Size(max=7)
    private Type type;
    //@Size(max=200)
    private String publisher;
    //@Size(max=13)
    private String isbn;
    //@Size(max=255)
    private String author;
    //@Size(max=20)
    private String runtime;
    //@Size(max=100)
    private String label;
    //@Size(max=100)
    private String production;
    private Set<String> actors;
    private Set<String> subtitles;
    private Set<String> languages;
    private Set<String> tags;
}
