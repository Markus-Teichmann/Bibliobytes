package com.bibliobytes.backend.items.digitals;

import com.bibliobytes.backend.donations.DonationMapper;
import com.bibliobytes.backend.donations.DonationRepository;
import com.bibliobytes.backend.donations.dtos.DonationDto;
import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.donations.entities.DonationState;
import com.bibliobytes.backend.items.ItemService;
import com.bibliobytes.backend.items.digitals.dtos.ActorDto;
import com.bibliobytes.backend.items.digitals.dtos.DigitalDto;
import com.bibliobytes.backend.items.digitals.dtos.LanguageDto;
import com.bibliobytes.backend.items.digitals.dtos.SubtitleDto;
import com.bibliobytes.backend.items.digitals.entities.Actor;
import com.bibliobytes.backend.items.digitals.entities.Digital;
import com.bibliobytes.backend.items.digitals.entities.Language;
import com.bibliobytes.backend.items.digitals.entities.Subtitle;
import com.bibliobytes.backend.items.digitals.mappers.ActorMapper;
import com.bibliobytes.backend.items.digitals.mappers.DigitalMapper;
import com.bibliobytes.backend.items.digitals.mappers.LanguageMapper;
import com.bibliobytes.backend.items.digitals.mappers.SubtitleMapper;
import com.bibliobytes.backend.items.digitals.repositorys.ActorRepository;
import com.bibliobytes.backend.items.digitals.repositorys.LanguageRepository;
import com.bibliobytes.backend.items.digitals.repositorys.SubtitleRepository;
import com.bibliobytes.backend.items.digitals.requests.*;
import com.bibliobytes.backend.items.items.ItemServiceUtils;
import com.bibliobytes.backend.items.items.dtos.ItemDto;
import com.bibliobytes.backend.items.items.entities.Item;
import com.bibliobytes.backend.items.items.repositorys.ItemRepository;
import com.bibliobytes.backend.items.items.requests.DonateNewItemRequest;
import com.bibliobytes.backend.users.UserMapper;
import com.bibliobytes.backend.users.UserRepository;
import com.bibliobytes.backend.users.UserService;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("DigitalService")
@AllArgsConstructor
public class DigitalService implements ItemService {
    private UserRepository userRepository;
    private ItemServiceUtils itemServiceUtils;
    private ActorRepository actorRepository;
    private ActorMapper actorMapper;
    private LanguageRepository languageRepository;
    private LanguageMapper languageMapper;
    private SubtitleRepository subtitleRepository;
    private SubtitleMapper subtitleMapper;
    private ItemRepository itemRepository;
    private DigitalMapper digitalMapper;
    private DonationRepository donationRepository;
    private UserMapper userMapper;
    private DonationMapper donationMapper;

    @Transactional
    public void addActors(Digital digital, Set<ActorDto> actors) {
        for (ActorDto actorDto : actors) {
            Actor actor = actorRepository.findByName(actorDto.getName()).orElse(null);
            if (actor == null) {
                actor = new Actor();
                actor.setName(actorDto.getName());
            }
            actor.addDigital(digital);
            actorRepository.save(actor);
        }
        itemRepository.save(digital);
    }

    @Transactional
    public void addLanguages(Digital digital, Set<LanguageDto> languages) {
        for (LanguageDto languageDto : languages) {
            Language language = languageRepository.findByName(languageDto.getName()).orElse(null);
            if (language == null) {
                language = new Language();
                language.setName(languageDto.getName());
            }
            language.addDigital(digital);
            languageRepository.save(language);
        }
        itemRepository.save(digital);
    }

    @Transactional
    public void addSubtitles(Digital digital, Set<SubtitleDto> subtitles) {
        for (SubtitleDto subtitleDto : subtitles) {
            Subtitle subtitle = subtitleRepository.findByLanguage(subtitleDto.getLanguage()).orElse(null);
            if (subtitle == null) {
                subtitle = new Subtitle();
                subtitle.setLanguage(subtitleDto.getLanguage());
            }
            subtitle.addDigital(digital);
            subtitleRepository.save(subtitle);
        }
        itemRepository.save(digital);
    }

    public Set<ItemDto> getAcceptedDigitals() {
        return itemRepository.findDigitalsByDonationState(DonationState.ACCEPTED).stream()
                .map(digital -> toDto(digital.getId())).collect(Collectors.toSet());
    }

    public Set<String> getAllActorNames() {
        return actorRepository.findAllNames();
    }

    public Set<String> getAllLanguageNames() {
        return languageRepository.findAllNames();
    }

    public Set<String> getAllSubtitleLanguages() {
        return subtitleRepository.findAllLanguages();
    }

    public Set<ActorDto> getActors(Digital digital) {
        return digital.getActors().stream().map(actor -> actorMapper.toDto(actor))
                .collect(Collectors.toSet());
    }

    public Set<LanguageDto> getLanguages(Digital digital) {
        return digital.getLanguages().stream().map(language -> languageMapper.toDto(language))
                .collect(Collectors.toSet());
    }

