package com.critical.stockservice.data.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookStockRequestEvent {

    public int bookId;

    public int stock;

    public String userEmail;

    public UUID messageId;
}