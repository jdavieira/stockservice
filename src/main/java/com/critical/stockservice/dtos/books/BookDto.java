package com.critical.stockservice.dtos.books;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDto {

    public int id;

    public String title;

    public String originalTitle;

    public String isbn;

    public String edition;

    public String synopsis;

    public boolean isSeries;

    public BookAvailabilityDto availability;

    public Date releaseDate;

    public Date editionDate;

    public Float price;

    public Float promotionalPrice;

    public int stockAvailable;

    public Instant createdOn;

    public Instant updatedOn;

    public List<AuthorDto> authors;

    public List<LanguageDto> languages;

    public List<GenreDto> genres;

    public List<TagDto> tags;

    public List<FormatDto> formats;

    public PublisherDto publisher;
}