package com.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.product.entity.Product;
import com.product.repo.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional("productTransactionManager")
public class ProductService {

	private final ProductRepository repo;

	public ProductService(ProductRepository repo) {
		this.repo = repo;
	}

	public Product create(Product p) {
		return repo.save(p);
	}

	public Optional<Product> findById(Long id) {
		return repo.findById(id);
	}

	public List<Product> findAll() {
		return repo.findAll();
	}

	public Product update(Long id, Product updated) {
		return repo.findById(id).map(p -> {
			p.setName(updated.getName());
			p.setPrice(updated.getPrice());
			return repo.save(p);
		}).orElseThrow(() -> new RuntimeException("Product not found"));
	}

	public void delete(Long id) {
		repo.deleteById(id);
	}
}
