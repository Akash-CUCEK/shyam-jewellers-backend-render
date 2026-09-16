package com.shyam.service.Imp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.shyam.common.exception.domain.SYMErrorType;
import com.shyam.common.exception.domain.SYMException;
import com.shyam.common.util.MessageSourceUtil;
import com.shyam.dto.PriceBreakdownDTO;
import com.shyam.entity.MaterialType;
import com.shyam.entity.MetalRate;
import com.shyam.entity.Product;
import com.shyam.entity.ProductVariant;
import com.shyam.entity.Purity;
import com.shyam.repository.MaterialTypeRepository;
import com.shyam.repository.MetalRateRepository;
import com.shyam.repository.ProductVariantRepository;
import com.shyam.repository.PurityRepository;
import com.shyam.service.PricingService;

@ExtendWith(MockitoExtension.class)
class PricingServiceImplTest {

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private MetalRateRepository metalRateRepository;

    @Mock
    private PurityRepository purityRepository;

    @Mock
    private MaterialTypeRepository materialTypeRepository;

    @Mock
    private MessageSourceUtil messageSourceUtil;

    @InjectMocks
    private PricingServiceImpl pricingServiceImpl;

    private Product testProduct;
    private ProductVariant testVariant;
    private MaterialType testMaterialType;
    private Purity testPurity;
    private MetalRate testMetalRate;

    @BeforeEach
    void setUp() {
        // Setup material type
        testMaterialType = MaterialType.builder()
                .materialTypeId(1L)
                .name("Gold")
                .makingChargeType("PERCENTAGE")
                .makingChargeValue(new BigDecimal("10.0"))
                .build();

        // Setup purity
        testPurity = Purity.builder()
                .purityId(1L)
                .materialType(testMaterialType)
                .purityName("22K")
                .purityFactor(new BigDecimal("0.916")) // 91.6%
                .build();

        // Setup metal rate
        testMetalRate = MetalRate.builder()
                .id(1L)
                .materialType(testMaterialType)
                .ratePerGram(new BigDecimal("5000.00")) // ₹5000 per gram
                .fetchedAt(LocalDateTime.now()) // Set fetched time to now
                .build();

        // Setup product
        testProduct = Product.builder()
                .productId(1L)
                .productName("Test Gold Product")
                .materialType(testMaterialType)
                .purity(testPurity)
                .makingChargeType("PERCENTAGE")
                .makingChargeValue(new BigDecimal("10.0")) // 10%
                .build();

        // Setup product variant
        testVariant = ProductVariant.builder()
                .variantId(1L)
                .product(testProduct)
                .weight(new BigDecimal("10.0")) // 10 grams
                .build();
    }

    @Test
    void testCalculatePrice_Success_PercentageMakingCharge() {
        // Arrange
        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(materialTypeRepository.findById(testMaterialType.getMaterialTypeId())).thenReturn(Optional.of(testMaterialType));
        when(purityRepository.findById(testPurity.getPurityId())).thenReturn(Optional.of(testPurity));
        when(metalRateRepository.findLatestByMaterialType(testMaterialType)).thenReturn(Optional.of(testMetalRate));

        // Act
        PriceBreakdownDTO result = pricingServiceImpl.calculatePrice(testVariant);

        // Assert
        assertNotNull(result);

        // Calculate expected values:
        // Weight: 10g
        // Metal Rate: ₹5000/g
        // Purity Factor: 0.916 (22K)
        // Metal Value = 10 * 5000 * 0.916 = ₹45,800
        // Making Charge (10%) = 45800 * 0.10 = ₹4,580
        // Subtotal = 45800 + 4580 = ₹50,380
        // GST (3%) = 50380 * 0.03 = ₹1,511.40
        // Final Price = 50380 + 1511.40 = ₹51,891.40

        assertEquals(new BigDecimal("45800.00"), result.getMetalValue());
        assertEquals(new BigDecimal("4580.00"), result.getMakingCharge());
        assertEquals(new BigDecimal("1511.40"), result.getGst());
        assertEquals(new BigDecimal("51891.40"), result.getFinalPrice());
        assertEquals(new BigDecimal("5000.00"), result.getRatePerGramUsed());
        assertEquals(new BigDecimal("0.916"), result.getPurityFactorUsed());
    }

