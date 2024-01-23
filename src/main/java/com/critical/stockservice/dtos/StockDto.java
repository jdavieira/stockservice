package com.critical.stockservice.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockDto {

    public int id;

    public int bookId;

    public int stock;
}