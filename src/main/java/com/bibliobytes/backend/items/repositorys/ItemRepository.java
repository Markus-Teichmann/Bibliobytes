package com.bibliobytes.backend.items.repositorys;

import com.bibliobytes.backend.items.entities.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {
    Item findById(long id);
}
