package com.shyam.repository;

import com.shyam.ShyamApplication;
import com.shyam.entity.Category;
import com.shyam.entity.MaterialType;
import com.shyam.entity.Product;
import com.shyam.entity.Purity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for ProductRepository using H2 in-memory database.
 * This test verifies the repository layer operations for Product entity.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({ShyamApplication.class})
class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MaterialTypeRepository materialTypeRepository;

    @Autowired
    private PurityRepository purityRepository;

    private Category testCategory;
    private MaterialType testMaterialType;
    private Purity testPurity;

    @BeforeEach
    void setUp() {
        // Clear all repositories before each test
        productRepository.deleteAll();
        purityRepository.deleteAll();
        materialTypeRepository.deleteAll();
        categoryRepository.deleteAll();

        // Create test category
        testCategory = Category.builder()
                .name("Gold")
                .build();
        testCategory = categoryRepository.save(testCategory);

        // Create test material type
        testMaterialType = MaterialType.builder()
                .name("22K")
                .build();
        testMaterialType = materialTypeRepository.save(testMaterialType);

        // Create test purity
        testPurity = Purity.builder()
                .purityName("916")
                .materialType(testMaterialType)
                .build();
        testPurity = purityRepository.save(testPurity);
    }

    @Test
    void testSaveProduct_Success() {
        // Arrange
        Product product = Product.builder()
                .category(testCategory)
                .materialType(testMaterialType)
                .purity(testPurity)
                .makingChargeType("PERCENTAGE")
                .makingChargeValue(BigDecimal.valueOf(10.0))
                .description("Test gold product")
                .status(com.shyam.common.constants.ProductStatus.DRAFT)
                .hallmarkCertified(true)
                .certificationNumber("CERT123")
                .discountType("PERCENTAGE")
                .discountValue(BigDecimal.valueOf(5.0))
                .build();

        // Act
        Product savedProduct = productRepository.save(product);

        // Assert
        assertNotNull(savedProduct.getProductId());
        assertEquals(testCategory.getCategoryId(), savedProduct.getCategory().getCategoryId());
        assertEquals(testMaterialType.getMaterialTypeId(), savedProduct.getMaterialType().getMaterialTypeId());
        assertEquals(testPurity.getPurityId(), savedProduct.getPurity().getPurityId());
        assertEquals("Test gold product", savedProduct.getDescription());
        assertEquals(com.shyam.common.constants.ProductStatus.DRAFT, savedProduct.getStatus());
    }

    @Test
    void testFindProductById_Success() {
        // Arrange
        Product product = Product.builder()
                .category(testCategory)
                .materialType(testMaterialType)
                .purity(testPurity)
                .makingChargeType("PERCENTAGE")
                .makingChargeValue(BigDecimal.valueOf(10.0))
                .description("Test gold product")
                .status(com.shyam.common.constants.ProductStatus.DRAFT)
                .build();

        Product savedProduct = productRepository.save(product);

        // Act
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getProductId());

        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals(savedProduct.getProductId(), foundProduct.get().getProductId());
        assertEquals(testCategory.getName(), foundProduct.get().getCategory().getName());
        assertEquals(testMaterialType.getName(), foundProduct.get().getMaterialType().getName());
        assertEquals(testPurity.getPurityName(), foundProduct.get().getPurity().getPurityName());
    }

    @Test
    void testFindProductById_NotFound() {
        // Act
        Optional<Product> foundProduct = productRepository.findById(999L);

        // Assert
        assertFalse(foundProduct.isPresent());
    }

    @Test
    void testDeleteProduct_Success() {
        // Arrange
        Product product = Product.builder()
                .category(testCategory)
                .materialType(testMaterialType)
                .purity(testPurity)
                .makingChargeType("PERCENTAGE")
                .makingChargeValue(BigDecimal.valueOf(10.0))
                .description("Test gold product")
                .status(com.shyam.common.constants.ProductStatus.DRAFT)
                .build();

        Product savedProduct = productRepository.save(product);

        // Act
        productRepository.deleteById(savedProduct.getProductId());

        // Assert
        Optional<Product> deletedProduct = productRepository.findById(savedProduct.getProductId());
        assertFalse(deletedProduct.isPresent());
    }

    @Test
    void testGetAllProducts_ReturnsAllProducts() {
        // Arrange
        Product product1 = Product.builder()
                .category(testCategory)
                .materialType(testMaterialType)
                .purity(testPurity)
                .makingChargeType("PERCENTAGE")
                .makingChargeValue(BigDecimal.valueOf(10.0))
                .description("Test product 1")
                .status(com.shyam.common.constants.ProductStatus.DRAFT)
                .build();

        Product product2 = Product.builder()
                .category(testCategory)
                .materialType(testMaterialType)
                .purity(testPurity)
                .makingChargeType("PERCENTAGE")
                .makingChargeValue(BigDecimal.valueOf(15.0))
                .description("Test product 2")
                .status(com.shyam.common.constants.ProductStatus.ACTIVE)
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        // Act
        var products = productRepository.findAll();

        // Assert
        assertEquals(2, products.size());
    }
}