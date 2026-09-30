package com.shyam.controller;

import com.shyam.ShyamApplication;
import com.shyam.common.constants.ProductStatus;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.GetProductByIdRequestDTO;
import com.shyam.dto.response.GetProductResponseDTO;
import com.shyam.entity.Category;
import com.shyam.entity.MaterialType;
import com.shyam.entity.Purity;
import com.shyam.entity.Product;
import com.shyam.repository.CategoryRepository;
import com.shyam.repository.MaterialTypeRepository;
import com.shyam.repository.PurityRepository;
import com.shyam.repository.ProductRepository;
import com.shyam.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.HashMap;
import java.util List;
import java.util Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for PublicProductController using H2 in-memory database.
 * This test verifies the complete HTTP endpoint layer functionality.
 */
@SpringBootTest(classes = ShyamApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PublicProductControllerIntegrationTest {

    @Autowired
    private Environment environment;

    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MaterialTypeRepository materialTypeRepository;

    @Autowired
    private PurityRepository purityRepository;

    @Autowired
    private ProductRepository productRepository;

    private Category testCategory;
    private MaterialType testMaterialType;
    private Purity testPurity;
    private String baseUrl = "/api/v1/public/products";

    @BeforeEach
    void setUp() {
        // Get the port from environment
        this.port = Integer.parseInt(environment.getProperty("local.server.port"));

        // Clean database before every test
        productRepository.deleteAll();
        purityRepository.deleteAll();
        materialTypeRepository.deleteAll();
        categoryRepository.deleteAll();

        // Create test category
        testCategory = Category.builder()
                .name("Gold")
                .showOnHome(true)
                .status(true)
                .createdBy("test")
                .build();
        testCategory = categoryRepository.save(testCategory);

        // Create test material type
        testMaterialType = MaterialType.builder()
                .name("22K")
                .status(true)
                .makingChargeType("PER_GRAM")
                .makingChargeValue(
                        java.math.BigDecimal.valueOf(500.00)
                )
                .createdBy("test")
                .build();
        testMaterialType = materialTypeRepository.save(testMaterialType);

        // Create test purity
        testPurity = Purity.builder()
                .purityName("916")
                .purityFactor(
                        java.math.BigDecimal.valueOf(0.916)
                )
                .status(true)
                .materialType(testMaterialType)
                .createdBy("test")
                .build();
        testPurity = purityRepository.save(testPurity);
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        // Public endpoints don't require authentication
        return headers;
    }

    // Helper method to get the most recently added product
    private Product getMostRecentProduct() {
        List<Product> products = productRepository.findAll();
        return products.isEmpty() ? null : products.get(products.size() - 1);
    }

    @Test
    void testGetAllActiveProducts_Success() {
        // Arrange - Create products with different statuses
        // Active product
        Product activeProduct = Product.builder()
                .category(testCategory)
                .materialType(testMaterialType)
                .purity(testPurity)
                .makingChargeType("PER_GRAM")
                .makingChargeValue(java.math.BigDecimal.valueOf(10.0))
                .description("Active gold product")
                .status(ProductStatus.ACTIVE)
                .hallmarkCertified(true)
                .certificationNumber("ACTIVE-CERT")
                .discountType("FLAT")
                .discountValue(java.math.BigDecimal.valueOf(5.0))
                .build();
        productRepository.save(activeProduct);

        // Inactive product (should not appear in results)
        Product inactiveProduct = Product.builder()
                .category(testCategory)
                .materialType(testMaterialType)
                .purity(testPurity)
                .makingChargeType("PER_GRAM")
                .makingChargeValue(java.math.BigDecimal.valueOf(15.0))
                .description("Inactive gold product")
                .status(ProductStatus.INACTIVE)
                .hallmarkCertified(false)
                .build();
        productRepository.save(inactiveProduct);

        // Act
        ResponseEntity<BaseResponseDTO<Page<GetProductResponseDTO>>> response = restTemplate.exchange(
                "http://localhost:" + port + baseUrl,
                HttpMethod.GET,
                new HttpEntity<>(createAuthHeaders()),
                new ParameterizedTypeReference<BaseResponseDTO<Page<GetProductResponseDTO>>>() {}
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getResponse());
        assertFalse(response.getBody().getResponse().getContent().isEmpty());
        assertEquals(1, response.getBody().getResponse().getContent().size());
        assertEquals("Active gold product", response.getBody().getResponse().getContent().get(0).getDescription());
        assertEquals(ProductStatus.ACTIVE.name(), response.getBody().getResponse().getContent().get(0).getStatus());
    }

    @Test
    void testGetAllActiveProducts_WithFilters() {
        // Arrange - Create a second material type for testing
        MaterialType otherMaterialType = MaterialType.builder()
                .name("18K")
                .status(true)
                .makingChargeType("PER_GRAM")
                .makingChargeValue(
                        java.math.BigDecimal.valueOf(300.00)
                )
                .createdBy("test")
                .build();
        otherMaterialType = materialTypeRepository.save(otherMaterialType);

        Purity otherPurity = Purity.builder()
                .purityName("750")
                .purityFactor(
                        java.math.BigDecimal.valueOf(0.750)
                )
                .status(true)
                .materialType(otherMaterialType)
                .createdBy("test")
                .build();
        otherPurity = purityRepository.save(otherPurity);

        // Create product with first material type (22K) - ACTIVE
        Product product1 = Product.builder()
                .category(testCategory)
                .materialType(testMaterialType) // 22K
                .purity(testPurity) // 916
                .makingChargeType("PER_GRAM")
                .makingChargeValue(java.math.BigDecimal.valueOf(10.0))
                .description("22K active product")
                .status(ProductStatus.ACTIVE)
                .createdBy("test")
                .build();
        productRepository.save(product1);

        // Create product with second material type (18K) - ACTIVE
        Product product2 = Product.builder()
                .category(testCategory)
                .materialType(otherMaterialType) // 18K
                .purity(otherPurity) // 750
                .makingChargeType("PER_GRAM")
                .makingChargeValue(java.math.BigDecimal.valueOf(12.0))
                .description("18K active product")
                .status(ProductStatus.ACTIVE)
                .createdBy("test")
                .build();
        productRepository.save(product2);

        // Act - Get products filtered by 22K material type
        ResponseEntity<BaseResponseDTO<Page<GetProductResponseDTO>>> response = restTemplate.exchange(
                "http://localhost:" + port + baseUrl + "?materialTypeId=" + testMaterialType.getMaterialTypeId(),
                HttpMethod.GET,
                new HttpEntity<>(createAuthHeaders()),
                new ParameterizedTypeReference<BaseResponseDTO<Page<GetProductResponseDTO>>>() {}
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getResponse());
        // Would contain only the 22K product in a real detailed assertion
        assertEquals(1, response.getBody().getResponse().getContent().size());
        assertEquals("22K active product", response.getBody().getResponse().getContent().get(0).getDescription());
    }

    @Test
    void testGetProductById_Success() {
        // Arrange - First create a product
        Product product = Product.builder()
                .category(testCategory)
                .materialType(testMaterialType)
                .purity(testPurity)
                .makingChargeType("PER_GRAM")
                .makingChargeValue(java.math.BigDecimal.valueOf(15.0))
                .description("Product to retrieve via API")
                .status(ProductStatus.ACTIVE)
                .hallmarkCertified(false)
                .createdBy("test")
                .build();
        productRepository.save(product);

        Long productId = product.getProductId();

        // Act
        ResponseEntity<org.springframework.common.exception.dto.BaseResponseDTO<GetProductResponseDTO>> response = restTemplate.exchange(
                "http://localhost:" + port + baseUrl + "/" + productId,
                HttpMethod.GET,
                new HttpEntity<>(createAuthHeaders()),
                new ParameterizedTypeReference<org.springframework.common.exception.dto.BaseResponseDTO<GetProductResponseDTO>>() {}
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getResponse());
        assertEquals(productId, response.getBody().getResponse().getProductId());
        assertEquals("Gold 22K 916 #" + productId, response.getBody().getResponse().getProductName());
        assertEquals("Product to retrieve via API", response.getBody().getResponse().getDescription());
        assertEquals(java.math.BigDecimal.valueOf(15.0), response.getBody().getResponse().getMakingChargeValue());
        assertFalse(response.getBody().getResponse().getHallmarkCertified());
    }

    @Test
    void testGetProductById_NonExistingProductId_ReturnsNotFound() {
        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + baseUrl + "/999",
                HttpMethod.GET,
                new HttpEntity<>(createAuthHeaders()),
                String.class
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetProductById_InactiveProduct_ReturnsNotFound() {
        // Arrange - Create an inactive product
        Product inactiveProduct = Product.builder()
                .category(testCategory)
                .materialType(testMaterialType)
                .purity(testPurity)
                .makingChargeType("PER_GRAM")
                .makingChargeValue(java.math.BigDecimal.valueOf(10.0))
                .description("Inactive product")
                .status(ProductStatus.INACTIVE) // Not active
                .createdBy("test")
                .build();
        productRepository.save(inactiveProduct);

        Long productId = inactiveProduct.getProductId();

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + baseUrl + "/" + productId,
                HttpMethod.GET,
                new HttpEntity<>(createAuthHeaders()),
                String.class
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}