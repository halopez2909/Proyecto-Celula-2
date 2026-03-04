package com.reto.catalog.service;

import com.reto.catalog.dto.ProductRequest;
import com.reto.catalog.entity.ProductEntity;
import com.reto.catalog.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductEntity create(ProductRequest request) {
        ProductEntity entity = new ProductEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setPrice(request.getPrice());
        entity.setSku(request.getSku());
        return productRepository.save(entity);
    }

    public List<ProductEntity> findAll() {
        return productRepository.findAll();
    }

    public Optional<ProductEntity> findById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Optional<ProductEntity> update(Long id, ProductRequest request) {
        return productRepository.findById(id)
                .map(entity -> {
                    entity.setName(request.getName());
                    entity.setDescription(request.getDescription());
                    entity.setPrice(request.getPrice());
                    entity.setSku(request.getSku());
                    return productRepository.save(entity);
                });
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
