package com.phonestore.catalog.web;


import com.phonestore.catalog.domain.Product;
import com.phonestore.catalog.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/catalog/products")
public class ProductController {
    private final ProductService svc;
    public ProductController(ProductService svc){this.svc=svc;}


    @GetMapping public List<Product> list(){ return svc.list(); }
    @GetMapping("/{id}") public Product get(@PathVariable Long id){ return svc.get(id);}
    @PostMapping public Product create(@RequestBody Product p){ return svc.create(p);}
    @PutMapping("/{id}") public Product update(@PathVariable Long id, @RequestBody Product p){ return svc.update(id,p);}
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id){ svc.delete(id);}
}