package com.bibliobytes.backend.items;

import com.bibliobytes.backend.items.items.ItemServiceUtils;
import com.bibliobytes.backend.items.items.dtos.ItemDto;
import com.bibliobytes.backend.items.items.entities.Item;

public interface ItemService {
    ItemDto toDto(long id);
    ItemServiceUtils utils();
}
