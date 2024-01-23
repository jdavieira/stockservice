package com.critical.stockservice.service.mapper;

import com.critical.stockservice.data.entity.Stock;
import com.critical.stockservice.dtos.StockDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface StockMapper {
    StockMapper MAPPER = Mappers.getMapper(StockMapper.class);

    List<StockDto> mapStocksToStocksDto(List<Stock> entity);

    StockDto mapStockToStockDto(Stock entity);
}