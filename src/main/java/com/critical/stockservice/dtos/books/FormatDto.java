package com.critical.stockservice.dtos.books;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.ReadOnlyProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormatDto {

    @ReadOnlyProperty
    public int id;

    @NotNull
    @Size(min = 1, max = 50)
    public String name;
}