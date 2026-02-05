package fr.elikia.backend.controller;

import fr.elikia.backend.bll.AuthService;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.dto.AuthResponseDTO;
import fr.elikia.backend.dto.LoginDTO;
import fr.elikia.backend.dto.RegisterDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@Tag(name = "Authentification", description = "Gestion de la connexion et de l'adhésion")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Post mapping for register
     * @return the register method of the Auth service
     */
    @Operation(
            summary = "Demande d'adhésion",
            description = "Soumet une demande d'adhésion qui sera validée par un administrateur"
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
    public  ResponseEntity<LogicResult<Void>> register(@RequestBody RegisterDTO registerDTO){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(registerDTO));
    }


    /**
     * Post mapping for login
     * @return the login method of the Auth service
     */
    @Operation(
            summary = "Connexion utilisateur",
            description = "Permet à un administrateur ou un membre validé de se connecter"
    )
    @ApiResponse(
            responseCode = "200",
            description = "connexion réussie"
    )
    @ApiResponse(
            responseCode = "401",
            description = "Email ou mot de passe incorrect"
    )
    @ApiResponse(
            responseCode = "403",
            description = "compte non validé ou accès refusé"
    )
    @ApiResponse(
            responseCode = "423",
            description = "Compte temporairement bloqué"
    )
    @PostMapping("/login")
    public ResponseEntity<LogicResult<AuthResponseDTO>> login(@RequestBody LoginDTO loginDTO) {

        LogicResult<String> result = authService.login(loginDTO);

        HttpStatus status = HttpStatus.resolve(
                Integer.parseInt(result.getCode())
        );

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        // If failed (no token)
        if (result.getData() == null) {
            return ResponseEntity
                    .status(status)
                    .body(new LogicResult<>(
                            result.getCode(),
                            result.getMessage(),
                            null
                    ));
        }

        // Success
        return ResponseEntity
                .status(status)
                .body(new LogicResult<>(
                        result.getCode(),
                        result.getMessage(),
                        new AuthResponseDTO(result.getData())
                ));
    }


}
