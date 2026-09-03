package com.shyam.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.service.AdminService;
import com.shyam.service.CategoryService;
import com.shyam.service.Imp.CloudinaryService;
import com.shyam.service.MaterialTypeService;
import com.shyam.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auth/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "Admin management endpoints")
public class AdminController {

  private final AdminService adminService;
  private final ProductService productService;
  private final CategoryService categoryService;
  private final CloudinaryService cloudinaryService;
  private final MaterialTypeService materialTypeService;

  @Operation(summary = "Initiate admin login", description = "Step 1: Send OTP to admin email")
  @PostMapping("/initiateLogin")
  public BaseResponseDTO<LogInResponseDTO> initiateLogin(
      @RequestBody AdminLogInRequestDTO adminLogInRequestDTO) {

    log.info("Received request to initiate admin login for: {}", adminLogInRequestDTO.getEmail());

    var response = adminService.initiateLogin(adminLogInRequestDTO.getEmail());

    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Verify admin login OTP",
      description = "Step 2: Verify OTP and complete login")
  @PostMapping("/verifyLoginOtp")
  public ResponseEntity<BaseResponseDTO<VerifyAdminResponseDTO>> verifyLoginOtp(
      @RequestBody VerifyAdminRequestDTO verifyAdminRequestDTO) {
    log.info(
        "Received request to verify admin login OTP for: {}", verifyAdminRequestDTO.getEmail());
    ResponseEntity<BaseResponseDTO<VerifyAdminResponseDTO>> response =
        adminService.verifyLoginOtp(
            verifyAdminRequestDTO.getEmail(), verifyAdminRequestDTO.getOtp());
    return ResponseEntity.status(response.getStatusCode())
        .headers(response.getHeaders())
        .body(response.getBody());
  }

  @Operation(summary = "Logout a admin user", description = "Logout a Admin User.")
  @PostMapping("/logout")
  public ResponseEntity<BaseResponseDTO<AdminLogoutResponseDTO>> logout(
      @RequestHeader("Authorization") String authorization,
      @CookieValue(value = "refreshToken", required = false) String refreshToken) {
    log.info("Received request for logout");
    var accessToken = authorization.replace("Bearer ", "");

    AdminLogoutResponseDTO response = adminService.logout(accessToken, refreshToken);

    ResponseCookie deleteCookie =
        ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .path("/")
            .maxAge(0)
            .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
        .body(new BaseResponseDTO<>(response, null));
  }

