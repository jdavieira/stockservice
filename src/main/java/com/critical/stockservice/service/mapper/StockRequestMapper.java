package com.critical.stockservice.service.mapper;

import com.critical.stockservice.data.entity.StockRequest;
import com.critical.stockservice.dtos.StockRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface StockRequestMapper {

    StockRequestMapper MAPPER = Mappers.getMapper(StockRequestMapper.class);

    List<StockRequestDto> mapStockRequestsToStockRequestsDto(List<StockRequest> entity);

    StockRequestDto mapStockRequestToStockRequestDto(StockRequest entity);


}