package com.bibliobytes.backend.items.books;

import com.bibliobytes.backend.donations.DonationMapper;
import com.bibliobytes.backend.donations.DonationRepository;
import com.bibliobytes.backend.donations.dtos.DonationDto;
import com.bibliobytes.backend.donations.entities.Donation;
import com.bibliobytes.backend.items.ItemService;
import com.bibliobytes.backend.items.books.dtos.BookDto;
import com.bibliobytes.backend.items.books.requests.UpdateAuthorRequest;
import com.bibliobytes.backend.items.books.requests.UpdateIsbnRequest;
import com.bibliobytes.backend.items.books.requests.UpdatePublisherRequest;
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
import com.bibliobytes.backend.validation.validbookid.ValidBookId;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("BookService")
@AllArgsConstructor
public class BookService implements ItemService {
    private final UserRepository userRepository;
    private ItemServiceUtils itemServiceUtils;
    private ItemRepository itemRepository;
    private BookMapper bookMapper;
    private DonationRepository donationRepository;
    private UserMapper userMapper;
    private DonationMapper donationMapper;

//    public BookDto getItemDetails(@ValidBookId long id) {
//        Book book = itemRepository.findBookById(id).orElse(null);
//        return toDto(book);
//    }

    @Transactional
    public DonationDto donateItem(DonateNewItemRequest request, UserService userService) {
        UUID myId = userService.getMyId();
        User me = userRepository.findById(myId).orElse(null);
        Book book = null;
        if (request.getItemId() != null) {
            book = itemRepository.findBookById(request.getItemId()).orElse(null);
        }
        if (book == null) {
            book = bookMapper.toEntity(request);
        }
        itemRepository.save(book);
        itemServiceUtils.addTags(book, request.getTags());
        Donation donation = Donation.builder().owner(me).condition(request.getCondition()).build();
        book.donate(donation);
        itemRepository.save(book);
        donationRepository.save(donation);
        UserDto owner = userMapper.toDto(me);
        BookDto bookDto = toDto(book);
        return donationMapper.toDto(donation, owner, bookDto);
    }

    public ItemDto updateAuthor(Long id, UpdateAuthorRequest request) {
        Book book = (Book) itemRepository.findById(id).orElse(null);
        book.setAuthor(request.getAuthor());
        itemRepository.save(book);
        return toDto(book);
    }

    public ItemDto updatePublisher(Long id, UpdatePublisherRequest request) {
        Book book = (Book) itemRepository.findById(id).orElse(null);
        book.setPublisher(request.getPublisher());
        itemRepository.save(book);
        return toDto(book);
    }

    public ItemDto updateIsbn(Long id, UpdateIsbnRequest request) {
        Book book = (Book) itemRepository.findById(id).orElse(null);
        book.setIsbn(request.getIsbn());
        itemRepository.save(book);
        return toDto(book);
    }

    @Override
    public ItemServiceUtils utils() {
        return itemServiceUtils;
    }

    public BookDto toDto(Item book) {
        return bookMapper.toDto((Book) book, itemServiceUtils.getTags(book), itemServiceUtils.getOwners(book), book.getStock());
    }
}
