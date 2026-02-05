package fr.elikia.backend.controller;

import fr.elikia.backend.bll.AdminService;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.dto.RegisterDTO;
import fr.elikia.backend.security.jwt.RequiredJWTAuth;
import fr.elikia.backend.security.jwt.RequiredRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin")
@RequiredJWTAuth
@RequiredRole("ADMIN")
@Tag(name = "Admin Dashbord", description = "Gestion de l'espace admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {

        this.adminService = adminService;
    }

    /**
     * Post mapping for register
     * @return the register method of the admin service
     */
    @Operation(
            summary = "Inscription admin",
            description = "Inscription à la plateforme comme administrateur"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Request send"
    )
    @ApiResponse(
            responseCode = "400",
            description = "valid data"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Email already in use"
    )
    @PostMapping("/register")
    public ResponseEntity<LogicResult<Void>> register(@RequestBody RegisterDTO registerDTO){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminService.createAdmin(registerDTO));
    }



}
