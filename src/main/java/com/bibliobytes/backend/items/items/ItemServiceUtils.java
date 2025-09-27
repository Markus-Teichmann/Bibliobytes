package com.bibliobytes.backend.items.items;

import com.bibliobytes.backend.donations.DonationMapper;
import com.bibliobytes.backend.donations.DonationRepository;
import com.bibliobytes.backend.donations.dtos.DonationDto;
import com.bibliobytes.backend.donations.entities.Condition;
import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.donations.entities.DonationState;
import com.bibliobytes.backend.items.ItemService;
import com.bibliobytes.backend.items.items.dtos.*;
import com.bibliobytes.backend.items.items.entities.*;
import com.bibliobytes.backend.items.items.mappers.*;
import com.bibliobytes.backend.items.items.repositorys.*;
import com.bibliobytes.backend.items.items.requests.*;
import com.bibliobytes.backend.rentals.RentalMapper;
import com.bibliobytes.backend.rentals.RentalRepository;
import com.bibliobytes.backend.rentals.dtos.RentalDto;
import com.bibliobytes.backend.rentals.entities.Rental;
import com.bibliobytes.backend.rentals.entities.RentalState;
import com.bibliobytes.backend.users.UserMapper;
import com.bibliobytes.backend.users.UserRepository;
import com.bibliobytes.backend.users.UserService;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("ItemService")
@AllArgsConstructor
public class ItemServiceUtils {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
//    private UserService userService;
    private DonationRepository donationRepository;
    private RentalRepository rentalRepository;
    private TagRepository tagRepository;
    private UserMapper userMapper;
    private TagMapper tagMapper;
    private ItemMapper itemMapper;
    private DonationMapper donationMapper;
    private RentalMapper rentalMapper;

    public Set<TagDto> getTags(Item item) {
        return tagRepository.findAllByItemId(item.getId()).stream()
                .map(tag -> tagMapper.toDto(tag)).collect(Collectors.toSet());
    }

    public Set<UserDto> getOwners(Item item) {
        return itemRepository.findOwnersOfWithState(item.getId(), DonationState.ACCEPTED.name())
                .stream().map(user -> userMapper.toDto(user))
                .collect(Collectors.toSet());
    }

    public Set<ItemDto> getItems() {
        return itemRepository.findAllItems().stream()
                .map(item -> toDto(item)).collect(Collectors.toSet());
    }

    public Set<ItemDto> getApprovableItems() {
        Set<ItemDto> set = itemRepository.findAllByDonationStatus(DonationState.APPLIED).stream()
                .map(item -> toDto(item)).collect(Collectors.toSet());
        set.addAll(itemRepository.findAllByRentalState(RentalState.REQUESTED).stream()
                .map(item -> toDto(item)).collect(Collectors.toSet()));
        return set;
    }

    public ItemDto getItemDetails(long id, ItemService service) {
        Item item = itemRepository.findById(id).orElse(null);
        return service.toDto(item);
    }
//    public ItemDto getItemDetails(long id) {
//        Item item = itemRepository.findById(id).orElse(null);
//        ItemServiceUtils itemService = itemServiceDispatcher.dispatch(item);
//        return itemService.getItemDetails(id);
////        if (item instanceof Book) {
////            return bookService.getItemDetails(id);
////        }
////        return digitalService.getItemDetails(id);
//    }

    @Transactional
    public void addTags(Item item, Set<String> tags) {
        for (String tagName : tags) {
            Tag tag = tagRepository.findByName(tagName).orElse(null);
            if (tag == null) {
                tag = new Tag();
                tag.setName(tagName);
            }
            item.addTag(tag);
            tagRepository.save(tag);
        }
        itemRepository.save(item);
    }

    @Transactional
    public DonationDto donateItem(Long itemId, Condition condition, UserService userService ,ItemService service) {
        User me = userService.findMe();
        Item item = itemRepository.findById(itemId).orElse(null);
        Donation donation = Donation.builder().owner(me).condition(condition).build();
        item.donate(donation);
        itemRepository.save(item);
        donationRepository.save(donation);
        UserDto owner = userMapper.toDto(me);
        ItemDto itemDto = service.toDto(item);
        return donationMapper.toDto(donation, owner, itemDto);
    }

    @Transactional
    public RentalDto rentItem(Long itemId, RentItemRequest request, UserService userService, ItemService itemService) {
        User external = null;
        if (request.registerNewExternal()) {
            external = userService.registerExternal(
                    request.getExternalEmail(),
                    request.getExternalFirstName(),
                    request.getExternalLastName()
            );
        }
        if (request.forExternalUser()) {
            external = userRepository.findById(request.getExternalId()).orElse(null);
        }
        User user = userService.findMe();
        Donation donation = donationRepository.findById(request.getDonationId()).orElse(null);
        Rental rental = rentalMapper.toEntity(donation, user, request, external);
        rentalRepository.save(rental);
        Item item = itemRepository.findById(itemId).orElse(null);
        return rentalMapper.toDto(
                rental,
                itemService.toDto(item),
                userMapper.toDto(user),
                userMapper.toDto(external)
        );
    }

