package com.critical.stockservice.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.ReadOnlyProperty;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockRequestDto {

    @NotNull
    @ReadOnlyProperty
    public int id;

    @NotNull
    @Min(1)
    public int stockRequest;

    @NotNull
    public String userEmail;

    @NotNull
    public int bookId;
}