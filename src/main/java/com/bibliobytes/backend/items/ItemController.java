package com.bibliobytes.backend.items;

import com.bibliobytes.backend.items.dtos.*;
import com.bibliobytes.backend.items.entities.Book;
import com.bibliobytes.backend.items.entities.Digital;
import com.bibliobytes.backend.items.entities.Item;
import com.bibliobytes.backend.items.entities.Type;
import com.bibliobytes.backend.items.mappers.BookMapper;
import com.bibliobytes.backend.items.mappers.DigitalMapper;
import com.bibliobytes.backend.users.UserService;
import com.bibliobytes.backend.users.dtos.UserDto;
import com.bibliobytes.backend.users.entities.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final BookMapper bookMapper;
    private final DigitalMapper digitalMapper;
    private final UserService userService;

    /*
        Fügt genau ein Item hinzu.
     */
    @PostMapping("/donate")
    public ResponseEntity<?> donateItem(
            @Valid @RequestBody DonationRequest request,
            UriComponentsBuilder uriBuilder
            ) {
        if (
                request.getType() == Type.DIGITAL && (
                request.getRuntime() == null ||
                request.getActors() == null ||
                request.getLanguages() == null
        )) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message", "For a Digital Runtime, Actors and Languages are required"
                    )
            );
        }

        if (
                request.getType() == Type.BOOK && (
                request.getPublisher() == null ||
                request.getAuthor() == null ||
                request.getIsbn() == null
        )) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message", "For a Book Publisher, Author and ISBN are required"
                    )
            );
        }

        if (
                (request.getType() == Type.BOOK && request.getRuntime() != null) ||
                (request.getType() == Type.DIGITAL && request.getIsbn() != null)
        ) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message", "Media Type and Information missmatch!"
                    )
            );
        }

        User me = userService.findMe();
        Item item = itemService.saveItem(request, me);
        Set<TagDto> tags = itemService.getTags(item);
        Set<UserDto> owners = itemService.getOwners(item);
        int stock = itemService.getStock(item);
        if (request.getType() == Type.BOOK) {
            var uri = uriBuilder.path("/items/{id}").buildAndExpand(item.getId()).toUri();
            return ResponseEntity.created(uri).body(bookMapper.toDto((Book) item, tags, owners, stock));
        } else if (request.getType() == Type.DIGITAL) {
            Set<ActorDto> actors = itemService.getActors((Digital) item);
            Set<LanguageDto> languages = itemService.getLanguages((Digital) item);
            Set<SubtitleDto> subtitles = itemService.getSubtitles((Digital) item);
            var uri = uriBuilder.path("/items/{id}").buildAndExpand(item.getId()).toUri();
            return ResponseEntity.created(uri).body(digitalMapper.toDto((Digital) item, tags, actors, languages, subtitles, owners, stock));
        } else {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message", "Type is either BOOK or DIGITAL and is required"
                    )
            );
        }
    }
}
