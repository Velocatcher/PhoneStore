package com.phonestore.search.web;


import com.phonestore.common.dto.ProductDto;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;


@RestController
@RequestMapping("/api/search")
public class SearchController {
    @GetMapping
    public List<ProductDto> search(@RequestParam String q){
// Временно мок: отдает один результат, если строка похожа на телефон
        if(q.toLowerCase().contains("iphone")){
            return List.of(new ProductDto(1L,"IPH-15-128-BLK","iPhone 15 128GB Black","Apple", new BigDecimal("799.00"),"USD","Mocked"));
        }
        if(q.toLowerCase().contains("s24")){
            return List.of(new ProductDto(2L,"SMG-S24-256-GRY","Samsung Galaxy S24 256GB Gray","Samsung", new BigDecimal("899.00"),"USD","Mocked"));
        }
        return Collections.emptyList();
    }
}