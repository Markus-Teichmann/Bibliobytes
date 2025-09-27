package com.bibliobytes.backend.items.items.requests;

import com.bibliobytes.backend.donations.entities.Condition;
import com.bibliobytes.backend.validation.validcondition.ValidCondition;
import lombok.Data;

@Data
public class DonateItemRequest {
    @ValidCondition
    private Condition condition;
}
