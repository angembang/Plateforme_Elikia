package fr.elikia.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthResponseDTO {
    @Schema(
            description = "JWT token",
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    private String token;

    public AuthResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
