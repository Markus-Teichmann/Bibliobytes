package com.bibliobytes.backend.items.digitals;

import com.bibliobytes.backend.donations.DonationMapper;
import com.bibliobytes.backend.donations.DonationRepository;
import com.bibliobytes.backend.donations.dtos.DonationDto;
import com.bibliobytes.backend.donations.entities.Donation;
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
import com.bibliobytes.backend.users.UserService;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.User;
import com.bibliobytes.backend.validation.validdigitalid.ValidDigitalId;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service("DigitalService")
@AllArgsConstructor
public class DigitalService implements ItemService {
    private ItemServiceUtils itemServiceUtils;
    private ActorRepository actorRepository;
    private ActorMapper actorMapper;
    private LanguageRepository languageRepository;
    private LanguageMapper languageMapper;
    private SubtitleRepository subtitleRepository;
    private SubtitleMapper subtitleMapper;
    private ItemRepository itemRepository;
    private DigitalMapper digitalMapper;
    private UserService userService;
    private DonationRepository donationRepository;
    private UserMapper userMapper;
    private DonationMapper donationMapper;

    @Transactional
    public void addActors(Digital digital, Set<String> actors) {
        for (String actorName : actors) {
            Actor actor = actorRepository.findByName(actorName).orElse(null);
            if (actor == null) {
                actor = new Actor();
                actor.setName(actorName);
            }
            actor.addDigital(digital);
            actorRepository.save(actor);
        }
        itemRepository.save(digital);
    }

    @Transactional
    public void addLanguages(Digital digital, Set<String> languages) {
        for (String languageName : languages) {
            Language language = languageRepository.findByName(languageName).orElse(null);
            if (language == null) {
                language = new Language();
                language.setName(languageName);
            }
            language.addDigital(digital);
            languageRepository.save(language);
        }
        itemRepository.save(digital);
    }

    @Transactional
    public void addSubtitles(Digital digital, Set<String> subtitles) {
        for (String subtitleLanguage : subtitles) {
            Subtitle subtitle = subtitleRepository.findByLanguage(subtitleLanguage).orElse(null);
            if (subtitle == null) {
                subtitle = new Subtitle();
                subtitle.setLanguage(subtitleLanguage);
            }
            subtitle.addDigital(digital);
            subtitleRepository.save(subtitle);
        }
        itemRepository.save(digital);
    }

    public Set<ActorDto> getActors(Digital digital) {
        return actorRepository.findAllByItemId(digital.getId()).stream()
                .map(actor -> actorMapper.toDto(actor)).collect(Collectors.toSet());
    }

    public Set<LanguageDto> getLanguages(Digital digital) {
        return languageRepository.findAllByItemId(digital.getId()).stream()
                .map(language -> languageMapper.toDto(language)).collect(Collectors.toSet());
    }

    public Set<SubtitleDto> getSubtitles(Digital digital) {
        return subtitleRepository.findAllByItemId(digital.getId()).stream()
                .map(subtitle -> subtitleMapper.toDto(subtitle)).collect(Collectors.toSet());
    }

//    public DigitalDto getItemDetails(@ValidDigitalId long id) {
//        Digital digital = itemRepository.findDigitalById(id).orElse(null);
//        return toDto(digital);
//    }

    @Transactional
    public DonationDto donateItem(DonateNewItemRequest request) {
        User me = userService.findMe();
        Digital digital = itemRepository.findDigitalById(request.getItemId()).orElse(null);
        if (digital == null) {
            digital = digitalMapper.toEntity(request);
        }
        itemServiceUtils.addTags(digital, request.getTags());
        addActors(digital, request.getActors());
        addSubtitles(digital, request.getSubtitles());
        addLanguages(digital, request.getLanguages());
        Donation donation = Donation.builder().owner(me).condition(request.getCondition()).build();
        digital.donate(donation);
        itemRepository.save(digital);
        donationRepository.save(donation);
        UserDto owner = userMapper.toDto(me);
        DigitalDto digitalDto = toDto(digital);
        return donationMapper.toDto(donation, owner, digitalDto);
    }

