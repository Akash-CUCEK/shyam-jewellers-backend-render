package com.shyam.service;

import com.shyam.dto.request.*;
import com.shyam.dto.response.*;

/**
 * Service interface for admin management operations. Follows Single Responsibility Principle by
 * handling only admin CRUD operations.
 */
public interface AdminManagementService {

  /**
   * Edits an existing admin's details.
   *
   * @param editAdminRequestDTO the admin details to update
   * @return response indicating the edit operation result
   */
  EditAdminResponseDTO edit(EditAdminRequestDTO editAdminRequestDTO);

  /**
   * Registers a new admin user.
   *
   * @param registerRequestDTO the registration details for the new admin
   * @return response indicating the registration result
   */
  RegisterResponseDTO registerAdmin(RegisterRequestDTO registerRequestDTO);

  /**
   * Retrieves all admins in the system.
   *
   * @return response containing the list of all admins
   */
  GetAdminListResponseDTO getAllAdmin();

  /**
   * Deletes an admin user by their credentials.
   *
   * @param deleteAdmin the admin details for deletion
   * @return response indicating the deletion result
   */
  DeleteAdminResponseDTO deleteAdmin(DeleteAdminRequestDTO deleteAdmin);

  /**
   * Retrieves a specific admin by their email address.
   *
   * @param getAdminRequestDTO the admin's email address
   * @return response containing the admin details
   */
  GetAdminResponseDTO getAdmin(GetAdminRequestDTO getAdminRequestDTO);
}
