package fr.elikia.backend.controller;

import fr.elikia.backend.bll.WorkshopRegistrationService;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.dto.RefusalReasonDTO;
import fr.elikia.backend.dto.WorkshopRegistrationAdminDTO;
import fr.elikia.backend.dto.WorkshopRegistrationDTO;
import fr.elikia.backend.security.jwt.RequiredJWTAuth;
import fr.elikia.backend.security.jwt.RequiredRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller responsible for managing workshop registrations.
 */
@RestController
@RequestMapping("/api/workshop-registration")
@Tag(
        name = "Workshop Registration",
        description = "Endpoints for managing workshop registrations"
)
public class WorkshopRegistrationController {

    private final WorkshopRegistrationService workshopRegistrationService;

    public WorkshopRegistrationController(
            WorkshopRegistrationService workshopRegistrationService
    ) {
        this.workshopRegistrationService = workshopRegistrationService;
    }

    /**
     * Convert the business code returned by LogicResult
     * into an HTTP status.
     *
     * @param code business code
     * @return corresponding HTTP status or INTERNAL_SERVER_ERROR by default
     */
    private HttpStatus resolveHttpStatus(String code) {
        HttpStatus status = HttpStatus.resolve(Integer.parseInt(code));

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return status;
    }

    /**
     * Create a workshop registration request for a visitor.
     *
     * @param workshopId workshop identifier
     * @param workshopRegistrationDTO registration data
     * @return HTTP response containing a LogicResult
     */
    @Operation(
            summary = "Register a visitor to a workshop",
            description = "Creates a pending registration for a public workshop"
    )
    @ApiResponse(responseCode = "201", description = "Registration created successfully")
    @ApiResponse(responseCode = "400", description = "Validation or business error")
    @ApiResponse(responseCode = "404", description = "Workshop not found")
    @PostMapping("/public/workshop/{workshopId}")
    public ResponseEntity<LogicResult<Void>> registerVisitorToWorkshop(
            @PathVariable Long workshopId,
            @RequestBody @Valid WorkshopRegistrationDTO workshopRegistrationDTO
    ) {
        LogicResult<Void> result =
                workshopRegistrationService.registerToWorkshop(
                        workshopId,
                        workshopRegistrationDTO,
                        null
                );

        HttpStatus status = resolveHttpStatus(result.getCode());

        return ResponseEntity
                .status(status)
                .body(result);
    }

    /**
     * Create a workshop registration request for a connected member.
     *
     * @param workshopId workshop identifier
     * @param workshopRegistrationDTO registration data
     * @param memberEmail connected member email
     * @return HTTP response containing a LogicResult
     */
    @Operation(
            summary = "Register a member to a workshop",
            description = "Creates a pending registration for a workshop"
    )
    @ApiResponse(responseCode = "201", description = "Registration created successfully")
    @ApiResponse(responseCode = "400", description = "Validation or business error")
    @ApiResponse(responseCode = "404", description = "Workshop or member not found")
    @PostMapping("/member/workshop/{workshopId}")
    @RequiredJWTAuth
    @RequiredRole("MEMBER")
    public ResponseEntity<LogicResult<Void>> registerMemberToWorkshop(
            @PathVariable Long workshopId,
            @RequestBody @Valid WorkshopRegistrationDTO workshopRegistrationDTO,
            @RequestParam String memberEmail
    ) {
        LogicResult<Void> result =
                workshopRegistrationService.registerToWorkshop(
                        workshopId,
                        workshopRegistrationDTO,
                        memberEmail
                );

        HttpStatus status = resolveHttpStatus(result.getCode());

        return ResponseEntity
                .status(status)
                .body(result);
    }

    /**
     * Retrieve all registrations linked to a workshop.
     *
     * @param workshopId workshop identifier
     * @return workshop registrations
     */
    @Operation(
            summary = "Get registrations by workshop",
            description = "Returns all registrations associated with a specific workshop"
    )
    @ApiResponse(responseCode = "200", description = "Registrations retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid workshop identifier")
    @ApiResponse(responseCode = "404", description = "Workshop not found")
    @GetMapping("/admin/workshop/{workshopId}")
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public ResponseEntity<LogicResult<List<WorkshopRegistrationAdminDTO>>> getRegistrationsByWorkshop(
            @PathVariable Long workshopId
    ) {
        LogicResult<List<WorkshopRegistrationAdminDTO>> result =
                workshopRegistrationService.getRegistrationsByWorkshop(workshopId);

        HttpStatus status = resolveHttpStatus(result.getCode());

        return ResponseEntity
                .status(status)
                .body(result);
    }

    /**
     * Approve a workshop registration.
     *
     * @param registrationId registration identifier
     * @return operation result
     */
    @Operation(
            summary = "Approve a workshop registration",
            description = "Changes the registration status to APPROVED"
    )
    @ApiResponse(responseCode = "200", description = "Registration approved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid registration identifier")
    @ApiResponse(responseCode = "404", description = "Registration not found")
    @PutMapping("/admin/{registrationId}/approve")
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public ResponseEntity<LogicResult<Void>> approveRegistration(
            @PathVariable Long registrationId
    ) {
        LogicResult<Void> result =
                workshopRegistrationService.approveRegistration(registrationId);

        HttpStatus status = resolveHttpStatus(result.getCode());

        return ResponseEntity
                .status(status)
                .body(result);
    }

    /**
     * Reject a workshop registration with a refusal reason.
     *
     * @param registrationId registration identifier
     * @param refusalReasonDTO refusal reason
     * @return operation result
     */
    @Operation(
            summary = "Reject a workshop registration",
            description = "Changes the registration status to REJECTED"
    )
    @ApiResponse(responseCode = "200", description = "Registration rejected successfully")
    @ApiResponse(responseCode = "400", description = "Validation or business error")
    @ApiResponse(responseCode = "404", description = "Registration not found")
    @PutMapping("/admin/{registrationId}/reject")
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public ResponseEntity<LogicResult<Void>> rejectRegistration(
            @PathVariable Long registrationId,
            @RequestBody @Valid RefusalReasonDTO refusalReasonDTO
    ) {
        LogicResult<Void> result =
                workshopRegistrationService.rejectRegistration(
                        registrationId,
                        refusalReasonDTO.getReason()
                );

        HttpStatus status = resolveHttpStatus(result.getCode());

        return ResponseEntity
                .status(status)
                .body(result);
    }

    /**
     * Cancel a workshop registration.
     *
     * @param registrationId registration identifier
     * @return operation result
     */
    @Operation(
            summary = "Cancel a workshop registration",
            description = "Changes the registration status to CANCELLED"
    )
    @ApiResponse(responseCode = "200", description = "Registration cancelled successfully")
    @ApiResponse(responseCode = "400", description = "Invalid registration identifier")
    @ApiResponse(responseCode = "404", description = "Registration not found")
    @PutMapping("/member/{registrationId}/cancel")
    @RequiredJWTAuth
    @RequiredRole("MEMBER")
    public ResponseEntity<LogicResult<Void>> cancelRegistration(
            @PathVariable Long registrationId
    ) {
        LogicResult<Void> result =
                workshopRegistrationService.cancelRegistration(registrationId);

        HttpStatus status = resolveHttpStatus(result.getCode());

        return ResponseEntity
                .status(status)
                .body(result);
    }
}