package fr.elikia.backend.controller;

import fr.elikia.backend.bll.AdminService;
import fr.elikia.backend.bll.MemberService;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.Member;
import fr.elikia.backend.dto.AdminUpdateMemberDTO;
import fr.elikia.backend.dto.MemberAdminDTO;
import fr.elikia.backend.dto.RegisterDTO;
import fr.elikia.backend.dto.RejectMembershipDTO;
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
     * Convertit le code métier retourné par LogicResult
     * en statut HTTP exploitable par la réponse REST.
     *
     * @param code code métier sous forme de chaîne
     * @return statut HTTP correspondant ou INTERNAL_SERVER_ERROR par défaut
     */
    private HttpStatus resolveHttpStatus(String code) {
        HttpStatus status = HttpStatus.resolve(Integer.parseInt(code));

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return status;
    }

    /**
     * Post mapping for register
     *
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
    public ResponseEntity<LogicResult<Void>> register(
            @RequestBody @Valid RegisterDTO registerDTO
    ) {
        LogicResult<Void> result = adminService.createAdmin(registerDTO);
        HttpStatus status = resolveHttpStatus(result.getCode());

        return ResponseEntity
                .status(status)
                .body(result);
    }

    /**
     * Récupère toutes les member
     * pour l'interface d'administration.
     */
    @Operation(
            summary = "Liste des membres",
            description = "Récupère tous les membres enregistrés pour l'interface d'administration"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Membres récupérés avec succès"
    )
    @GetMapping("/members")
    public ResponseEntity<LogicResult<List<MemberAdminDTO>>> getAllMembers() {
        LogicResult<List<MemberAdminDTO>> result = memberService.findAll();
        HttpStatus status = resolveHttpStatus(result.getCode());

        return ResponseEntity
                .status(status)
                .body(result);
    }

    /**
     * Accepte une demande d'adhésion.
     */
    /*@Operation(
            summary = "Acceptation d'une demande d'adhésion",
            description = "Valide une demande d'adhésion, génère le numéro d'adhésion et renseigne la date d'adhésion"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Demande d'adhésion acceptée avec succès"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Identifiant du membre invalide"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Membre introuvable"
    )
    @PatchMapping("/membership-requests/{id}/accept")
    public ResponseEntity<LogicResult<MemberAdminDTO>> acceptMembership(
            @PathVariable Long id
    ) {
        LogicResult<MemberAdminDTO> result = memberService.acceptMembership(id);
        HttpStatus status = resolveHttpStatus(result.getCode());

        return ResponseEntity
                .status(status)
                .body(result);
    }*/

    /**
     * Refuse une demande d'adhésion avec un motif envoyé par l'administrateur.
     */
    /*@Operation(
            summary = "Refus d'une demande d'adhésion",
            description = "Refuse une demande d'adhésion avec un motif renseigné par l'administrateur"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Demande d'adhésion refusée avec succès"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Identifiant invalide ou motif du refus manquant"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Membre introuvable"
    )
    @PatchMapping("/membership-requests/{id}/reject")
    public ResponseEntity<LogicResult<MemberAdminDTO>> rejectMembership(
            @PathVariable Long id,
            @Valid @RequestBody RejectMembershipDTO dto
    ) {
        LogicResult<MemberAdminDTO> result =
                memberService.rejectMembership(id, dto.getReason());

        HttpStatus status = resolveHttpStatus(result.getCode());

        return ResponseEntity
                .status(status)
                .body(result);
    }*/


    /**
     * Met à jour les informations administratives d'un membre.
     */
    @Operation(
            summary = "Mise à jour d'un membre",
            description = "Permet à l'administrateur de modifier le statut ou le rôle d'un membre"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Membre mis à jour avec succès"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Identifiant invalide ou données de mise à jour invalides"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Membre ou rôle introuvable"
    )
    @PatchMapping("/members/{id}")
    public ResponseEntity<LogicResult<Member>> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateMemberDTO dto
    ) {
        LogicResult<Member> result = memberService.updateMember(id, dto);
        HttpStatus status = resolveHttpStatus(result.getCode());

        return ResponseEntity
                .status(status)
                .body(result);
    }
}
