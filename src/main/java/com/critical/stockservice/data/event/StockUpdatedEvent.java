package com.critical.stockservice.data.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdatedEvent {
    public int bookId;

    public int stock;

    public String userEmail;
}