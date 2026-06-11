package fr.elikia.backend.controller;

import fr.elikia.backend.bll.AdminService;
import fr.elikia.backend.bll.MemberService;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.Member;
import fr.elikia.backend.dto.RegisterDTO;
import fr.elikia.backend.dto.RejectMembershipDTO;
import fr.elikia.backend.security.jwt.RequiredJWTAuth;
import fr.elikia.backend.security.jwt.RequiredRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin")
@RequiredJWTAuth
@RequiredRole("ADMIN")
@Tag(name = "Admin Dashbord", description = "Gestion de l'espace admin")
public class AdminController {
    private final AdminService adminService;
    private final MemberService memberService;

    public AdminController(AdminService adminService, MemberService memberService) {

        this.adminService = adminService;
        this.memberService = memberService;
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
    /**
     * Récupère toutes les demandes d'adhésion en attente
     * pour l'interface d'administration.
     */
    @Operation(
            summary = "Liste des demandes d'adhésion",
            description = "Récupère toutes les demandes d'adhésion en attente de validation"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Demandes d'adhésion récupérées avec succès"
    )
    @GetMapping("/membership-requests")
    public ResponseEntity<LogicResult<List<Member>>> getMembershipRequests() {
        return ResponseEntity.ok(memberService.findPendingMembershipRequests());
    }

    /**
     * Accepte une demande d'adhésion.
     */
    @Operation(
            summary = "Acceptation d'une demande d'adhésion",
            description = "Valide une demande d'adhésion, génère le numéro d'adhésion et renseigne la date d'adhésion"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Demande d'adhésion acceptée avec succès"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Membre introuvable"
    )
    @PatchMapping("/membership-requests/{id}/accept")
    public ResponseEntity<LogicResult<Member>> acceptMembership(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.acceptMembership(id));
    }

    /**
     * Refuse une demande d'adhésion avec un motif envoyé par l'administrateur.
     */
    @Operation(
            summary = "Refus d'une demande d'adhésion",
            description = "Refuse une demande d'adhésion avec un motif renseigné par l'administrateur"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Demande d'adhésion refusée avec succès"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Membre introuvable"
    )
    @PatchMapping("/membership-requests/{id}/reject")
    public ResponseEntity<LogicResult<Member>> rejectMembership(
            @PathVariable Long id,
            @RequestBody RejectMembershipDTO dto
    ) {
        return ResponseEntity.ok(memberService.rejectMembership(id, dto.getReason()));
    }

}
