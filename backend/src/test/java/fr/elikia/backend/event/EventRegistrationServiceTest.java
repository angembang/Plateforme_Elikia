package fr.elikia.backend.event;

import fr.elikia.backend.bll.EmailService;
import fr.elikia.backend.bll.EventRegistrationService;
import fr.elikia.backend.bo.Event;
import fr.elikia.backend.bo.EventRegistration;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.enums.RegistrationStatus;
import fr.elikia.backend.bo.enums.Visibility;
import fr.elikia.backend.dao.idao.IDAOEvent;
import fr.elikia.backend.dao.idao.IDAOEventRegistration;
import fr.elikia.backend.dao.idao.IDAOMember;
import fr.elikia.backend.dto.EventRegistrationDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventRegistrationServiceTest {

    @Mock
    private IDAOEvent idaoEvent;

    @Mock
    private IDAOEventRegistration idaoEventRegistration;

    @Mock
    private IDAOMember idaoMember;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EventRegistrationService eventRegistrationService;

    /**
     * Build valid registration data used by the tests.
     */
    private EventRegistrationDTO buildValidRegistrationDTO() {
        EventRegistrationDTO dto = new EventRegistrationDTO();

        dto.setFirstName("Iman");
        dto.setLastName("Test");
        dto.setEmail("iman@example.com");

        return dto;
    }

    /**
     * Verify that a visitor can successfully register for a public event.
     */
    @Test
    void shouldRegisterVisitorToPublicEventSuccessfully() {
        // Arrange
        Long eventId = 1L;

        Event event = new Event();
        event.setVisibility(Visibility.PUBLIC);

        EventRegistrationDTO registrationDTO =
                buildValidRegistrationDTO();

        when(idaoEvent.findById(eventId))
                .thenReturn(event);

        when(idaoEventRegistration.existsByEventAndEmail(
                event,
                registrationDTO.getEmail()
        )).thenReturn(false);

        when(idaoEventRegistration.create(
                any(EventRegistration.class)
        )).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        LogicResult<Void> result =
                eventRegistrationService.registerToEvent(
                        eventId,
                        registrationDTO,
                        null
                );

        // Assert
        assertEquals("201", result.getCode());
        assertEquals(
                "Event registration created successfully",
                result.getMessage()
        );
        assertNull(result.getData());

        ArgumentCaptor<EventRegistration> registrationCaptor =
                ArgumentCaptor.forClass(EventRegistration.class);

        verify(idaoEventRegistration)
                .create(registrationCaptor.capture());

        EventRegistration savedRegistration =
                registrationCaptor.getValue();

        assertEquals(
                registrationDTO.getFirstName(),
                savedRegistration.getFirstName()
        );

        assertEquals(
                registrationDTO.getLastName(),
                savedRegistration.getLastName()
        );

        assertEquals(
                registrationDTO.getEmail(),
                savedRegistration.getEmail()
        );

        assertEquals(
                RegistrationStatus.PENDING,
                savedRegistration.getStatus()
        );

        assertSame(event, savedRegistration.getEvent());
        assertNull(savedRegistration.getMember());
        assertNotNull(savedRegistration.getRegistrationDate());

        verify(idaoEvent).findById(eventId);

        verify(idaoEventRegistration)
                .existsByEventAndEmail(
                        event,
                        registrationDTO.getEmail()
                );

        verify(
                idaoEventRegistration,
                never()
        ).existsByEventAndMember(any(), any());

        verifyNoInteractions(idaoMember, emailService);
    }

    /**
     * Verify that registration fails when the event does not exist.
     */
    @Test
    void shouldReturnNotFoundWhenEventDoesNotExist() {
        // Arrange
        Long eventId = 1L;

        EventRegistrationDTO registrationDTO =
                buildValidRegistrationDTO();

        when(idaoEvent.findById(eventId))
                .thenReturn(null);

        // Act
        LogicResult<Void> result =
                eventRegistrationService.registerToEvent(
                        eventId,
                        registrationDTO,
                        null
                );

        // Assert
        assertEquals("404", result.getCode());
        assertEquals("Event not found", result.getMessage());
        assertNull(result.getData());

        verify(idaoEvent).findById(eventId);

        verifyNoInteractions(
                idaoEventRegistration,
                idaoMember,
                emailService
        );
    }

    /**
     * Verify that the same visitor cannot register twice
     * for the same event.
     */
    @Test
    void shouldNotRegisterVisitorTwiceToSameEvent() {
        // Arrange
        Long eventId = 1L;

        Event event = new Event();
        event.setVisibility(Visibility.PUBLIC);

        EventRegistrationDTO registrationDTO =
                buildValidRegistrationDTO();

        when(idaoEvent.findById(eventId))
                .thenReturn(event);

        when(idaoEventRegistration.existsByEventAndEmail(
                event,
                registrationDTO.getEmail()
        )).thenReturn(true);

        // Act
        LogicResult<Void> result =
                eventRegistrationService.registerToEvent(
                        eventId,
                        registrationDTO,
                        null
                );

        // Assert
        assertEquals("400", result.getCode());
        assertEquals(
                "This email is already registered for this event",
                result.getMessage()
        );
        assertNull(result.getData());

        verify(idaoEvent).findById(eventId);

        verify(idaoEventRegistration)
                .existsByEventAndEmail(
                        event,
                        registrationDTO.getEmail()
                );

        verify(
                idaoEventRegistration,
                never()
        ).create(any(EventRegistration.class));

        verify(
                idaoEventRegistration,
                never()
        ).existsByEventAndMember(any(), any());

        verifyNoInteractions(idaoMember, emailService);
    }

    /**
     * Verify that an event registration can be approved.
     */
    @Test
    void shouldApproveEventRegistrationSuccessfully() {
        // Arrange
        Long registrationId = 10L;

        Event event = new Event();
        event.setTitle("Spring Boot Event");

        EventRegistration registration =
                new EventRegistration();

        registration.setFirstName("Iman");
        registration.setEmail("iman@example.com");
        registration.setStatus(RegistrationStatus.PENDING);
        registration.setEvent(event);

        when(idaoEventRegistration.findById(registrationId))
                .thenReturn(registration);

        when(idaoEventRegistration.update(registration))
                .thenReturn(registration);

        // Act
        LogicResult<Void> result =
                eventRegistrationService.approveRegistration(
                        registrationId
                );

        // Assert
        assertEquals("200", result.getCode());

        assertEquals(
                "Event registration approved successfully",
                result.getMessage()
        );

        assertNull(result.getData());

        assertEquals(
                RegistrationStatus.APPROVED,
                registration.getStatus()
        );

        verify(idaoEventRegistration)
                .findById(registrationId);

        verify(idaoEventRegistration)
                .update(registration);

        verify(emailService)
                .sendEventRegistrationAcceptedEmail(
                        "iman@example.com",
                        "Iman",
                        "Spring Boot Event"
                );

        verifyNoInteractions(idaoEvent, idaoMember);
    }

    /**
     * Verify that an event registration can be rejected.
     */
    @Test
    void shouldRejectEventRegistrationSuccessfully() {
        // Arrange
        Long registrationId = 10L;
        String refusalReason = "The event is already full";

        Event event = new Event();
        event.setTitle("Spring Boot Event");

        EventRegistration registration =
                new EventRegistration();

        registration.setFirstName("Iman");
        registration.setEmail("iman@example.com");
        registration.setStatus(RegistrationStatus.PENDING);
        registration.setEvent(event);

        when(idaoEventRegistration.findById(registrationId))
                .thenReturn(registration);

        when(idaoEventRegistration.update(registration))
                .thenReturn(registration);

        // Act
        LogicResult<Void> result =
                eventRegistrationService.rejectRegistration(
                        registrationId,
                        refusalReason
                );

        // Assert
        assertEquals("200", result.getCode());

        assertEquals(
                "Event registration rejected successfully",
                result.getMessage()
        );

        assertNull(result.getData());

        assertEquals(
                RegistrationStatus.REJECTED,
                registration.getStatus()
        );

        verify(idaoEventRegistration)
                .findById(registrationId);

        verify(idaoEventRegistration)
                .update(registration);

        verify(emailService)
                .sendEventRegistrationRejectedEmail(
                        "iman@example.com",
                        "Iman",
                        "Spring Boot Event",
                        refusalReason
                );

        verifyNoInteractions(idaoEvent, idaoMember);
    }
}