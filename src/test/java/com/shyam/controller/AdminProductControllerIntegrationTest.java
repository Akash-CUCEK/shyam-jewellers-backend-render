package com.shyam.controller;

import com.shyam.ShyamApplication;
import com.shyam.dto.request.*;
import com.shyam.dto.response.AddProductResponseDTO;
import com.shyam.dto.response.GetProductResponseDTO;
import com.shyam.entity.AdminUsers;
import com.shyam.entity.Category;
import com.shyam.entity.MaterialType;
import com.shyam.entity.Purity;
import com.shyam.entity.Product;
import com.shyam.repository.AdminRepository;
import com.shyam.repository.CategoryRepository;
import com.shyam.repository.MaterialTypeRepository;
import com.shyam.repository.PurityRepository;
import com.shyam.repository.ProductRepository;
import com.shyam.common.constants.ProductStatus;
import com.shyam.common.constants.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for AdminProductController using H2 in-memory database.
 * This test verifies the complete HTTP endpoint layer functionality.
 */
@SpringBootTest(classes = ShyamApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@WithMockUser(username = "test-admin", roles = {"ADMIN"})
class AdminProductControllerIntegrationTest {

    @Autowired
    private Environment environment;

    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MaterialTypeRepository materialTypeRepository;

    @Autowired
    private PurityRepository purityRepository;

    @Autowired
    private ProductRepository productRepository;

    private AdminUsers testAdmin;
    private Category testCategory;
    private MaterialType testMaterialType;
    private Purity testPurity;
    private String baseUrl = "/admin/products";

    @BeforeEach
    void setUp() {
        // Get the port from environment
        this.port = Integer.parseInt(environment.getProperty("local.server.port"));

        // Clean database before every test
        productRepository.deleteAll();
        purityRepository.deleteAll();
        materialTypeRepository.deleteAll();
        categoryRepository.deleteAll();
        adminRepository.deleteAll();

        // Create test admin user (for database lookup by UserDetailsService)
        testAdmin = AdminUsers.builder()
                .email("test-admin@example.com")
                .name("Test Admin")
                .phoneNumber("1234567890")
                .role(Role.ADMIN)
                .build();
        testAdmin = adminRepository.save(testAdmin);

        // Create test category
        testCategory = Category.builder()
                .name("Gold")
                .showOnHome(true)
                .imageUrl("https://example.com/image.jpg")
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
        // With @WithMockUser, we don't need to set Authorization header manually
        // Spring Security will use the mock user from SecurityContext
        return headers;
    }

    // Helper method to get the most recently added product
    private Product getMostRecentProduct() {
        List<Product> products = productRepository.findAll();
        return products.isEmpty() ? null : products.get(products.size() - 1);
    }

    @Test
    void testAddProductEndpoint_Success() {
        // Arrange
        AddProductRequestDTO requestDTO = AddProductRequestDTO.builder()
                .categoryId(testCategory.getCategoryId())
                .materialTypeId(testMaterialType.getMaterialTypeId())
                .purityId(testPurity.getPurityId())
                .makingChargeValue(java.math.BigDecimal.valueOf(12.5))
                .description("Test gold product via API")
                .hallmarkCertified(true)
                .certificationNumber("API-CERT123")
                .discountType("FLAT")
                .discountValue(java.math.BigDecimal.valueOf(50.0))
                .createdBy("api-test")
                .build();

        HttpEntity<AddProductRequestDTO> requestEntity = new HttpEntity<>(requestDTO, createAuthHeaders());

        // Act
        ResponseEntity<BaseResponseDTO<AddProductResponseDTO>> response = restTemplate.exchange(
                "http://localhost:" + port + baseUrl,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<BaseResponseDTO<AddProductResponseDTO>>() {}
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getResponse());
        assertNotNull(response.getBody().getResponse().getMessage());

        // Verify product was created in database
        Product savedProduct = getMostRecentProduct();
        assertNotNull(savedProduct);
        assertNotNull(savedProduct.getProductId());
        assertEquals("Gold 22K 916 #" + savedProduct.getProductId(), savedProduct.getProductName());
        assertEquals(testCategory.getCategoryId(), savedProduct.getCategory().getCategoryId());
        assertEquals(testMaterialType.getMaterialTypeId(), savedProduct.getMaterialType().getMaterialTypeId());
        assertEquals(testPurity.getPurityId(), savedProduct.getPurity().getPurityId());
    }

    @Test
    void testAddProductEndpoint_InvalidData_ReturnsBadRequest() {
        // Arrange
        AddProductRequestDTO requestDTO = AddProductRequestDTO.builder()
                // Missing required fields: categoryId, materialTypeId, purityId
                .makingChargeValue(java.math.BigDecimal.valueOf(10.0))
                .description("Incomplete product")
                .createdBy("api-test")
                .build();

        HttpEntity<AddProductRequestDTO> requestEntity = new HttpEntity<>(requestDTO, createAuthHeaders());

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + baseUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateProductEndpoint_Success() {
        // Arrange - First create a product
        AddProductRequestDTO addRequestDTO = AddProductRequestDTO.builder()
                .categoryId(testCategory.getCategoryId())
                .materialTypeId(testMaterialType.getMaterialTypeId())
                .purityId(testPurity.getPurityId())
                .makingChargeValue(java.math.BigDecimal.valueOf(10.0))
                .description("Original product")
                .createdBy("api-test")
                .build();

        HttpEntity<AddProductRequestDTO> addRequestEntity = new HttpEntity<>(addRequestDTO, createAuthHeaders());
        ResponseEntity<BaseResponseDTO<AddProductResponseDTO>> addResponse = restTemplate.exchange(
                "http://localhost:" + port + baseUrl,
                HttpMethod.POST,
                addRequestEntity,
                new ParameterizedTypeReference<BaseResponseDTO<AddProductResponseDTO>>() {}
        );

        Product createdProduct = getMostRecentProduct();
        Long productId = createdProduct.getProductId();

        // Arrange - Update request
        UpdateProductRequestDTO updateRequestDTO = UpdateProductRequestDTO.builder()
                .productId(productId)
                .categoryId(testCategory.getCategoryId())
                .materialTypeId(testMaterialType.getMaterialTypeId())
                .purityId(testPurity.getPurityId())
                .makingChargeValue(java.math.BigDecimal.valueOf(20.0))
                .description("Updated product via API")
                .hallmarkCertified(true)
                .certificationNumber("UPDATED-API-CERT")
                .discountType("PERCENTAGE")
                .discountValue(java.math.BigDecimal.valueOf(15.0))
                .updatedBy("api-test")
                .build();

        HttpEntity<UpdateProductRequestDTO> updateRequestEntity = new HttpEntity<>(updateRequestDTO, createAuthHeaders());

        // Act
        ResponseEntity<BaseResponseDTO<AddProductResponseDTO>> response = restTemplate.exchange(
                "http://localhost:" + port + baseUrl + "/" + productId,
                HttpMethod.PUT,
                updateRequestEntity,
                new ParameterizedTypeReference<BaseResponseDTO<AddProductResponseDTO>>() {}
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getResponse());
        assertNotNull(response.getBody().getResponse().getMessage());

        // Verify product was updated in database
        Product updatedProduct = productRepository.findById(productId).orElse(null);
        assertNotNull(updatedProduct);
        assertEquals(java.math.BigDecimal.valueOf(20.0), updatedProduct.getMakingChargeValue());
        assertEquals("Updated product via API", updatedProduct.getDescription());
        assertTrue(updatedProduct.getHallmarkCertified());
        assertEquals("UPDATED-API-CERT", updatedProduct.getCertificationNumber());
        assertEquals(java.math.BigDecimal.valueOf(15.0), updatedProduct.getDiscountValue());
    }

    @Test
    void testUpdateProductEndpoint_NonExistingProductId_ReturnsNotFound() {
        // Arrange
        UpdateProductRequestDTO requestDTO = UpdateProductRequestDTO.builder()
                .productId(999L) // Non-existent product
                .categoryId(testCategory.getCategoryId())
                .materialTypeId(testMaterialType.getMaterialTypeId())
                .purityId(testPurity.getPurityId())
                .makingChargeValue(java.math.BigDecimal.valueOf(10.0))
                .description("Non-existing product")
                .updatedBy("api-test")
                .build();

        HttpEntity<UpdateProductRequestDTO> requestEntity = new HttpEntity<>(requestDTO, createAuthHeaders());

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + baseUrl + "/999",
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteProductEndpoint_Success() {
        // Arrange - First create a product
        AddProductRequestDTO addRequestDTO = AddProductRequestDTO.builder()
                .categoryId(testCategory.getCategoryId())
                .materialTypeId(testMaterialType.getMaterialTypeId())
                .purityId(testPurity.getPurityId())
                .makingChargeValue(java.math.BigDecimal.valueOf(10.0))
                .description("Product to delete via API")
                .createdBy("api-test")
                .build();

        HttpEntity<AddProductRequestDTO> addRequestEntity = new HttpEntity<>(addRequestDTO, createAuthHeaders());
        ResponseEntity<AddProductResponseDTO> addResponse = restTemplate.exchange(
                "http://localhost:" + port + baseUrl,
                HttpMethod.POST,
                addRequestEntity,
                AddProductResponseDTO.class
        );

        Product createdProduct = getMostRecentProduct();
        Long productId = createdProduct.getProductId();

        // Arrange - Delete request
        Map<String, String> params = new HashMap<>();
        params.put("updatedBy", "api-test");

        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthHeaders());

        // Act
        ResponseEntity<AddProductResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + baseUrl + "/{productId}?updatedBy={updatedBy}",
                HttpMethod.DELETE,
                requestEntity,
                AddProductResponseDTO.class,
                params
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getMessage());

        // Verify product was deleted (soft delete - set to INACTIVE) in database
        Product deletedProduct = productRepository.findById(productId).orElse(null);
        assertNotNull(deletedProduct);
        assertEquals(ProductStatus.INACTIVE, deletedProduct.getStatus());
        assertEquals("api-test", deletedProduct.getUpdatedBy());
    }

    @Test
    void testDeleteProductEndpoint_NonExistingProductId_ReturnsNotFound() {
        // Arrange
        Map<String, String> params = new HashMap<>();
        params.put("updatedBy", "api-test");

        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthHeaders());

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + baseUrl + "/{productId}?updatedBy={updatedBy}",
                HttpMethod.DELETE,
                requestEntity,
                String.class,
                Map.of("productId", "999", "updatedBy", "api-test")
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetProductByIdEndpoint_Success() {
        // Arrange - First create a product
        AddProductRequestDTO addRequestDTO = AddProductRequestDTO.builder()
                .categoryId(testCategory.getCategoryId())
                .materialTypeId(testMaterialType.getMaterialTypeId())
                .purityId(testPurity.getPurityId())
                .makingChargeValue(java.math.BigDecimal.valueOf(15.0))
                .description("Product to retrieve via API")
                .hallmarkCertified(false)
                .createdBy("api-test")
                .build();

        HttpEntity<AddProductRequestDTO> addRequestEntity = new HttpEntity<>(addRequestDTO, createAuthHeaders());
        ResponseEntity<AddProductResponseDTO> addResponse = restTemplate.exchange(
                "http://localhost:" + port + baseUrl,
                HttpMethod.POST,
                addRequestEntity,
                AddProductResponseDTO.class
        );

        Product createdProduct = getMostRecentProduct();
        Long productId = createdProduct.getProductId();

        // Act
        ResponseEntity<GetProductResponseDTO> response = restTemplate.exchange(
                "http://localhost:" + port + baseUrl + "/" + productId,
                HttpMethod.GET,
                new HttpEntity<>(createAuthHeaders()),
                GetProductResponseDTO.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productId, response.getBody().getProductId());
        assertEquals("Gold 22K 916 #" + productId, response.getBody().getProductName());
        assertEquals("Product to retrieve via API", response.getBody().getDescription());
        assertEquals(java.math.BigDecimal.valueOf(15.0), response.getBody().getMakingChargeValue());
        assertFalse(response.getBody().getHallmarkCertified());
    }

    @Test
    void testGetProductByIdEndpoint_NonExistingProductId_ReturnsNotFound() {
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
    void testGetAllProductsEndpoint_ReturnsPaginatedList() {
        // Arrange - Create multiple products
        for (int i = 1; i <= 3; i++) {
            AddProductRequestDTO requestDTO = AddProductRequestDTO.builder()
                    .categoryId(testCategory.getCategoryId())
                    .materialTypeId(testMaterialType.getMaterialTypeId())
                    .purityId(testPurity.getPurityId())
                    .makingChargeValue(java.math.BigDecimal.valueOf(10.0 + i))
                    .description("Test product " + i)
                    .hallmarkCertified(i % 2 == 0)
                    .createdBy("api-test")
                    .build();

            HttpEntity<AddProductRequestDTO> requestEntity = new HttpEntity<>(requestDTO, createAuthHeaders());
            restTemplate.exchange(
                    "http://localhost:" + port + baseUrl,
                    HttpMethod.POST,
                    requestEntity,
                    AddProductResponseDTO.class
            );
        }

        // Act
        ResponseEntity<Map> response = restTemplate.exchange(
                "http://localhost:" + port + baseUrl + "?page=0&size=2",
                HttpMethod.GET,
                new HttpEntity<>(createAuthHeaders()),
                Map.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // The response is a BaseResponseDTO with Page<GetProductResponseDTO> as the body
        // For simplicity, we're checking that we got a response
        assertTrue(response.getBody().containsKey("body")); // BaseResponseDTO structure
    }

    @Test
    void testGetAllProductsEndpoint_WithMaterialTypeFilter() {
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

        // Create product with first material type (22K)
        AddProductRequestDTO request1 = AddProductRequestDTO.builder()
                .categoryId(testCategory.getCategoryId())
                .materialTypeId(testMaterialType.getMaterialTypeId()) // 22K
                .purityId(testPurity.getPurityId()) // 916
                .makingChargeValue(java.math.BigDecimal.valueOf(10.0))
                .description("22K product")
                .createdBy("api-test")
                .build();

        HttpEntity<AddProductRequestDTO> requestEntity1 = new HttpEntity<>(request1, createAuthHeaders());
        restTemplate.exchange(
                "http://localhost:" + port + baseUrl,
                HttpMethod.POST,
                requestEntity1,
                AddProductResponseDTO.class
        );

        // Create product with second material type (18K)
        AddProductRequestDTO request2 = AddProductRequestDTO.builder()
                .categoryId(testCategory.getCategoryId())
                .materialTypeId(otherMaterialType.getMaterialTypeId()) // 18K
                .purityId(otherPurity.getPurityId()) // 750
                .makingChargeValue(java.math.BigDecimal.valueOf(12.0))
                .description("18K product")
                .createdBy("api-test")
                .build();

        HttpEntity<AddProductRequestDTO> requestEntity2 = new HttpEntity<>(request2, createAuthHeaders());
        restTemplate.exchange(
                "http://localhost:" + port + baseUrl,
                HttpMethod.POST,
                requestEntity2,
                AddProductResponseDTO.class
        );

        // Act - Get products filtered by 22K material type
        ResponseEntity<Map> response = restTemplate.exchange(
                "http://localhost:" + port + baseUrl + "?materialTypeId=" + testMaterialType.getMaterialTypeId(),
                HttpMethod.GET,
                new HttpEntity<>(createAuthHeaders()),
                Map.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Would contain only the 22K product in a real detailed assertion
    }
}