package fr.elikia.backend.event;

import fr.elikia.backend.AbstractIntegrationTest;
import fr.elikia.backend.bo.Event;
import fr.elikia.backend.bo.enums.Visibility;
import fr.elikia.backend.repository.EventRegistrationRepository;
import fr.elikia.backend.repository.EventRepository;
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
class EventRegistrationFunctionalTest extends AbstractIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

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
     * Create and save an event in the test database.
     */
    private Event createEvent(Visibility visibility) {
        Event event = new Event(
                "Spring Boot Event",
                "Functional test event",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(2),
                "Paris",
                "10 Test Street",
                50,
                visibility
        );

        return eventRepository.save(event);
    }

    /**
     * Verify that a visitor can register for a public event.
     */
    @Test
    void visitorCanRegisterToPublicEvent() throws Exception {
        // Arrange
        Event event = createEvent(Visibility.PUBLIC);

        String requestBody = """
                {
                  "firstName": "Iman",
                  "lastName": "Test",
                  "email": "iman.event@example.com"
                }
                """;

        // Act and assert
        mockMvc.perform(
                        post(
                                "/api/event-registration/public/event/{eventId}",
                                event.getEventId()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(
                        jsonPath("$.message")
                                .value("Event registration created successfully")
                );

        assertTrue(
                eventRegistrationRepository.existsByEventAndEmail(
                        event,
                        "iman.event@example.com"
                )
        );
    }

    /**
     * Verify that a visitor cannot register twice
     * for the same event using the same email.
     */
    @Test
    void visitorCannotRegisterTwiceToSameEvent() throws Exception {
        // Arrange
        Event event = createEvent(Visibility.PUBLIC);

        String requestBody = """
                {
                  "firstName": "Iman",
                  "lastName": "Test",
                  "email": "duplicate.event@example.com"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/event-registration/public/event/{eventId}",
                                event.getEventId()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated());

        // Act and assert
        mockMvc.perform(
                        post(
                                "/api/event-registration/public/event/{eventId}",
                                event.getEventId()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "This email is already registered for this event"
                                )
                );
    }

    /**
     * Verify that a visitor cannot register
     * for a member-only event.
     */
    @Test
    void visitorCannotRegisterToMemberOnlyEvent() throws Exception {
        // Arrange
        Event event = createEvent(Visibility.MEMBER_ONLY);

        String requestBody = """
                {
                  "firstName": "Iman",
                  "lastName": "Test",
                  "email": "visitor.memberonly@example.com"
                }
                """;

        // Act and assert
        mockMvc.perform(
                        post(
                                "/api/event-registration/public/event/{eventId}",
                                event.getEventId()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"))
                .andExpect(
                        jsonPath("$.message")
                                .value("This event is reserved for members only")
                );

        assertFalse(
                eventRegistrationRepository.existsByEventAndEmail(
                        event,
                        "visitor.memberonly@example.com"
                )
        );
    }

    /**
     * Verify that registration returns 404
     * when the event does not exist.
     */
    @Test
    void registrationReturnsNotFoundWhenEventDoesNotExist()
            throws Exception {

        String requestBody = """
                {
                  "firstName": "Iman",
                  "lastName": "Test",
                  "email": "notfound.event@example.com"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/event-registration/public/event/{eventId}",
                                999999L
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("Event not found"));
    }

    /**
     * Verify that an administrator can retrieve
     * registrations for an event.
     */
    @Test
    void adminCanRetrieveEventRegistrations() throws Exception {
        // Arrange
        Event event = createEvent(Visibility.PUBLIC);

        String token = jwtService.generateToken(
                "admin@mail.com",
                "ADMIN"
        );

        // Act and assert
        mockMvc.perform(
                        get(
                                "/api/event-registration/admin/event/{eventId}",
                                event.getEventId()
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
                                        "Event registrations retrieved successfully"
                                )
                )
                .andExpect(jsonPath("$.data").isArray());
    }
}