package com.phonestore.catalog.service;


import com.phonestore.catalog.domain.Product;
import com.phonestore.catalog.repo.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class ProductService {
    private final ProductRepository repo;
    public ProductService(ProductRepository repo){this.repo=repo;}
    public List<Product> list(){return repo.findAll();}
    public Product get(Long id){return repo.findById(id).orElseThrow();}
    public Product create(Product p){return repo.save(p);}
    public Product update(Long id, Product p){p.setId(id);return repo.save(p);}
    public void delete(Long id){repo.deleteById(id);}
}