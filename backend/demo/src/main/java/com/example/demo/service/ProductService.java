package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.entity.ProductStatus;
import com.example.demo.repository.ProductRepository;
import com.example.demo.suggestion.SuggestionService;
import com.example.demo.suggestion.SuggestionService;
import com.example.demo.suggestion.SuggestionTrigger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final SuggestionService suggestionService;

    private final ProductRepository productRepository;

public ProductService(
        ProductRepository productRepository,
        SuggestionService suggestionService) {

    this.productRepository = productRepository;
    this.suggestionService = suggestionService;
}

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateStock(Long id, Integer quantity) {

    Product product = productRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Product not found"));

    int newStock = product.getStockLevel() + quantity;

    if (newStock < 0) {
        throw new RuntimeException("Stock cannot be negative");
    }

    product.setStockLevel(newStock);

    if (newStock == 0) {

        product.setStatus(ProductStatus.OUT_OF_STOCK);

    } else if (
            newStock > 0 &&
            product.getStatus() == ProductStatus.OUT_OF_STOCK) {

        product.setStatus(ProductStatus.ACTIVE);
    }

    Product savedProduct = productRepository.save(product);

    // Inventory-low signal
    if (newStock < product.getReorderThreshold()) {

        product.setStatus(ProductStatus.PRICE_REVIEW_PENDING);
        savedProduct = productRepository.save(product);

        suggestionService.createSuggestions(
                savedProduct,
                SuggestionTrigger.INVENTORY_LOW
        );
    }

    return savedProduct;
}

    public Product createOrder(Long id, Integer quantity) {

    if (quantity == null || quantity <= 0) {
        throw new RuntimeException(
                "Order quantity must be greater than 0");
    }

    Product product = productRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Product not found"));

    if (product.getStockLevel() < quantity) {
        throw new RuntimeException("Insufficient stock");
    }

    int newStock = product.getStockLevel() - quantity;

    product.setStockLevel(newStock);

    if (newStock == 0) {

        product.setStatus(ProductStatus.OUT_OF_STOCK);

    } else if (newStock < product.getReorderThreshold()) {

        product.setStatus(ProductStatus.PRICE_REVIEW_PENDING);
    }

    Product savedProduct = productRepository.save(product);

    // Inventory-low signal
    if (newStock < product.getReorderThreshold()) {

        suggestionService.createSuggestions(
                savedProduct,
                SuggestionTrigger.INVENTORY_LOW
        );
    }

    return savedProduct;
}

    public void deleteProduct(Long id) {

        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }

        productRepository.deleteById(id);
    }

    public void checkForDemandSpike(Long productId) {

    Product product = productRepository.findById(productId)
            .orElseThrow(() ->
                    new RuntimeException("Product not found"));

    Double categoryAverage =
            productRepository.findAverageDemandVelocityByCategory(
                    product.getCategory()
            );

    if (categoryAverage == null || categoryAverage <= 0) {
        return;
    }

    suggestionService.checkDemandSpike(
            product,
            categoryAverage
    );
}
}