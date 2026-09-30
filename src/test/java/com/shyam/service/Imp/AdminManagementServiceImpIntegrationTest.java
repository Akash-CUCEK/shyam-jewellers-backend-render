package com.shyam.service.Imp;

import com.shyam.ShyamApplication;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.entity.AdminUsers;
import com.shyam.common.constants.Role;
import com.shyam.repository.AdminRepository;
import com.shyam.service.AdminManagementService;
import com.shyam.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Integration test for AdminManagementServiceImpl using H2 database.
 * This test verifies the complete flow from service to repository layer.
 */
@SpringBootTest(classes = ShyamApplication.class)
@ActiveProfiles("test")
class AdminManagementServiceImpIntegrationTest {

    @Autowired
    private AdminManagementService adminManagementService;

    @Autowired
    private AdminRepository adminRepository;

    @MockBean
    private NotificationService notificationService;

    private AdminUsers testAdmin;

    @BeforeEach
    void setUp() {
        // Clean database before every test
        adminRepository.deleteAll();

        // Create test admin
        testAdmin = AdminUsers.builder()
                .email("test@example.com")
                .name("Test Admin")
                .phoneNumber("1234567890")
                .role(Role.ADMIN)
                .build();

        testAdmin = adminRepository.save(testAdmin);
    }

    @Test
    void testRegisterAdmin_Success() {
        // Arrange
        RegisterRequestDTO requestDTO = RegisterRequestDTO.builder()
                .email("newadmin@example.com")
                .name("New Admin")
                .phoneNumber("0987654321")
                .build();

        // Act
        RegisterResponseDTO response = adminManagementService.registerAdmin(requestDTO);

        // Assert
        assertNotNull(response);

        AdminUsers savedAdmin = adminRepository.findByEmail("newadmin@example.com")
                .orElse(null);

        assertNotNull(savedAdmin);
        assertEquals("newadmin@example.com", savedAdmin.getEmail());
        assertEquals("New Admin", savedAdmin.getName());
        assertEquals("0987654321", savedAdmin.getPhoneNumber());
        assertEquals(Role.ADMIN, savedAdmin.getRole());

        // Register should trigger notification
        verify(notificationService).process(any());
    }

    @Test
    void testRegisterAdmin_EmailAlreadyExists_ThrowsException() {
        // Arrange
        RegisterRequestDTO requestDTO = RegisterRequestDTO.builder()
                .email("test@example.com")
                .name("Duplicate Admin")
                .phoneNumber("1112223333")
                .build();

        // Act + Assert
        assertThrows(Exception.class, () -> adminManagementService.registerAdmin(requestDTO));

        // Original admin should be the only admin
        assertEquals(1, adminRepository.count());
    }

    @Test
    void testGetAllAdmin_ReturnsAdmins() {
        // Act
        GetAdminListResponseDTO response = adminManagementService.getAllAdmin();

        // Assert
        assertNotNull(response);
        assertNotNull(response.getGetAllAdminResponseDTOList());
        assertEquals(1, response.getGetAllAdminResponseDTOList().size());
    }

    @Test
    void testGetAdminByEmail_ReturnsAdmin() {
        // Arrange
        GetAdminRequestDTO requestDTO = GetAdminRequestDTO.builder()
                .email("test@example.com")
                .build();

        // Act
        GetAdminResponseDTO response = adminManagementService.getAdmin(requestDTO);

        // Assert
        assertNotNull(response);

        /*
         * GetAdminResponseDTO contains only:
         * name
         * phoneNumber
         * imageUrl
         *
         * It does NOT contain:
         * id
         * email
         */

        assertEquals(testAdmin.getName(), response.getName());
        assertEquals(testAdmin.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(testAdmin.getImageUrl(), response.getImageUrl());
    }

    @Test
    void testEditAdmin_Success() {
        // Arrange
        EditAdminRequestDTO requestDTO = EditAdminRequestDTO.builder()
                .email("test@example.com")
                .name("Updated Admin Name")
                .phoneNumber("5556667777")
                .imageUrl("https://example.com/image.jpg")
                .build();

        // Act
        EditAdminResponseDTO response = adminManagementService.edit(requestDTO);

        // Assert
        assertNotNull(response);

        AdminUsers updatedAdmin = adminRepository.findById(testAdmin.getId())
                .orElse(null);

        assertNotNull(updatedAdmin);
        assertEquals("Updated Admin Name", updatedAdmin.getName());
        assertEquals("5556667777", updatedAdmin.getPhoneNumber());
        assertEquals("https://example.com/image.jpg", updatedAdmin.getImageUrl());

        // Edit should trigger notification
        verify(notificationService).process(any());
    }

    @Test
    void testDeleteAdmin_Success() {

        // Arrange
        DeleteAdminRequestDTO requestDTO =
                DeleteAdminRequestDTO.builder()
                        .email("test@example.com")
                        .build();

        // Act
        DeleteAdminResponseDTO response =
                adminManagementService.deleteAdmin(requestDTO);

        // Assert
        assertNotNull(response);

        // Verify admin was deleted from database
        assertEquals(
                0,
                adminRepository.count()
        );

        assertFalse(
                adminRepository
                        .findByEmail("test@example.com")
                        .isPresent()
        );
    }
}