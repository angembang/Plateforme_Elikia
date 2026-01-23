package fr.elikia.backend.auth;

import fr.elikia.backend.bo.Member;
import fr.elikia.backend.bo.Role;
import fr.elikia.backend.dao.idao.IDAOMember;
import fr.elikia.backend.dao.idao.IDAORole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class AuthControllerIntegrationTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private IDAOMember idaoMember;

    @Autowired
    private IDAORole idaoRole;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void itShouldReturn403WhenMemberNotValidated() throws Exception {

        // GIVEN
        Role role = new Role();
        role.setName("BENEVOLE");
        idaoRole.create(role);

        Member member = new Member();
        member.setFirstName("John");
        member.setLastName("Doe");
        member.setEmail("member@mail.com");
        member.setPassword(passwordEncoder.encode("password123"));
        member.setStatus("INSCRIPTION_TRANSMISE");

        member.setCreatedAt(LocalDate.now());
        member.setFailedLoginAttempts(0);
        member.setLockUntil(null);
        member.setImage(null);
        member.setRole(role);
        member.setMembershipDate(null);
        member.setMembershipNumber(null);

        idaoMember.create(member);

        // WHEN / THEN
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "email": "member@mail.com",
                              "password": "password123"
                            }
                        """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value("Your membership is currently being processed."));
    }
}

