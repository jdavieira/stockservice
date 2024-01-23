package com.critical.stockservice.service.mapper;

import com.critical.stockservice.data.entity.StockRequest;
import com.critical.stockservice.data.entity.StockRequestHistory;
import com.critical.stockservice.dtos.StockRequestHistoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface StockRequestHistoryMapper {

    StockRequestHistoryMapper MAPPER = Mappers.getMapper(StockRequestHistoryMapper.class);

    List<StockRequestHistoryDto> mapStockRequestsToStockRequestsDto(List<StockRequestHistory> entity);

    StockRequestHistoryDto mapStockRequestToStockRequestDto(StockRequestHistory entity);


    StockRequestHistory mapStockRequestToStockRequestHistory(StockRequest entity);
}