    public RentalDto decideOnRental(Long itemId, DecideOnRentalStateRequest request, ItemService service) {
        Rental rental = rentalRepository.findById(request.getRentalId()).orElse(null);
        rental.setStatus(request.getState());
        rentalRepository.save(rental);
        User user = rental.getUser();
        User external = rental.getExternal();
        Item item = itemRepository.findById(itemId).orElse(null);
        return rentalMapper.toDto(
                rental,
                service.toDto(item),
                userMapper.toDto(user),
                userMapper.toDto(external)
        );
    }

    public ItemDto updateTitle(Long id, UpdateTitleRequest request, ItemService service) {
        Item item = itemRepository.findById(id).orElse(null);
        item.setTitel(request.getTitel());
        itemRepository.save(item);
        return service.toDto(item);
    }

    public ItemDto updatePlace(Long id, UpdatePlaceRequest request, ItemService service) {
        Item item = itemRepository.findById(id).orElse(null);
        item.setPlace(request.getPlace());
        itemRepository.save(item);
        return service.toDto(item);
    }

    public ItemDto updateTopic(Long id, UpdateTopicRequest request, ItemService service) {
        Item item = itemRepository.findById(id).orElse(null);
        item.setTopic(request.getTopic());
        itemRepository.save(item);
        return service.toDto(item);
    }

    public ItemDto updateNote(Long id, UpdateNoteRequest request, ItemService service) {
        Item item = itemRepository.findById(id).orElse(null);
        item.setNote(request.getNote());
        itemRepository.save(item);
        return service.toDto(item);
    }

    @Transactional
    public ItemDto addTag(Long id, AddTagRequest request, ItemService service) {
        Item item = itemRepository.findById(id).orElse(null);
        Tag tag = tagRepository.findById(request.getId()).orElse(null);
        if (tag == null) {
            tag = new Tag();
            tag.setName(request.getName());
        }
        tag.addItem(item);
        tagRepository.save(tag);
        itemRepository.save(item);
        return service.toDto(item);
    }

    @Transactional
    public ItemDto removeTag(Long id, RemoveTagRequest request, ItemService service) {
        Item item = itemRepository.findById(id).orElse(null);
        Tag tag = tagRepository.findById(request.getId()).orElse(null);
        tag.removeItem(item);
        tagRepository.save(tag);
        itemRepository.save(item);
        return service.toDto(item);
    }

    public ItemDto deleteItem(Long id, ItemService service) {
        Item item = itemRepository.findById(id).orElse(null);
        item.setState(ItemState.REMOVED);
        itemRepository.save(item);
        return service.toDto(item);
    }

    public ItemDto toDto(Item item) {
        return itemMapper.toDto(item, getTags(item), getOwners(item), item.getStock());
    }

//    public int getStock(Item item) {
//        int acceptedDonations = donationRepository.countByStatusAndItem(DonationState.ACCEPTED, item);
//        int approvedRentals = rentalRepository.countByStatusAndItem(RentalState.APPROVED, item);
//        return acceptedDonations - approvedRentals;
//    }

    /*
        Gibt den aktuell ausleihbaren Bestand zurück.
     */

    //    @Transactional
//    public Item saveItem(DonateNewItemRequest request, User owner) {
//        Item item = null;
//        if (request.getItemId() != null) {
//            item = itemRepository.findById(request.getItemId()).orElseThrow(EntityNotFoundException::new);
//        } else if (request.getType() == Type.BOOK) {
//            item = bookMapper.toEntity(request);
//        } else {
//            item = digitalMapper.toEntity(request);
//            for (String actorName : request.getActors()) {
//                Actor actor = actorRepository.findByName(actorName).orElse(null);
//                if (actor == null) {
//                    actor = new Actor();
//                    actor.setName(actorName);
//                }
//                actor.addDigital((Digital) item);
//                actorRepository.save(actor);
//            }
//            for (String subtitleLanguage : request.getSubtitles()) {
//                Subtitle subtitle = subtitleRepository.findByLanguage(subtitleLanguage).orElse(null);
//                if (subtitle == null) {
//                    subtitle = new Subtitle();
//                    subtitle.setLanguage(subtitleLanguage);
//                }
//                subtitle.addDigital((Digital) item);
//                subtitleRepository.save(subtitle);
//            }
//            for (String languageName : request.getLanguages()) {
//                Language language = languageRepository.findByName(languageName).orElse(null);
//                if (language == null) {
//                    language = new Language();
//                    language.setName(languageName);
//                }
//                language.addDigital((Digital) item);
//                languageRepository.save(language);
//            }
//        }
//        for (String tagName: request.getTags()) {
//            Tag tag = tagRepository.findByName(tagName).orElse(null);
//            if (tag == null) {
//                tag = new Tag();
//                tag.setName(tagName);
//            }
//            //"Adding Tag -- Runs second select statement" -- because of the !contains it has to check
//            item.addTag(tag);
//            tagRepository.save(tag);
//        }
//        Donation donation = Donation.builder().owner(owner).condition(request.getCondition()).build();
//        item.donate(donation);
//        itemRepository.save(item);
//        //Donation references book wich is now transient so this method needs to be transactional
//        //And also the Donation must be saved after the item is saved.
//        donationRepository.save(donation);
//        return item;
//    }
}
