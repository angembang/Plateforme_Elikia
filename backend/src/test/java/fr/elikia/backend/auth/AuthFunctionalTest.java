package fr.elikia.backend.auth;

import fr.elikia.backend.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class AuthFunctionalTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void rolesEndpointMustBeProtected() throws Exception {
        mockMvc.perform(get("/roles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminCanAccessRoles() throws Exception {
        String token = jwtService.generateToken("admin@mail.com", "ADMIN");

        mockMvc.perform(get("/roles")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void memberCannotAccessRoles() throws Exception {
        String token = jwtService.generateToken("member@mail.com", "MEMBER");

        mockMvc.perform(get("/roles")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
