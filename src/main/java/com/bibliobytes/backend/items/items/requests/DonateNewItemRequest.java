package com.bibliobytes.backend.items.items.requests;

import com.bibliobytes.backend.donations.entities.Condition;
import com.bibliobytes.backend.items.digitals.dtos.ActorDto;
import com.bibliobytes.backend.items.digitals.dtos.LanguageDto;
import com.bibliobytes.backend.items.digitals.dtos.SubtitleDto;
import com.bibliobytes.backend.items.items.dtos.TagDto;
import com.bibliobytes.backend.items.items.entities.Type;
import com.bibliobytes.backend.validation.ItemTypeNotBlank.ItemTypeNotBlank;
import com.bibliobytes.backend.validation.validcondition.ValidCondition;
import com.bibliobytes.backend.validation.validitemid.ValidItemId;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class DonateNewItemRequest {
    @ValidItemId
    private Long id;
    @ValidCondition
    private Condition condition;
    @NotBlank(message = "Titel is required")
    //@Size(max=200)
    private String title;
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
    @Size(max=13)
    private String isbn;
    //@Size(max=255)
    private String author;
    //@Size(max=20)
    private String runtime;
    //@Size(max=100)
    private String label;
    //@Size(max=100)
    private String production;
    private Set<ActorDto> actors;
    private Set<SubtitleDto> subtitles;
    private Set<LanguageDto> languages;
    private Set<TagDto> tags;

    @AssertTrue(message = "Type is required")
    boolean typeNotNull() {
        System.out.println("AssertTrue Type not null: " + (type != null));
        return type != null;
    }

    @AssertTrue(message = "Missing required items")
    boolean validDonation() {
        System.out.println("AssertTrue validDonation: " + (isValidBook() || isValidDigital() || id != null));
        return isValidBook() || isValidDigital() || id != null;
    }

    @AssertTrue(message = "Too much information. Is this a book or a digital?")
    boolean validInformation() {
        boolean temp = !(
                (isValidBook() && runtime != null) ||
                (isValidBook() && languages != null) ||
                (isValidDigital() && author != null) ||
                (isValidDigital() && isbn != null) ||
                (isValidDigital() && publisher != null)
        );
        System.out.println("AssertTrue validInformation: " + temp);
        return temp;
    }

    public boolean isValidDigital() {
        System.out.println("isValidDigital: " + (type == Type.DIGITAL && runtime != null && languages != null));
        return runtime != null && languages != null && type == Type.DIGITAL;
    }

    public boolean isValidBook() {
        System.out.println("isValidBook: " + (type == Type.BOOK && author != null && publisher != null && isbn != null));
        return author != null && isbn != null && publisher != null && type == Type.BOOK;
    }
}
