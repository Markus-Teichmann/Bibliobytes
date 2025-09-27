package com.bibliobytes.backend.items;

import com.bibliobytes.backend.items.books.Book;
import com.bibliobytes.backend.items.books.BookService;
import com.bibliobytes.backend.items.digitals.DigitalService;
import com.bibliobytes.backend.items.digitals.entities.Digital;
import com.bibliobytes.backend.items.items.entities.Item;
import com.bibliobytes.backend.items.items.repositorys.ItemRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemServiceDispatcher {
    private ItemService bookService;
    private ItemService digitalService;
    private ItemRepository itemRepository;

    public ItemServiceDispatcher(DigitalService digitalService, BookService bookService, ItemRepository itemRepository) {
        this.bookService = bookService;
        this.digitalService = digitalService;
        this.itemRepository = itemRepository;
    }

    public ItemService dispatch(long id) {
        Item item = itemRepository.findById(id).orElse(null);
        return dispatch(item);
    }

    public ItemService dispatch(Item item) {
        if (item instanceof Book) {
            return bookService;
        }
        if (item instanceof Digital) {
            return digitalService;
        }
        return null;
    }
}
