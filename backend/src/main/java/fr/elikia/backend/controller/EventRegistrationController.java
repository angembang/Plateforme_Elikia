package fr.elikia.backend.controller;

import fr.elikia.backend.bll.EventRegistrationService;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.dto.EventRegistrationDTO;
import fr.elikia.backend.security.jwt.RequiredJWTAuth;
import fr.elikia.backend.security.jwt.RequiredRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        // Convertit le code métier en statut HTTP
        HttpStatus status = HttpStatus.resolve(
                Integer.parseInt(result.getCode())
        );

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

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

        // Convertit le code métier en statut HTTP
        HttpStatus status = HttpStatus.resolve(
                Integer.parseInt(result.getCode())
        );

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity
                .status(status)
                .body(result);
    }
}