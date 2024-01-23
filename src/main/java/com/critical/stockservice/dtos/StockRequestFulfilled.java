package com.critical.stockservice.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockRequestFulfilled {

    @NotNull
    @Email
    public String userEmail;

    @NotNull
    public int bookId;
}