package com.phonestore.catalog.web;

import com.phonestore.catalog.domain.Product;
import com.phonestore.catalog.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/catalog/products", produces = "application/json")
public class ProductController {
    private final ProductService svc;
    public ProductController(ProductService svc){ this.svc = svc; }

    @GetMapping
    public List<Product> list(){ return svc.list(); }

    @GetMapping("/{id}")
    public Product get(@PathVariable("id") Long id) { return svc.get(id); }

    @PutMapping(value = "/{id}", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("id") Long id, @RequestBody @Valid Product p) { svc.update(id, p); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) { svc.delete(id); }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody @Valid Product p){ return svc.create(p); }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    class NotFoundException extends RuntimeException {
        NotFoundException(String msg){ super(msg); }
    }
}