    @Test
    void testCalculatePrice_Success_FixedMakingCharge() {
        // Arrange
        testProduct.setMakingChargeType("FIXED");
        testProduct.setMakingChargeValue(new BigDecimal("500.00")) // ₹500 fixed

        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(materialTypeRepository.findById(testMaterialType.getMaterialTypeId())).thenReturn(Optional.of(testMaterialType));
        when(purityRepository.findById(testPurity.getPurityId())).thenReturn(Optional.of(testPurity));
        when(metalRateRepository.findLatestByMaterialType(testMaterialType)).thenReturn(Optional.of(testMetalRate));

        // Act
        PriceBreakdownDTO result = pricingServiceImpl.calculatePrice(testVariant);

        // Assert
        assertNotNull(result);

        // Calculate expected values:
        // Metal Value = 10 * 5000 * 0.916 = ₹45,800
        // Making Charge (Fixed) = ₹500
        // Subtotal = 45800 + 500 = ₹46,300
        // GST (3%) = 46300 * 0.03 = ₹1,389.00
        // Final Price = 46300 + 1389 = ₹47,689.00

        assertEquals(new BigDecimal("45800.00"), result.getMetalValue());
        assertEquals(new BigDecimal("500.00"), result.getMakingCharge());
        assertEquals(new BigDecimal("1389.00"), result.getGst());
        assertEquals(new BigDecimal("47689.00"), result.getFinalPrice());
        assertEquals(new BigDecimal("5000.00"), result.getRatePerGramUsed());
        assertEquals(new BigDecimal("0.916"), result.getPurityFactorUsed());
    }

    @Test
    void testCalculatePrice_Success_PerGramMakingCharge() {
        // Arrange
        testProduct.setMakingChargeType("PER_GRAM");
        testProduct.setMakingChargeValue(new BigDecimal("20.00")) // ₹20 per gram

        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(materialTypeRepository.findById(testMaterialType.getMaterialTypeId())).thenReturn(Optional.of(testMaterialType));
        when(purityRepository.findById(testPurity.getPurityId())).thenReturn(Optional.of(testPurity));
        when(metalRateRepository.findLatestByMaterialType(testMaterialType)).thenReturn(Optional.of(testMetalRate));

        // Act
        PriceBreakdownDTO result = pricingServiceImpl.calculatePrice(testVariant);

        // Assert
        assertNotNull(result);

        // Calculate expected values:
        // Metal Value = 10 * 5000 * 0.916 = ₹45,800
        // Making Charge (Per Gram) = 10 * 20 = ₹200
        // Subtotal = 45800 + 200 = ₹46,000
        // GST (3%) = 46000 * 0.03 = ₹1,380.00
        // Final Price = 46000 + 1380 = ₹47,380.00

        assertEquals(new BigDecimal("45800.00"), result.getMetalValue());
        assertEquals(new BigDecimal("200.00"), result.getMakingCharge());
        assertEquals(new BigDecimal("1380.00"), result.getGst());
        assertEquals(new BigDecimal("47380.00"), result.getFinalPrice());
        assertEquals(new BigDecimal("5000.00"), result.getRatePerGramUsed());
        assertEquals(new BigDecimal("0.916"), result.getPurityFactorUsed());
    }

