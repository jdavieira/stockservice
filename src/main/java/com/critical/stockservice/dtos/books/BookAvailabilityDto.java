package com.critical.stockservice.dtos.books;

import lombok.Getter;

@Getter
public enum BookAvailabilityDto { TO_BE_LAUNCHED(0),
    ON_PRE_ORDER(1),
    ON_ORDER(2),
    AVAILABLE(3);

    private final int value;

    BookAvailabilityDto(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}