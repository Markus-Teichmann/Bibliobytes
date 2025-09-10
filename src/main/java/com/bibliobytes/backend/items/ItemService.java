package com.bibliobytes.backend.items;

import com.bibliobytes.backend.donations.DonationRepository;
import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.donations.entities.DonationState;
import com.bibliobytes.backend.items.dtos.*;
import com.bibliobytes.backend.items.entities.*;
import com.bibliobytes.backend.items.mappers.*;
import com.bibliobytes.backend.items.repositorys.*;
import com.bibliobytes.backend.rentals.RentalRepository;
import com.bibliobytes.backend.rentals.entities.Rental;
import com.bibliobytes.backend.rentals.entities.RentalState;
import com.bibliobytes.backend.users.UserMapper;
import com.bibliobytes.backend.users.UserService;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final BookMapper bookMapper;
    private final DigitalMapper digitalMapper;
    private final ActorRepository actorRepository;
    private final SubtitleRepository subtitleRepository;
    private final LanguageRepository languageRepository;
    private UserService userService;
    private DonationRepository donationRepository;
    private RentalRepository rentalRepository;
    private TagRepository tagRepository;
    private UserMapper userMapper;
    private TagMapper tagMapper;
    private ActorMapper actorMapper;
    private LanguageMapper languageMapper;
    private SubtitleMapper subtitleMapper;

    public Set<UserDto> getOwners(Item item) {
        List<Donation> donations = donationRepository.findAllByItem(item);
        Set<UserDto> owners = new HashSet<>();
        for (Donation donation : donations) {
            owners.add(userMapper.toDto(donation.getOwner()));
        }
        return owners;
    }

    /*
        Gibt den aktuell ausleihbaren Bestand zurück.
     */
    public int getStock(Item item) {
        int stock = 0;
        for (Donation donation : donationRepository.findAllByItem(item)) {
            if (donation.getStatus() == DonationState.ACCEPTED) {
                stock++;
            }
            Rental rental = rentalRepository.findByDonation(donation).orElse(null);
            if (rental != null) {
                stock--;
            }
        }
        return stock;
    }

    public Set<TagDto> getTags(Item item) {
        Set<TagDto> tagDtos = new HashSet<>();
        Set<Tag> tags = item.getTags();
        for (Tag tag : tags) {
            tagDtos.add(tagMapper.toDto(tag));
        }
        return tagDtos;
    }

    public Set<ActorDto> getActors(Digital digital) {
        Set<ActorDto> actorDtos = new HashSet<>();
        Set<Actor> actors = digital.getActors();
        for (Actor actor : actors) {
            actorDtos.add(actorMapper.toDto(actor));
        }
        return actorDtos;
    }

    public Set<LanguageDto> getLanguages(Digital digital) {
        Set<LanguageDto> languageDtos = new HashSet<>();
        Set<Language> languages = digital.getLanguages();
        for (Language language : languages) {
            languageDtos.add(languageMapper.toDto(language));
        }
        return languageDtos;
    }

    public Set<SubtitleDto> getSubtitles(Digital digital) {
        Set<SubtitleDto> subtitleDtos = new HashSet<>();
        Set<Subtitle> subtitles = digital.getSubtitles();
        for (Subtitle subtitle : subtitles) {
            subtitleDtos.add(subtitleMapper.toDto(subtitle));
        }
        return subtitleDtos;
    }

    @Transactional
    public Item saveItem(DonationRequest request, User owner) {
        Item item = null;
        if (request.getItemId() != null) {
            item = itemRepository.findById(request.getItemId()).orElseThrow(EntityNotFoundException::new);
        } else if (request.getType() == Type.BOOK) {
            item = bookMapper.toEntity(request);
        } else {
            item = digitalMapper.toEntity(request);
            for (String actorName : request.getActors()) {
                Actor actor = actorRepository.findByName(actorName).orElse(null);
                if (actor == null) {
                    actor = new Actor();
                    actor.setName(actorName);
                }
                actor.addDigital((Digital) item);
                actorRepository.save(actor);
            }
            for (String subtitleLanguage : request.getSubtitles()) {
                Subtitle subtitle = subtitleRepository.findByLanguage(subtitleLanguage).orElse(null);
                if (subtitle == null) {
                    subtitle = new Subtitle();
                    subtitle.setLanguage(subtitleLanguage);
                }
                subtitle.addDigital((Digital) item);
                subtitleRepository.save(subtitle);
            }
            for (String languageName : request.getLanguages()) {
                Language language = languageRepository.findByName(languageName).orElse(null);
                if (language == null) {
                    language = new Language();
                    language.setName(languageName);
                }
                language.addDigital((Digital) item);
                languageRepository.save(language);
            }
        }
        for (String tagName: request.getTags()) {
            Tag tag = tagRepository.findByName(tagName).orElse(null);
            if (tag == null) {
                tag = new Tag();
                tag.setName(tagName);
            }
            //"Adding Tag -- Runs second select statement" -- because of the !contains it has to check
            item.addTag(tag);
            tagRepository.save(tag);
        }
        Donation donation = Donation.builder().owner(owner).condition(request.getCondition()).build();
        item.donate(donation);
        itemRepository.save(item);
        //Donation references book wich is now transient so this method needs to be transactional
        //And also the Donation must be saved after the item is saved.
        donationRepository.save(donation);
        return item;
    }
}
