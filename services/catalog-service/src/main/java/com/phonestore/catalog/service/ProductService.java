package com.phonestore.catalog.service;

import com.phonestore.catalog.domain.Product;
import com.phonestore.catalog.repo.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> list() {
        return repo.findAll();
    }

    public Product get(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product " + id + " not found"));
    }

    @Transactional
    public Product create(Product p) {
        // На всякий случай: не позволяем клиенту притащить id
        p.setId(null);
        try {
            return repo.save(p);
        } catch (DataIntegrityViolationException ex) {
            // у тебя в БД уникальный индекс по sku; вернём 409 если поймали нарушение
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU already exists", ex);
        }
    }

    @Transactional
    public void update(Long id, Product p) {
        // 404 если нет такой записи
        Product existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product " + id + " not found"));

        // если клиент прислал id в теле и он не совпал — не молча перетирать
        if (p.getId() != null && !id.equals(p.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body id differs from path id");
        }

        // обновляем поля; если хочешь частичные апдейты — сделай patch-метод отдельно
        existing.setSku(p.getSku());
        existing.setName(p.getName());
        existing.setBrand(p.getBrand());
        existing.setPrice(p.getPrice());
        existing.setCurrency(p.getCurrency());
        existing.setDescription(p.getDescription());

        try {
            repo.save(existing);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU already exists", ex);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + id + " not found");
        }
        repo.deleteById(id);
    }
}
