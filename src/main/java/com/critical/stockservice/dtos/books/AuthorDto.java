package com.critical.stockservice.dtos.books;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.ReadOnlyProperty;

import java.sql.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorDto {
    @ReadOnlyProperty
    public int id;
    @NotNull
    @Size(min = 1, max = 255)
    public String name;
    @NotNull
    @Size(min = 1, max = 500)
    public String originalName;
    @NotNull
    public Date dateOfBirth;
    @NotNull
    @Size(min = 1, max = 255)
    public String placeOfBirth;
    public Date dateOfDeath;
    @Size(min = 1, max = 255)
    public String placeOfDeath;
    @Size(min = 1, max = 500)
    public String about;
}