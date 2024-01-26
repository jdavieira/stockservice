package com.critical.stockservice.service.mapper;

import com.critical.stockservice.data.entity.StockRequest;
import com.critical.stockservice.dtos.StockRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface StockRequestMapper {

    StockRequestMapper MAPPER = Mappers.getMapper(StockRequestMapper.class);


    @Mappings({
            @Mapping(target = "bookId", source = "stock.bookId")
    })
    List<StockRequestDto> mapStockRequestsToStockRequestsDto(List<StockRequest> entity);
    @Mappings({
            @Mapping(target = "bookId", source = "stock.bookId")
    })
    StockRequestDto mapStockRequestToStockRequestDto(StockRequest entity);
}