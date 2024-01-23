package com.critical.stockservice.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockRequestHistoryDto {

    public int id;

    public int stockRequest;

    public StockDto stock;
}