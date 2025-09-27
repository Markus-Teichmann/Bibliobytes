package com.bibliobytes.backend.items.items.requests;

import com.bibliobytes.backend.donations.entities.Condition;
import com.bibliobytes.backend.items.items.entities.Type;
import com.bibliobytes.backend.validation.ItemTypeNotBlank.ItemTypeNotBlank;
import com.bibliobytes.backend.validation.validcondition.ValidCondition;
import com.bibliobytes.backend.validation.validitemid.ValidItemId;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class DonateNewItemRequest {
    @ValidItemId
    private Long itemId;
    @ValidCondition
    private Condition condition;
    @NotBlank(message = "Titel is required")
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
    @ItemTypeNotBlank
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

    @AssertTrue(message = "Type is required")
    boolean typeNotNull() {
        return type != null;
    }

    @AssertTrue(message = "Missing required items")
    boolean validDonation() {
        return isValidBook() || isValidDigital() || itemId != null;
    }

    @AssertTrue(message = "Too much information. Is this a book or a digital?")
    boolean validInformation() {
        return !(
                (isValidBook() && runtime != null) ||
                (isValidBook() && languages != null) ||
                (isValidDigital() && author != null) ||
                (isValidDigital() && isbn != null) ||
                (isValidDigital() && publisher != null)
        );
    }

    public boolean isValidDigital() {
        return runtime != null && languages != null;
    }

    public boolean isValidBook() {
        return author != null && isbn != null && publisher != null;
    }
}
