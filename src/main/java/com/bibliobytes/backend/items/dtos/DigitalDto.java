package com.bibliobytes.backend.items.dtos;

import com.bibliobytes.backend.items.entities.Actor;
import com.bibliobytes.backend.items.entities.Language;
import com.bibliobytes.backend.items.entities.Subtitle;
import com.bibliobytes.backend.users.entities.User;
import lombok.Data;

import java.util.List;

@Data
public class DigitalDto {
    private String title;
    private String place;
    private String topic;
    private List<User> owners;
    private int stock;
    private String note;
    private String runtime;
    private String label;
    private String production;
    private List<Actor> actors;
    private List<Language> languages;
    private List<Subtitle> subtitles;
}
