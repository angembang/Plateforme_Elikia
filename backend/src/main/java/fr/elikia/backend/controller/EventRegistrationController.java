package fr.elikia.backend.controller;

import fr.elikia.backend.bll.EventRegistrationService;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.dto.EventRegistrationDTO;
import fr.elikia.backend.dto.RefusalReasonDTO;
import fr.elikia.backend.security.jwt.RequiredJWTAuth;
import fr.elikia.backend.security.jwt.RequiredRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import fr.elikia.backend.dto.EventRegistrationAdminDTO;

import java.util.List;

/**
 * Contrôleur REST responsable de la gestion des inscriptions aux événements.
 */
@RestController
@RequestMapping("/api/event-registration")
@Tag(
        name = "Event Registration",
        description = "Endpoints for managing event registrations"
)
public class EventRegistrationController {

    // Dépendance vers le service métier
    private final EventRegistrationService eventRegistrationService;

    // Injection du service via le constructeur
    public EventRegistrationController(EventRegistrationService eventRegistrationService) {
        this.eventRegistrationService = eventRegistrationService;
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
     * Crée une demande d'inscription à un événement public.
     *
     * Cet endpoint :
     * - vérifie l'existence de l'événement
     * - valide les informations envoyées par le visiteur
     * - empêche les inscriptions en double
     * - crée une inscription avec le statut PENDING
     *
     * @param eventId identifiant unique de l'événement
     * @param eventRegistrationDTO données nécessaires à l'inscription
     *
     * @return réponse HTTP contenant un LogicResult
     */
    @Operation(
            summary = "Register a visitor to a public event",
            description = "Creates a pending registration for a public event"
    )
    @ApiResponse(responseCode = "201", description = "Registration created successfully")
    @ApiResponse(responseCode = "400", description = "Validation or business error")
    @ApiResponse(responseCode = "403", description = "Event reserved for members")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @PostMapping("/public/event/{eventId}")
    public ResponseEntity<LogicResult<Void>> registerVisitorToEvent(
            @PathVariable Long eventId,
            @RequestBody EventRegistrationDTO eventRegistrationDTO
    ) {
        // Délègue la validation et la logique métier au service
        LogicResult<Void> result =
                eventRegistrationService.registerToEvent(
                        eventId,
                        eventRegistrationDTO,
                        null
                );

        HttpStatus status = resolveHttpStatus(result.getCode());

        return ResponseEntity
                .status(status)
                .body(result);
    }

    /**
     * Crée une demande d'inscription à un événement pour un membre connecté.
     *
     * Cet endpoint :
     * - nécessite une authentification JWT
     * - vérifie le rôle MEMBER
     * - permet l'inscription aux événements publics ou réservés aux membres
     * - crée une inscription avec le statut PENDING
     *
     * @param eventId identifiant unique de l'événement
     * @param eventRegistrationDTO données nécessaires à l'inscription
     * @param memberEmail email du membre connecté
     *
     * @return réponse HTTP contenant un LogicResult
     */
    @Operation(
            summary = "Register a member to an event",
            description = "Creates a pending registration for a public or member-only event"
    )
    @ApiResponse(responseCode = "201", description = "Registration created successfully")
    @ApiResponse(responseCode = "400", description = "Validation or business error")
    @ApiResponse(responseCode = "404", description = "Event or member not found")
    @PostMapping("/member/event/{eventId}")
    @RequiredJWTAuth
    @RequiredRole("MEMBER")
    public ResponseEntity<LogicResult<Void>> registerMemberToEvent(
            @PathVariable Long eventId,
            @RequestBody EventRegistrationDTO eventRegistrationDTO,
            @RequestParam String memberEmail
    ) {
        // Délègue la validation et la logique métier au service
        LogicResult<Void> result =
                eventRegistrationService.registerToEvent(
                        eventId,
                        eventRegistrationDTO,
                        memberEmail
                );

        HttpStatus status = resolveHttpStatus(result.getCode());

        return ResponseEntity
                .status(status)
                .body(result);
    }

    /**
     * Récupère toutes les inscriptions d'un événement.
     *
     * Cet endpoint est réservé à l'administrateur.
     *
     * @param eventId identifiant unique de l'événement
     * @return liste des inscriptions liées à l'événement
     */
    @Operation(
            summary = "Get registrations by event",
            description = "Returns all registrations associated with a specific event"
    )
    @ApiResponse(responseCode = "200", description = "Registrations retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid event identifier")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @GetMapping("/admin/event/{eventId}")
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public ResponseEntity<LogicResult<List<EventRegistrationAdminDTO>>> getRegistrationsByEvent(
            @PathVariable Long eventId
    ) {
        LogicResult<List<EventRegistrationAdminDTO>> result =
                eventRegistrationService.getRegistrationsByEvent(eventId);

        HttpStatus status = resolveHttpStatus(result.getCode());

        return ResponseEntity
                .status(status)
                .body(result);
    }

    /**
     * Accepte une inscription à un événement.
     *
     * Cet endpoint est réservé à l'administrateur.
     *
     * @param registrationId identifiant unique de l'inscription
     * @return résultat de l'opération
     */
    @Operation(
            summary = "Approve an event registration",
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
                eventRegistrationService.approveRegistration(registrationId);

        HttpStatus status = resolveHttpStatus(result.getCode());

        return ResponseEntity
                .status(status)
                .body(result);
    }

    /**
     * Refuse une inscription à un événement avec un motif.
     *
     * Cet endpoint est réservé à l'administrateur.
     *
     * @param registrationId identifiant unique de l'inscription
     * @param refusalReasonDTO motif du refus envoyé par l'administrateur
     * @return résultat de l'opération
     */
    @Operation(
            summary = "Reject an event registration",
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
                eventRegistrationService.rejectRegistration(
                        registrationId,
                        refusalReasonDTO.getReason()
                );

        HttpStatus status = resolveHttpStatus(result.getCode());

        return ResponseEntity
                .status(status)
                .body(result);
    }

    /**
     * Annule une inscription à un événement.
     *
     * Cet endpoint permet à un membre connecté d'annuler une inscription.
     *
     * @param registrationId identifiant unique de l'inscription
     * @return résultat de l'opération
     */
    @Operation(
            summary = "Cancel an event registration",
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
                eventRegistrationService.cancelRegistration(registrationId);

        HttpStatus status = resolveHttpStatus(result.getCode());

        return ResponseEntity
                .status(status)
                .body(result);
    }
}