    public ItemDto updateRuntime(Long id, UpdateRuntimeRequest request) {
        Digital digital = (Digital) itemRepository.findById(id).orElse(null);
        digital.setRuntime(request.getRuntime());
        itemRepository.save(digital);
        return toDto(digital);
    }

    public ItemDto updateLabel(Long id, UpdateLabelRequest request) {
        Digital digital = (Digital) itemRepository.findById(id).orElse(null);
        digital.setLabel(request.getLabel());
        itemRepository.save(digital);
        return toDto(digital);
    }

    public ItemDto updateProduction(Long id, UpdateProductionRequest request) {
        Digital digital = (Digital) itemRepository.findById(id).orElse(null);
        digital.setProduction(request.getProduction());
        itemRepository.save(digital);
        return toDto(digital);
    }

    @Transactional
    public ItemDto addActor(Long id, AddActorRequest request) {
        Digital digital = (Digital) itemRepository.findById(id).orElse(null);
        Actor actor = actorRepository.findById(request.getId()).orElse(null);
        if (actor == null) {
            actor = new Actor();
            actor.setName(request.getName());
        }
        actor.addDigital(digital);
        actorRepository.save(actor);
        itemRepository.save(digital);
        return toDto(digital);
    }

    @Transactional
    public ItemDto removeActor(Long id, RemoveActorRequest request) {
        Digital digital = (Digital) itemRepository.findById(id).orElse(null);
        Actor actor = actorRepository.findById(request.getId()).orElse(null);
        actor.removeDigital(digital);
        actorRepository.save(actor);
        itemRepository.save(digital);
        return toDto(digital);
    }

    @Transactional
    public ItemDto addLanguage(Long id, AddLanguageRequest request) {
        Digital digital = (Digital) itemRepository.findById(id).orElse(null);
        Language language = languageRepository.findById(request.getId()).orElse(null);
        if (language == null) {
            language = new Language();
            language.setName(request.getName());
        }
        language.addDigital(digital);
        languageRepository.save(language);
        itemRepository.save(digital);
        return toDto(digital);
    }

    @Transactional
    public ItemDto removeLanguage(Long id, RemoveLanguageRequest request) {
        Digital digital = (Digital) itemRepository.findById(id).orElse(null);
        Language language = languageRepository.findById(request.getId()).orElse(null);
        language.removeDigital(digital);
        languageRepository.save(language);
        itemRepository.save(digital);
        return toDto(digital);
    }

    @Transactional
    public ItemDto addSubtitle(Long id, AddSubtitleRequest request) {
        Digital digital = (Digital) itemRepository.findById(id).orElse(null);
        Subtitle subtitle = subtitleRepository.findById(request.getId()).orElse(null);
        if (subtitle == null) {
            subtitle = new Subtitle();
            subtitle.setLanguage(request.getLanguage());
        }
        subtitle.addDigital(digital);
        subtitleRepository.save(subtitle);
        itemRepository.save(digital);
        return toDto(digital);
    }

    @Transactional
    public ItemDto removeSubtitle(Long id, RemoveSubtitleRequest request) {
        Digital digital = (Digital) itemRepository.findById(id).orElse(null);
        Subtitle subtitle = subtitleRepository.findById(request.getId()).orElse(null);
        subtitle.removeDigital(digital);
        subtitleRepository.save(subtitle);
        itemRepository.save(digital);
        return toDto(digital);
    }

    @Override
    public ItemServiceUtils utils() {
        return itemServiceUtils;
    }

    @Override
    public DigitalDto toDto(Item digital) {
        return digitalMapper.toDto((Digital) digital, itemServiceUtils.getTags(digital), getActors((Digital) digital), getLanguages((Digital) digital), getSubtitles((Digital) digital), itemServiceUtils.getOwners(digital), digital.getStock());
    }
}
