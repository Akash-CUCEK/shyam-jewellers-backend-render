package com.shyam.controller;

import com.shyam.common.exception.dto.BaseResponseDTO;
import com.shyam.dto.request.AddPurityRequestDTO;
import com.shyam.dto.request.GetPurityByIdRequestDTO;
import com.shyam.dto.request.UpdatePurityRequestDTO;
import com.shyam.dto.response.AddPurityResponseDTO;
import com.shyam.dto.response.GetPurityResponseDTO;
import com.shyam.service.PurityService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminPurityController {

    private final PurityService purityService;

    @Operation(summary = "Add purity", description = "Add a new purity for a material type.")
    @PostMapping("/addPurity")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public BaseResponseDTO<AddPurityResponseDTO> addPurity(
            @Valid @RequestBody AddPurityRequestDTO requestDTO) {
        var response = purityService.addPurity(requestDTO);
        return new BaseResponseDTO<>(response, null);
    }

    @Operation(summary = "Update purity", description = "Update an existing purity.")
    @PutMapping("/updatePurity")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public BaseResponseDTO<AddPurityResponseDTO> updatePurity(
            @Valid @RequestBody UpdatePurityRequestDTO requestDTO) {
        var response = purityService.updatePurity(requestDTO);
        return new BaseResponseDTO<>(response, null);
    }

    @Operation(summary = "Delete purity", description = "Delete a purity by its ID.")
    @DeleteMapping("/deletePurity")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public BaseResponseDTO<AddPurityResponseDTO> deletePurity(
            @Valid @RequestBody GetPurityByIdRequestDTO requestDTO) {
        var response = purityService.deletePurity(requestDTO);
        return new BaseResponseDTO<>(response, null);
    }

    @Operation(summary = "Get all purities", description = "Get list of all purities.")
    @PostMapping("/getAllPurities")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public BaseResponseDTO<List<GetPurityResponseDTO>> getAllPurities() {
        var response = purityService.getAllPurities();
        return new BaseResponseDTO<>(response, null);
    }
}
