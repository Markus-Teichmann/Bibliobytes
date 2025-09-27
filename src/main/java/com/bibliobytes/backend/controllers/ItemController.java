package com.bibliobytes.backend.controllers;

import com.bibliobytes.backend.donations.dtos.DonationDto;
import com.bibliobytes.backend.items.ItemService;
import com.bibliobytes.backend.items.ItemServiceDispatcher;
import com.bibliobytes.backend.items.books.BookService;
import com.bibliobytes.backend.items.digitals.DigitalService;
import com.bibliobytes.backend.items.items.ItemServiceUtils;
import com.bibliobytes.backend.items.books.requests.UpdateAuthorRequest;
import com.bibliobytes.backend.items.books.requests.UpdateIsbnRequest;
import com.bibliobytes.backend.items.books.requests.UpdatePublisherRequest;
import com.bibliobytes.backend.items.digitals.requests.*;
import com.bibliobytes.backend.items.items.dtos.*;
import com.bibliobytes.backend.items.items.requests.*;
import com.bibliobytes.backend.rentals.dtos.RentalDto;
import com.bibliobytes.backend.users.UserService;
import com.bibliobytes.backend.users.requests.SearchRequest;
import com.bibliobytes.backend.validation.validbookid.ValidBookId;
import com.bibliobytes.backend.validation.validdigitalid.ValidDigitalId;
import com.bibliobytes.backend.validation.validitemid.ValidItemId;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemServiceUtils itemServiceUtils;
    private final BookService bookService;
    private final DigitalService digitalService;
    private ItemServiceDispatcher itemServiceDispatcher;
    private UserService userService;

    @GetMapping()
    public ResponseEntity<Set<ItemDto>> allItems(){
        return ResponseEntity.ok(itemServiceUtils.getItems());
    }

    //ToDo: Searching is still a Todo.
    @GetMapping("/search")
    public ResponseEntity<Set<ItemDto>> search(
            @Valid @RequestBody SearchRequest request
    ) {
        return null;
    }

    @GetMapping("/new")
    public ResponseEntity<Set<ItemDto>> donations() {
        return ResponseEntity.ok(itemServiceUtils.getApprovableItems());
    }

    /*
        Fügt genau ein Item bzw. eine Donation hinzu.
     */
    @PostMapping("/donate")
    public ResponseEntity<DonationDto> donateItem(
            @Valid @RequestBody DonateNewItemRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        DonationDto dto = null;
        if (request.isValidBook()) {
            dto = bookService.donateItem(request);
        }
        if (request.isValidDigital()) {
            dto = digitalService.donateItem(request);
        }
        URI uri = uriBuilder.path("/donations/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }
//        if (
//                request.getType() == Type.DIGITAL && (
//                        request.getRuntime() == null ||
//                                request.getActors() == null ||
//                                request.getLanguages() == null
//                )) {
//            return ResponseEntity.badRequest().body(
//                    Map.of(
//                            "message", "For a Digital Runtime, Actors and Languages are required"
//                    )
//            );
//        }
//
//        if (
//                request.getType() == Type.BOOK && (
//                        request.getPublisher() == null ||
//                                request.getAuthor() == null ||
//                                request.getIsbn() == null
//                )) {
//            return ResponseEntity.badRequest().body(
//                    Map.of(
//                            "message", "For a Book Publisher, Author and ISBN are required"
//                    )
//            );
//        }
//        if (
//                (request.getType() == Type.BOOK && request.getRuntime() != null) ||
//                        (request.getType() == Type.DIGITAL && request.getIsbn() != null)
//        ) {
//            return ResponseEntity.badRequest().body(
//                    Map.of(
//                            "message", "Media Type and Information missmatch!"
//                    )
//            );
//        }

//        User me = userService.findMe();
//        Item item = itemServiceUtils.saveItem(request, me);
//        Set<TagDto> tags = itemServiceUtils.getTags(item);
//        Set<UserDto> owners = itemServiceUtils.getOwners(item);
//        int stock = itemServiceUtils.getStock(item);
//        if (request.getType() == Type.BOOK) {
//            URI uri = uriBuilder.path("/items/{id}").buildAndExpand(item.getId()).toUri();
//            return ResponseEntity.created(uri).body(bookMapper.toDto((Book) item, tags, owners, stock));
//        } else if (request.getType() == Type.DIGITAL) {
//            Set<ActorDto> actors = itemServiceUtils.getActors((Digital) item);
//            Set<LanguageDto> languages = itemServiceUtils.getLanguages((Digital) item);
//            Set<SubtitleDto> subtitles = itemServiceUtils.getSubtitles((Digital) item);
//            var uri = uriBuilder.path("/items/{id}").buildAndExpand(item.getId()).toUri();
//            return ResponseEntity.created(uri).body(digitalMapper.toDto((Digital) item, tags, actors, languages, subtitles, owners, stock));
//        } else {
//            return ResponseEntity.badRequest().body(
//                    Map.of(
//                            "message", "Type is either BOOK or DIGITAL and is required"
//                    )
//            );
//        }

    @GetMapping("/{id}")
    public ResponseEntity<?> itemDetails(
            @PathVariable @ValidItemId Long id
    ) {
        ItemService service = itemServiceDispatcher.dispatch(id);
        ItemDto dto = service.utils().getItemDetails(id, service);
        //ItemDto dto = itemServiceUtils.getItemDetails(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/title")
    public ResponseEntity<?> updateTitle(
            @PathVariable @ValidItemId Long id,
            @Valid @RequestBody UpdateTitleRequest request
    ) {
        ItemService service = itemServiceDispatcher.dispatch(id);
        ItemDto item = service.utils().updateTitle(id, request, service);
        //ItemDto item = itemServiceUtils.updateTitle(id, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}/place")
    public ResponseEntity<?> updatePlace(
            @PathVariable @ValidItemId Long id,
            @Valid @RequestBody UpdatePlaceRequest request
    ) {
        ItemService service = itemServiceDispatcher.dispatch(id);
        ItemDto item = service.utils().updatePlace(id, request, service);
//        ItemDto item = itemServiceUtils.updatePlace(id, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}/topic")
    public ResponseEntity<?> updateTopic(
            @PathVariable @ValidItemId Long id,
            @Valid @RequestBody UpdateTopicRequest request
    ) {
        ItemService service = itemServiceDispatcher.dispatch(id);
        ItemDto item = service.utils().updateTopic(id, request, service);
//        ItemDto item = itemServiceUtils.updateTopic(id, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}/note")
    public ResponseEntity<?> updateNote(
            @PathVariable @ValidItemId Long id,
            @Valid @RequestBody UpdateNoteRequest request
    ) {
        ItemService service = itemServiceDispatcher.dispatch(id);
        ItemDto item = service.utils().updateNote(id, request, service);
//        ItemDto item = itemServiceUtils.updateNote(id, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}/addTag")
    public ResponseEntity<?> addTag(
            @PathVariable @ValidItemId Long id,
            @Valid @RequestBody AddTagRequest request
    ) {
        ItemService service = itemServiceDispatcher.dispatch(id);
        ItemDto item = service.utils().addTag(id, request, service);
//        ItemDto item = itemServiceUtils.addTag(id, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}/removeTag")
    public ResponseEntity<?> removeTag(
            @PathVariable @ValidItemId Long id,
            @Valid @RequestBody RemoveTagRequest request
    ) {
        ItemService service = itemServiceDispatcher.dispatch(id);
        ItemDto item = service.utils().removeTag(id, request, service);
//        ItemDto item = itemServiceUtils.removeTag(id, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}/author")
    public ResponseEntity<?> updateAuthor(
            @PathVariable @ValidBookId Long id,
            @Valid @RequestBody UpdateAuthorRequest request
    ) {
        ItemDto item = bookService.updateAuthor(id, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}/publisher")
    public ResponseEntity<?> updatePublisher(
            @PathVariable @ValidBookId Long id,
            @Valid @RequestBody UpdatePublisherRequest request
    ) {
        ItemDto item = bookService.updatePublisher(id, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}/isbn")
    public ResponseEntity<?> updateIsbn(
            @PathVariable @ValidBookId Long id,
            @Valid @RequestBody UpdateIsbnRequest request
    ) {
        ItemDto item = bookService.updateIsbn(id, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}/runtime")
    public ResponseEntity<?> updateRuntime(
            @PathVariable @ValidDigitalId Long id,
            @Valid @RequestBody UpdateRuntimeRequest request
    ) {
        ItemDto item = digitalService.updateRuntime(id, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}/label")
    public ResponseEntity<?> updateLabel(
            @PathVariable @ValidDigitalId Long id,
            @Valid @RequestBody UpdateLabelRequest request
    ) {
        ItemDto item = digitalService.updateLabel(id, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}/production")
    public ResponseEntity<?> updateProduction(
            @PathVariable @ValidDigitalId Long id,
            @Valid @RequestBody UpdateProductionRequest request
    ) {
        ItemDto item = digitalService.updateProduction(id, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}/addActor")
    public ResponseEntity<?> addActor(
            @PathVariable @ValidDigitalId Long id,
            @Valid @RequestBody AddActorRequest request
    ) {
        ItemDto item = digitalService.addActor(id, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}/removeActor")
    public ResponseEntity<?> removeActor(
            @PathVariable @ValidDigitalId Long id,
            @Valid @RequestBody RemoveActorRequest request
    ) {
        ItemDto item = digitalService.removeActor(id, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}/addLanguage")
    public ResponseEntity<?> addLanguage(
            @PathVariable @ValidDigitalId Long id,
            @Valid @RequestBody AddLanguageRequest request
    ) {
        ItemDto item = digitalService.addLanguage(id, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}/removeLanguage")
    public ResponseEntity<?> removeLanguage(
            @PathVariable @ValidDigitalId Long id,
            @Valid @RequestBody RemoveLanguageRequest request
    ) {
        ItemDto item = digitalService.removeLanguage(id, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}/addSubtitle")
    public ResponseEntity<?> addSubtitle(
            @PathVariable @ValidDigitalId Long id,
            @Valid @RequestBody AddSubtitleRequest request
    ) {
        ItemDto item = digitalService.addSubtitle(id, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}/removeSubtitle")
    public ResponseEntity<?> removeSubtitle(
            @PathVariable @ValidDigitalId Long id,
            @Valid @RequestBody RemoveSubtitleRequest request
    ) {
        ItemDto item = digitalService.removeSubtitle(id, request);
        return ResponseEntity.ok(item);
    }

    @PostMapping("/{id}/donate")
    public ResponseEntity<?> donateItem(
            @PathVariable @ValidItemId Long id,
            @Valid @RequestBody DonateItemRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        ItemService service = itemServiceDispatcher.dispatch(id);
        DonationDto dto = service.utils().donateItem(id, request.getCondition(), userService, service);
//        DonationDto dto = itemServiceUtils.donateItem(id, request.getCondition());
        URI uri = uriBuilder.path("/donations/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PostMapping("/{id}/rent")
    public ResponseEntity<?> rentItem(
            @PathVariable @ValidItemId Long id,
            @Valid @RequestBody RentItemRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        ItemService service = itemServiceDispatcher.dispatch(id);
        RentalDto rental = service.utils().rentItem(id, request, userService, service);
//        RentalDto rental = itemServiceUtils.rentItem(id, request);
        URI uri = uriBuilder.path("/rentals/{id}").buildAndExpand(rental.getId()).toUri();
        return ResponseEntity.created(uri).body(rental);
    }

    /*
        Angefragte Leihe akzeptieren oder verändern.
        Nur den Status updaten, der Rest wir dann unter /rentals/{id}/external etc.
        zu verändern sein.
     */
    @PutMapping("/{id}/rent")
    public ResponseEntity<?> decideOnRental(
            @PathVariable @ValidItemId Long id,
            @Valid @RequestBody DecideOnRentalStateRequest request
    ) {
        ItemService service = itemServiceDispatcher.dispatch(id);
        RentalDto rental = service.utils().decideOnRental(id, request, service);
//        RentalDto rental = itemServiceUtils.decideOnRental(id, request);
        return ResponseEntity.ok(rental);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(
            @PathVariable @ValidItemId Long id
    ) {
        ItemService service = itemServiceDispatcher.dispatch(id);
        ItemDto itemDto = service.utils().deleteItem(id, service);
//        ItemDto itemDto = itemServiceUtils.deleteItem(id);
        return ResponseEntity.ok(itemDto);
    }

}
