package fr.elikia.backend.workshop;

import fr.elikia.backend.AbstractIntegrationTest;
import fr.elikia.backend.bo.Workshop;
import fr.elikia.backend.bo.enums.Visibility;
import fr.elikia.backend.repository.WorkshopRegistrationRepository;
import fr.elikia.backend.repository.WorkshopRepository;
import fr.elikia.backend.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WorkshopRegistrationFunctionalTest
        extends AbstractIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private WorkshopRepository workshopRepository;

    @Autowired
    private WorkshopRegistrationRepository workshopRegistrationRepository;

    @Autowired
    private JwtService jwtService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    /**
     * Create and save a workshop in the test database.
     */
    private Workshop createWorkshop(Visibility visibility) {
        Workshop workshop = new Workshop(
                "Spring Boot Workshop",
                "Functional test workshop",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(3),
                "Paris",
                "20 Test Street",
                30,
                visibility
        );

        return workshopRepository.save(workshop);
    }

    /**
     * Verify that a visitor can register for a public workshop.
     */
    @Test
    void visitorCanRegisterToPublicWorkshop() throws Exception {
        // Arrange
        Workshop workshop = createWorkshop(Visibility.PUBLIC);

        String requestBody = """
                {
                  "firstName": "Iman",
                  "lastName": "Test",
                  "email": "iman.workshop@example.com"
                }
                """;

        // Act and assert
        mockMvc.perform(
                        post(
                                "/api/workshop-registration/public/workshop/{workshopId}",
                                workshop.getWorkshopId()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Workshop registration created successfully"
                                )
                );

        assertTrue(
                workshopRegistrationRepository
                        .existsByWorkshopAndEmail(
                                workshop,
                                "iman.workshop@example.com"
                        )
        );
    }

    /**
     * Verify that a visitor cannot register twice
     * for the same workshop using the same email.
     */
    @Test
    void visitorCannotRegisterTwiceToSameWorkshop() throws Exception {
        // Arrange
        Workshop workshop = createWorkshop(Visibility.PUBLIC);

        String requestBody = """
                {
                  "firstName": "Iman",
                  "lastName": "Test",
                  "email": "duplicate.workshop@example.com"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/workshop-registration/public/workshop/{workshopId}",
                                workshop.getWorkshopId()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated());

        // Act and assert
        mockMvc.perform(
                        post(
                                "/api/workshop-registration/public/workshop/{workshopId}",
                                workshop.getWorkshopId()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "This email is already registered for this workshop"
                                )
                );
    }

    /**
     * Verify that a visitor cannot register
     * for a member-only workshop.
     */
    @Test
    void visitorCannotRegisterToMemberOnlyWorkshop() throws Exception {
        // Arrange
        Workshop workshop = createWorkshop(Visibility.MEMBER_ONLY);

        String requestBody = """
                {
                  "firstName": "Iman",
                  "lastName": "Test",
                  "email": "visitor.workshop@example.com"
                }
                """;

        // Act and assert
        mockMvc.perform(
                        post(
                                "/api/workshop-registration/public/workshop/{workshopId}",
                                workshop.getWorkshopId()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));

        assertFalse(
                workshopRegistrationRepository
                        .existsByWorkshopAndEmail(
                                workshop,
                                "visitor.workshop@example.com"
                        )
        );
    }

    /**
     * Verify that registration returns 404
     * when the workshop does not exist.
     */
    @Test
    void registrationReturnsNotFoundWhenWorkshopDoesNotExist()
            throws Exception {

        String requestBody = """
                {
                  "firstName": "Iman",
                  "lastName": "Test",
                  "email": "notfound.workshop@example.com"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/workshop-registration/public/workshop/{workshopId}",
                                999999L
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(
                        jsonPath("$.message")
                                .value("Workshop not found")
                );
    }

    /**
     * Verify that an administrator can retrieve
     * registrations for a workshop.
     */
    @Test
    void adminCanRetrieveWorkshopRegistrations() throws Exception {
        // Arrange
        Workshop workshop = createWorkshop(Visibility.PUBLIC);

        String token = jwtService.generateToken(
                "admin@mail.com",
                "ADMIN"
        );

        // Act and assert
        mockMvc.perform(
                        get(
                                "/api/workshop-registration/admin/workshop/{workshopId}",
                                workshop.getWorkshopId()
                        )
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Workshop registrations retrieved successfully"
                                )
                )
                .andExpect(jsonPath("$.data").isArray());
    }
}