    @Test
    void testCalculatePrice_VariantNotFound() {
        // Arrange
        when(productVariantRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        SYMException exception = assertThrows(SYMException.class, () -> {
            pricingServiceImpl.calculatePrice(testVariant);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("RESOURCE_NOT_FOUND", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Product variant not found"));
    }

    @Test
    void testCalculatePrice_NullVariant() {
        // Act & Assert
        SYMException exception = assertThrows(SYMException.class, () -> {
            pricingServiceImpl.calculatePrice(null);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("VARIANT_NULL", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Product variant is null"));
    }

    @Test
    void testCalculatePrice_NullProduct() {
        // Arrange
        ProductVariant variantWithoutProduct = ProductVariant.builder()
                .variantId(1L)
                .weight(new BigDecimal("10.0"))
                .build();

        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(variantWithoutProduct));

        // Act & Assert
        SYMException exception = assertThrows(SYMException.class, () -> {
            pricingServiceImpl.calculatePrice(variantWithoutProduct);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("PRODUCT_NULL", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Product is null for variant"));
    }

    @Test
    void testCalculatePrice_NullMaterialType() {
        // Arrange
        Product productWithoutMaterialType = Product.builder()
                .productId(1L)
                .purity(testPurity)
                .makingChargeType("PERCENTAGE")
                .makingChargeValue(new BigDecimal("10.0"))
                .build();

        ProductVariant variant = ProductVariant.builder()
                .variantId(1L)
                .product(productWithoutMaterialType)
                .weight(new BigDecimal("10.0"))
                .build();

        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(variant));

        // Act & Assert
        SYMException exception = assertThrows(SYMException.class, () -> {
            pricingServiceImpl.calculatePrice(variant);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("MATERIAL_TYPE_NULL", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Material type is null for product"));
    }

    @Test
    void testCalculatePrice_NoMetalRateFound() {
        // Arrange
        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(materialTypeRepository.findById(testMaterialType.getMaterialTypeId())).thenReturn(Optional.of(testMaterialType));
        when(purityRepository.findById(testPurity.getPurityId())).thenReturn(Optional.of(testPurity));
        when(metalRateRepository.findLatestByMaterialType(testMaterialType)).thenReturn(Optional.empty());

        // Act & Assert
        SYMException exception = assertThrows(SYMException.class, () -> {
            pricingServiceImpl.calculatePrice(testVariant);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("METAL_RATE_NOT_FOUND", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Metal rate not available, please try again later"));
    }

    @Test
    void testCalculatePrice_NullWeight() {
        // Arrange
        ProductVariant variantWithNullWeight = ProductVariant.builder()
                .variantId(1L)
                .product(testProduct)
                .weight(null) // Null weight
                .build();

        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(variantWithNullWeight));

        // Act & Assert
        SYMException exception = assertThrows(SYMException.class, () -> {
            pricingServiceImpl.calculatePrice(variantWithNullWeight);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("WEIGHT_NULL", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Weight is null for product variant"));
    }

    @Test
    void testCalculatePrice_NullPurityFactor() {
        // Arrange
        Purity purityWithNullFactor = Purity.builder()
                .purityId(1L)
                .materialType(testMaterialType)
                .purityName("22K")
                .purityFactor(null) // Null purity factor
                .build();

        Product variantWithNullPurity = Product.builder()
                .productId(1L)
                .materialType(testMaterialType)
                .purity(purityWithNullFactor)
                .makingChargeType("PERCENTAGE")
                .makingChargeValue(new BigDecimal("10.0"))
                .build();

        ProductVariant variant = ProductVariant.builder()
                .variantId(1L)
                .product(variantWithNullPurity)
                .weight(new BigDecimal("10.0"))
                .build();

        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(variant));

        // Act & Assert
        SYMException exception = assertThrows(SYMException.class, () -> {
            pricingServiceImpl.calculatePrice(variant);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("PURITY_FACTOR_NULL", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Purity factor is null for product"));
    }

    @Test
    void testCalculatePrice_NullMakingChargeValue() {
        // Arrange
        Product productWithNullMakingCharge = Product.builder()
                .productId(1L)
                .materialType(testMaterialType)
                .purity(testPurity)
                .makingChargeType("PERCENTAGE")
                .makingChargeValue(null) // Null making charge value
                .build();

        ProductVariant variant = ProductVariant.builder()
                .variantId(1L)
                .product(productWithNullMakingCharge)
                .weight(new BigDecimal("10.0"))
                .build();

        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(variant));

        // Act & Assert
        SYMException exception = assertThrows(SYMException.class, () -> {
            pricingServiceImpl.calculatePrice(variant);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("MAKING_CHARGE_VALUE_NULL", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Making charge value is null for product"));
    }

    @Test
    void testCalculatePrice_StaleMetalRateLogsWarning() {
        // Arrange
        MetalRate staleMetalRate = MetalRate.builder()
                .id(1L)
                .materialType(testMaterialType)
                .ratePerGram(new BigDecimal("5000.00"))
                .fetchedAt(LocalDateTime.now().minusHours(25)) // 25 hours old
                .build();

        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(materialTypeRepository.findById(testMaterialType.getMaterialTypeId())).thenReturn(Optional.of(testMaterialType));
        when(purityRepository.findById(testPurity.getPurityId())).thenReturn(Optional.of(testPurity));
        when(metalRateRepository.findLatestByMaterialType(testMaterialType)).thenReturn(Optional.of(staleMetalRate));

        // Act
        PriceBreakdownDTO result = pricingServiceImpl.calculatePrice(testVariant);

        // Assert - should still work but log a warning (we can't easily test logging in unit test)
        assertNotNull(result);
        // Just verify it doesn't throw an exception and returns valid data
        assertEquals(new BigDecimal("45800.00"), result.getMetalValue());
    }
}