  @Operation(summary = "Edit admin", description = "Edit admin details.")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  @PostMapping(value = "/editAdmin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public BaseResponseDTO<EditAdminResponseDTO> edit(
      @RequestPart("admin") String adminJson,
      @RequestPart(value = "image", required = false) MultipartFile image)
      throws JsonProcessingException {

    log.info("Received request for edit");

    ObjectMapper mapper = new ObjectMapper();

    EditAdminRequestDTO editAdminRequestDTO =
        mapper.readValue(adminJson, EditAdminRequestDTO.class);

    if (image != null && !image.isEmpty()) {
      log.info("New profile image received. Uploading to Cloudinary...");

      String imageUrl = cloudinaryService.upload(image);

      editAdminRequestDTO.setImageUrl(imageUrl);

      log.info("New profile image uploaded successfully: {}", imageUrl);

    } else {

      log.info("No new profile image provided. Keeping existing image.");
    }
    var response = adminService.edit(editAdminRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Register new Admin", description = "new admin register.")
  @PostMapping("/registerAdmin")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
  public BaseResponseDTO<RegisterResponseDTO> registerAdmin(
      @RequestBody RegisterRequestDTO registerRequestDTO) {
    log.info("Received request for register admin for");
    var response = adminService.registerAdmin(registerRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Get Admin", description = "Get Admin.")
  @PostMapping("/getAdminByEmail")
  @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
  public BaseResponseDTO<GetAdminResponseDTO> getAllAdmin(
      @RequestBody GetAdminRequestDTO getAdminRequestDTO) {
    log.info("Received request for getting admin ");
    var response = adminService.getAdmin(getAdminRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Get All Admin", description = "Get All Admin.")
  @PostMapping("/getAllAdmin")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public BaseResponseDTO<GetAdminListResponseDTO> getAllAdmin() {

    log.info("Received request for getting all admin");
    var response = adminService.getAllAdmin();
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "delete Admin", description = "Delete Admin.")
  @PostMapping("/deleteAdmin")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public BaseResponseDTO<DeleteAdminResponseDTO> deleteAdmin(
      @RequestBody DeleteAdminRequestDTO deleteAdmin) {
    log.info("Received request for delete admin");
    var response = adminService.deleteAdmin(deleteAdmin);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(
      summary = "Get all products",
      description = "Retrieve a paginated list of all products.")
  @PostMapping("/getAllProduct")
  public Page<BaseResponseDTO<GetAllProductsResponseDTO>> getAllProducts(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    log.info("Received request for getting all products");
    return productService.getAllProducts(page, size);
  }

  @Operation(summary = "Add product", description = "Add a new product with image.")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  @PostMapping(value = "/addProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public BaseResponseDTO<ProductAddResponseDTO> addProduct(
      @RequestParam("data") String data, @RequestParam("image") MultipartFile image)
      throws Exception {

    ObjectMapper mapper = new ObjectMapper();
    ProductAddRequestDTO dto = mapper.readValue(data, ProductAddRequestDTO.class);

    return new BaseResponseDTO<>(productService.addProduct(dto, image), null);
  }

  @Operation(summary = "Update product")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  @PutMapping("/updateProduct")
  public BaseResponseDTO<UpdateResponseDTO> updateProduct(
      @Valid @RequestBody UpdateRequestDTO dto) {
    return new BaseResponseDTO<>(productService.updateProduct(dto), null);
  }

  @Operation(summary = "Get product by product id")
  @GetMapping("/getProductById/{productId}")
  public BaseResponseDTO<AllProductResponseDTO> getProductById(@PathVariable Long productId) {
    return new BaseResponseDTO<>(productService.getProductById(productId), null);
  }

  @Operation(summary = "Delete product")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  @DeleteMapping("/deleteProduct")
  public BaseResponseDTO<DeleteResponseDTO> deleteProduct(
      @Valid @RequestBody DeleteProductRequestDTO dto) {
    return new BaseResponseDTO<>(productService.deleteProduct(dto), null);
  }

  @Operation(
      summary = "Get all categories",
      description = "Retrieve a paginated list of all categories.")
  @PostMapping("/getAllCategory")
  public Page<BaseResponseDTO<GetCategoriesResponseDTO>> getAllCategories(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    log.info("Received request for getting all category");
    return categoryService.getAllCategories(page, size);
  }

  @Operation(summary = "Add category", description = "Add a new category.")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  @PostMapping(value = "/addCategory", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public BaseResponseDTO<AddCategoryResponseDTO> addCategories(
      @RequestParam("image") MultipartFile image,
      @RequestParam("data") String addCategoryRequestDTOJson)
      throws JsonProcessingException {
    log.info("Received request for adding category");
    if (image == null || image.isEmpty()) {
      throw new RuntimeException("Category image is empty");
    }

    String imageUrl = cloudinaryService.upload(image);

    ObjectMapper mapper = new ObjectMapper();
    AddCategoryRequestDTO addCategoryRequestDTO =
        mapper.readValue(addCategoryRequestDTOJson, AddCategoryRequestDTO.class);
    addCategoryRequestDTO.setImageUrl(imageUrl);

    var response = categoryService.addCategories(addCategoryRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Update category", description = "Update an existing category.")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  @PutMapping(value = "/updateCategory", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public BaseResponseDTO<UpdateCategoryResponseDTO> updateCategories(
      @RequestParam(value = "image", required = false) MultipartFile image,
      @RequestParam("data") String updateCategoryRequestDTOJson)
      throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();

    UpdateCategoryRequestDTO updateCategoryRequestDTO =
        mapper.readValue(updateCategoryRequestDTOJson, UpdateCategoryRequestDTO.class);

    if (image != null && !image.isEmpty()) {

      log.info("📸 New image received for category update");

      String imageUrl = cloudinaryService.upload(image);

      log.info("☁️ Cloudinary image URL: {}", imageUrl);

      updateCategoryRequestDTO.setImageUrl(imageUrl);
    }

    // =====================================================
    // UPDATE CATEGORY
    // =====================================================

    var response = categoryService.updateCategoryRequestDTO(updateCategoryRequestDTO);

    log.info("✅ CATEGORY UPDATED SUCCESSFULLY");
    log.info("========================================");

    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Delete category", description = "Delete a category by its ID.")
  @DeleteMapping("/deleteCategory")
  public BaseResponseDTO<UpdateCategoryResponseDTO> deleteCategory(
      @RequestBody GetCategoryByIdRequestDTO getCategoryByIdRequestDTO) {
    log.info("Received request for deleting category");
    var response = categoryService.deleteCategory(getCategoryByIdRequestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Upload Excel", description = "Upload an Excel file to add categories.")
  @PostMapping("/uploadExcel")
  public ResponseEntity<?> uploadExcel(
      @RequestParam("file") MultipartFile file, @RequestParam("createdBy") String createdBy) {
    log.info("Received excel request for adding category");
    return categoryService.uploadExcel(file, createdBy);
  }

  @Operation(summary = "Offer Section", description = "Adding offer photo.")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  @PostMapping(value = "/addOfferPhoto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public BaseResponseDTO<EditPhotoResponseDTO> offerUpdate(
      @RequestParam("position") Integer position,
      @RequestParam("image") MultipartFile image,
      @RequestParam("isAvailable") Boolean isAvailable) {
    log.info("Received request for offer update");
    if (image == null || image.isEmpty()) {
      throw new RuntimeException("Offer image is empty");
    }

    String imageUrl = cloudinaryService.upload(image);

    EditPhotoRequestDTO request =
        EditPhotoRequestDTO.builder()
            .imgUrl(imageUrl)
            .position(position)
            .isAvailable(isAvailable)
            .build();

    var response = adminService.offerUpdate(request);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Add material type", description = "Add a new material type.")
  @PostMapping("/addMaterialType")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public BaseResponseDTO<AddMaterialTypeResponseDTO> addMaterialType(
      @Valid @RequestBody AddMaterialTypeRequestDTO requestDTO) {
    log.info("Received request to add material type: {}", requestDTO.getName());
    var response = materialTypeService.addMaterialType(requestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Update material type", description = "Update an existing material type.")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  @PutMapping("/updateMaterialType")
  public BaseResponseDTO<AddMaterialTypeResponseDTO> updateMaterialType(
      @Valid @RequestBody UpdateMaterialTypeRequestDTO requestDTO) {
    log.info("Received request to update material type id: {}", requestDTO.getMaterialTypeId());
    var response = materialTypeService.updateMaterialType(requestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Delete material type", description = "Delete a material type by its ID.")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  @DeleteMapping("/deleteMaterialType")
  public BaseResponseDTO<AddMaterialTypeResponseDTO> deleteMaterialType(
      @Valid @RequestBody GetMaterialTypeByIdRequestDTO requestDTO) {
    log.info("Received request to delete material type id: {}", requestDTO.getMaterialTypeId());
    var response = materialTypeService.deleteMaterialType(requestDTO);
    return new BaseResponseDTO<>(response, null);
  }

  @Operation(summary = "Get all material types", description = "Get list of all material types.")
  @PostMapping("/getAllMaterialTypes")
  @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
  public BaseResponseDTO<List<GetMaterialTypeResponseDTO>> getAllMaterialTypes() {
    log.info("Received request to get all material types");
    var response = materialTypeService.getAllMaterialTypes();
    return new BaseResponseDTO<>(response, null);
  }
}