    public Set<SubtitleDto> getSubtitles(Digital digital) {
        return digital.getSubtitles().stream().map(subtitle -> subtitleMapper.toDto(subtitle))
                .collect(Collectors.toSet());
    }

    @Transactional
    public DonationDto donateItem(DonateNewItemRequest request, UserService userService) {
        UUID myId = userService.getMyId();
        User me = userRepository.findById(myId).orElse(null);
        Digital digital = null;
        if (request.getId() != null) {
            digital = itemRepository.findDigitalById(request.getId()).orElse(null);
        }
        if (digital == null) {
            digital = digitalMapper.toEntity(request);
        }
        itemRepository.save(digital);
        itemServiceUtils.addTags(digital, request.getTags());
        addActors(digital, request.getActors());
        addSubtitles(digital, request.getSubtitles());
        addLanguages(digital, request.getLanguages());
        Donation donation = Donation.builder().owner(me).condition(request.getCondition()).build();
        digital.donate(donation);
        itemRepository.save(digital);
        donationRepository.save(donation);
        UserDto owner = userMapper.toDto(me);
        System.out.println("Vermutlich genau");
        DigitalDto digitalDto = toDto(digital.getId());
        System.out.println("Hier.");
        return donationMapper.toDto(donation, owner, digitalDto);
    }

    public ItemDto updateRuntime(Long id, UpdateRuntimeRequest request) {
        Digital digital = itemRepository.findDigitalById(id).orElse(null);
        digital.setRuntime(request.getRuntime());
        itemRepository.save(digital);
        return toDto(id);
    }

    public ItemDto updateLabel(Long id, UpdateLabelRequest request) {
        Digital digital = itemRepository.findDigitalById(id).orElse(null);
        digital.setLabel(request.getLabel());
        itemRepository.save(digital);
        return toDto(id);
    }

    public ItemDto updateProduction(Long id, UpdateProductionRequest request) {
        Digital digital = itemRepository.findDigitalById(id).orElse(null);
        digital.setProduction(request.getProduction());
        itemRepository.save(digital);
        return toDto(id);
    }

    @Transactional
    public ItemDto addActor(Long id, AddActorRequest request) {
        Digital digital = itemRepository.findDigitalById(id).orElse(null);
        Actor actor = actorRepository.findById(request.getId()).orElse(null);
        if (actor == null) {
            actor = new Actor();
            actor.setName(request.getName());
        }
        actor.addDigital(digital);
        actorRepository.save(actor);
        itemRepository.save(digital);
        return toDto(id);
    }

    @Transactional
    public ItemDto removeActor(Long id, RemoveActorRequest request) {
        Digital digital = itemRepository.findDigitalById(id).orElse(null);
        Actor actor = actorRepository.findById(request.getId()).orElse(null);
        actor.removeDigital(digital);
        actorRepository.save(actor);
        itemRepository.save(digital);
        return toDto(id);
    }

    @Transactional
    public ItemDto addLanguage(Long id, AddLanguageRequest request) {
        Digital digital = itemRepository.findDigitalById(id).orElse(null);
        Language language = languageRepository.findById(request.getId()).orElse(null);
        if (language == null) {
            language = new Language();
            language.setName(request.getName());
        }
        language.addDigital(digital);
        languageRepository.save(language);
        itemRepository.save(digital);
        return toDto(id);
    }

    @Transactional
    public ItemDto removeLanguage(Long id, RemoveLanguageRequest request) {
        Digital digital = itemRepository.findDigitalById(id).orElse(null);
        Language language = languageRepository.findById(request.getId()).orElse(null);
        language.removeDigital(digital);
        languageRepository.save(language);
        itemRepository.save(digital);
        return toDto(id);
    }

    @Transactional
    public ItemDto addSubtitle(Long id, AddSubtitleRequest request) {
        Digital digital = itemRepository.findDigitalById(id).orElse(null);
        Subtitle subtitle = subtitleRepository.findById(request.getId()).orElse(null);
        if (subtitle == null) {
            subtitle = new Subtitle();
            subtitle.setLanguage(request.getLanguage());
        }
        subtitle.addDigital(digital);
        subtitleRepository.save(subtitle);
        itemRepository.save(digital);
        return toDto(id);
    }

    @Transactional
    public ItemDto removeSubtitle(Long id, RemoveSubtitleRequest request) {
        Digital digital = itemRepository.findDigitalById(id).orElse(null);
        Subtitle subtitle = subtitleRepository.findById(request.getId()).orElse(null);
        subtitle.removeDigital(digital);
        subtitleRepository.save(subtitle);
        itemRepository.save(digital);
        return toDto(id);
    }

    @Override
    public ItemServiceUtils utils() {
        return itemServiceUtils;
    }

    @Override
    public DigitalDto toDto(long id) {
        Digital digital = itemRepository.findDigitalById(id).orElse(null);
        if (digital != null) {
            return digitalMapper.toDto(digital, itemServiceUtils.getTags(digital), getActors(digital), getLanguages(digital), getSubtitles(digital), itemServiceUtils.getOwners(digital), digital.getStock());
        } else {
            return null;
        }
    }
}
