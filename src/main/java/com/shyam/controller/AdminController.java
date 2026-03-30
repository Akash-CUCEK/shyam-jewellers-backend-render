package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.*;
import com.shyam.dto.response.*;
import com.shyam.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;
//    private final ProductService productService;
//    private final CategoryService categoryService;

    @Operation(summary = "Login a admin user", description = "Login a Admin User.")
    @PostMapping("/logIn")
    public BaseResponseDTO<AdminLogInResponseDTO> logIn(
            @RequestBody AdminLogInRequestDTO adminLogInRequestDTO
    ){
        logger.info("Received request for login admin");
        var response = adminService.logIn(adminLogInRequestDTO);
        return new BaseResponseDTO<>(response,null);
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<BaseResponseDTO<VerifyAdminResponseDTO>> verify(
            @RequestBody VerifyAdminOtpRequestDTO verifyAdminRequestDTO
    ){
        logger.info("Received request for verify otp");

        ResponseEntity<VerifyAdminResponseDTO> responseEntity =
                adminService.verifyOtp(verifyAdminRequestDTO);

        return ResponseEntity
                .status(responseEntity.getStatusCode())
                .headers(responseEntity.getHeaders())
                .body(new BaseResponseDTO<>(
                        responseEntity.getBody(),
                        null
                ));
    }

    @Operation(summary = "Reset Password", description = "Reset Password")
    @PostMapping("/forgetPassword")
    public BaseResponseDTO<ForgetPasswordResponseDTO> forgetPasswordOtp(
            @RequestBody ForgetPasswordRequestDTO forgetPasswordRequestDTO
    ){
        logger.info("Received request for password reset");
        var response = adminService.forgetPassword(forgetPasswordRequestDTO);
        return new BaseResponseDTO<>(response,null);
    }

    @Operation(summary = "Verify otp for password reset", description = "Verify otp for password reset")
    @PostMapping("/verifyPasswordOtp")
    public BaseResponseDTO<VerifyForgetPasswordResponseDTO> forgetVerifyPassword(
            @RequestBody VerifyAdminRequestDTO verifyAdminRequestDTO
    ){
        logger.info("Received request for verify otp for password reset");
        var response = adminService.forgetVerifyOtp(verifyAdminRequestDTO);
        return new BaseResponseDTO<>(response,null);
    }

    @Operation(summary = "Offer Section", description = "Adding offer photo.")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/addOfferPhoto")
    public BaseResponseDTO<EditPhotoResponseDTO> offerUpdate(
            @RequestBody EditPhotoRequestDTO editPhotoRequestDTO
    ){
        logger.info("Received request for offer update");
        var response = adminService.offerUpdate(editPhotoRequestDTO);
        return new BaseResponseDTO<>(response,null);
    }

    @Operation(summary = "Logout a admin user", description = "Logout a Admin User.")
    @PostMapping("/logout")
    public ResponseEntity<BaseResponseDTO<AdminLogoutResponseDTO>> logout(
            @RequestHeader("Authorization") String authorization,
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        logger.info("Received request for logout");
        String accessToken = authorization.replace("Bearer ", "");

        AdminLogoutResponseDTO response =
                adminService.logout(accessToken, refreshToken);

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
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

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PostMapping("/editAdmin")
    public BaseResponseDTO<EditAdminResponseDTO> edit(
            @RequestBody EditAdminRequestDTO editAdminRequestDTO
    ) {
        logger.info("Received request for edit");
        var response = adminService.edit(editAdminRequestDTO);
        return new BaseResponseDTO<>(response, null);
    }

    @Operation(summary = "Change password", description = "Admin change password.")
    @PostMapping("/changePassword")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public BaseResponseDTO<ChangePasswordResponseDTO> changePassword(
            @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO
    ){
        logger.info("Received request for change password");
        var response = adminService.changePassword(changePasswordRequestDTO);
        return new BaseResponseDTO<>(response,null);
    }

    @Operation(summary = "Register new Admin", description = "new admin register.")
    @PostMapping("/registerAdmin")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public BaseResponseDTO<RegisterResponseDTO> registerAdmin(
            @RequestBody RegisterRequestDTO registerRequestDTO,
            @RequestHeader("Authorization") String authHeader
    ){
        logger.info("Received request for register admin for");
        var response = adminService.registerAdmin(registerRequestDTO);
        return new BaseResponseDTO<>(response,null);
    }

    @Operation(summary = "Get Admin", description = "Get Admin.")
    @PostMapping("/getAdminByEmail")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public BaseResponseDTO<GetAdminResponseDTO> getAllAdmin(
            @RequestBody GetAdminRequestDTO getAdminRequestDTO
    ){
        logger.info("Received request for getting admin ");
        var response = adminService.getAdmin(getAdminRequestDTO);
        return new BaseResponseDTO<>(response,null);
    }

    @Operation(summary = "Get All Admin", description = "Get All Admin.")
    @PostMapping("/getAllAdmin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public BaseResponseDTO<GetAdminListResponseDTO> getAllAdmin(

    ){
        logger.info("Received request for getting all admin");
        var response = adminService.getAllAdmin();
        return new BaseResponseDTO<>(response,null);
    }

    @Operation(summary = "delete Admin", description = "Delete Admin.")
    @PostMapping("/deleteAdmin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public BaseResponseDTO<DeleteAdminResponseDTO> deleteAdmin(
            @RequestBody DeleteAdminRequestDTO deleteAdmin
    ){
        logger.info("Received request for delete admin");
        var response = adminService.deleteAdmin(deleteAdmin);
        return new BaseResponseDTO<>(response,null);
    }

//    @Operation(summary = "Add new product")
//    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
//    @PostMapping("/addProduct")
//    public BaseResponseDTO<ProductAddResponseDTO> addProduct(
//            @Valid @RequestBody ProductAddRequestDTO dto
//    ) {
//        log.info("Received request for adding product");
//        return new BaseResponseDTO<>(
//                productService.addProduct(dto),
//                null
//        );
//    }

//    @Operation(summary = "Update product")
//    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
//    @PutMapping
//    public BaseResponseDTO<UpdateResponseDTO> updateProduct(
//            @Valid @RequestBody UpdateRequestDTO dto
//    ) {
//        return new BaseResponseDTO<>(
//                productService.updateProduct(dto),
//                null
//        );
//    }
//
//
//    @Operation(summary = "Get product by product id")
//    @GetMapping("/getProductById/{productId}")
//    public BaseResponseDTO<AllProductResponseDTO> getProductById(
//            @PathVariable Long productId
//    ) {
//        return new BaseResponseDTO<>(
//                productService.getProductById(productId),
//                null
//        );
//    }
//
//    @Operation(summary = "Delete product")
//    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
//    @DeleteMapping
//    public BaseResponseDTO<DeleteResponseDTO> deleteProduct(
//            @Valid @RequestBody DeleteProductRequestDTO dto
//    ) {
//        return new BaseResponseDTO<>(
//                productService.deleteProduct(dto),
//                null
//        );
//    }
//
//    @PostMapping("/getAllCategory")
//    public BaseResponseDTO<GetCategoryResponseDTO> getAllCategories() {
//        log.info("Received request for getting all category");
//        var response = categoryService.getAllCategories();
//        return new BaseResponseDTO<>(response,null);
//    }
//
//    @PostMapping("/getCategory")
//    public BaseResponseDTO<GetCategoryByIdResponseDTO> getCategory(
//            @RequestBody GetCategoryByIdRequestDTO getCategoryByIdRequestDTO
//    ) {
//        log.info("Received request for get category by Id");
//        var response = categoryService.getCategory(getCategoryByIdRequestDTO);
//        return new BaseResponseDTO<>(response,null);
//    }
//
//    @PostMapping("/addCategory")
//    public BaseResponseDTO<AddCategoryResponseDTO> addCategories(
//            @RequestBody AddCategoryRequestDTO addCategoryRequestDTO
//    ) {
//        log.info("Received request for adding category");
//        var response =  categoryService.addCategories(addCategoryRequestDTO);
//        return new BaseResponseDTO<>(response,null);
//    }
//
//    @PutMapping("/updateCategory")
//    public BaseResponseDTO<UpdateCategoryResponseDTO> updateCategories(
//            @RequestBody AddCategoryRequestDTO updateCategoryRequestDTO
//    ) {
//        log.info("Received request for updating category");
//        var response =  categoryService.updateCategoryRequestDTO(updateCategoryRequestDTO);
//        return new BaseResponseDTO<>(response,null);
//    }
//
//    @DeleteMapping("/deleteCategory")
//    public BaseResponseDTO<UpdateCategoryResponseDTO> deleteCategory(
//            @RequestBody GetCategoryByIdRequestDTO getCategoryByIdRequestDTO
//    ) {
//        log.info("Received request for deleting category");
//        var response =  categoryService.deleteCategory(getCategoryByIdRequestDTO);
//        return new BaseResponseDTO<>(response,null);
//    }
//
//    @PostMapping("/uploadExcel")
//    public ResponseEntity<?> uploadExcel(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("createdBy") String createdBy
//    ) {
//        log.info("Received excel request for adding category");
//        return categoryService.uploadExcel(file,createdBy);
//    }

}
