package com.bibliobytes.backend.donations.requests;

import com.bibliobytes.backend.donations.entities.Condition;
import com.bibliobytes.backend.validation.validcondition.ValidCondition;
import lombok.Data;

@Data
public class UpdateConditionRequest {
    @ValidCondition
    private Condition condition;
}
