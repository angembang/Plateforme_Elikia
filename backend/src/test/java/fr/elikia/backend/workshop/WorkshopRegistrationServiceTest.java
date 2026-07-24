package fr.elikia.backend.workshop;

import fr.elikia.backend.bll.EmailService;
import fr.elikia.backend.bll.WorkshopRegistrationService;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.Member;
import fr.elikia.backend.bo.Workshop;
import fr.elikia.backend.bo.WorkshopRegistration;
import fr.elikia.backend.bo.enums.RegistrationStatus;
import fr.elikia.backend.dao.idao.IDAOMember;
import fr.elikia.backend.dao.idao.IDAOWorkshop;
import fr.elikia.backend.dao.idao.IDAOWorkshopRegistration;
import fr.elikia.backend.dto.WorkshopRegistrationDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WorkshopRegistrationService.
 */
@ExtendWith(MockitoExtension.class)
class WorkshopRegistrationServiceTest {

    // Mocked DAO and service dependencies
    @Mock
    private IDAOWorkshop idaoWorkshop;

    @Mock
    private IDAOWorkshopRegistration idaoWorkshopRegistration;

    @Mock
    private IDAOMember idaoMember;

    @Mock
    private EmailService emailService;

    // Service under test
    @InjectMocks
    private WorkshopRegistrationService workshopRegistrationService;

    /**
     * Build valid visitor registration data.
     */
    private WorkshopRegistrationDTO buildValidRegistrationDTO() {
        return new WorkshopRegistrationDTO(
                "Marie",
                "Dupont",
                "marie.dupont@example.com"
        );
    }

    /**
     * Verify that a visitor can register successfully for a workshop.
     */
    @Test
    void shouldRegisterVisitorToWorkshopSuccessfully() {
        // Arrange
        Long workshopId = 1L;

        Workshop workshop = new Workshop();

        WorkshopRegistrationDTO registrationDTO =
                buildValidRegistrationDTO();

        when(idaoWorkshop.findById(workshopId))
                .thenReturn(workshop);

        when(
                idaoWorkshopRegistration.existsByWorkshopAndEmail(
                        workshop,
                        registrationDTO.getEmail()
                )
        ).thenReturn(false);

        when(idaoWorkshopRegistration.create(any(WorkshopRegistration.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        LogicResult<Void> result =
                workshopRegistrationService.registerToWorkshop(
                        workshopId,
                        registrationDTO,
                        null
                );

        // Assert
        assertEquals("201", result.getCode());

        assertEquals(
                "Workshop registration created successfully",
                result.getMessage()
        );

        assertNull(result.getData());

        ArgumentCaptor<WorkshopRegistration> registrationCaptor =
                ArgumentCaptor.forClass(WorkshopRegistration.class);

        verify(idaoWorkshopRegistration)
                .create(registrationCaptor.capture());

        WorkshopRegistration createdRegistration =
                registrationCaptor.getValue();

        assertEquals(
                registrationDTO.getFirstName(),
                createdRegistration.getFirstName()
        );

        assertEquals(
                registrationDTO.getLastName(),
                createdRegistration.getLastName()
        );

        assertEquals(
                registrationDTO.getEmail(),
                createdRegistration.getEmail()
        );

        assertEquals(
                RegistrationStatus.PENDING,
                createdRegistration.getStatus()
        );

        assertEquals(
                workshop,
                createdRegistration.getWorkshop()
        );

        assertNull(createdRegistration.getMember());
        assertNotNull(createdRegistration.getRegistrationDate());

        verify(idaoWorkshop).findById(workshopId);

        verify(idaoWorkshopRegistration)
                .existsByWorkshopAndEmail(
                        workshop,
                        registrationDTO.getEmail()
                );

        verify(idaoWorkshopRegistration, never())
                .existsByWorkshopAndMember(any(), any());

        verify(idaoMember, never())
                .findByEmail(any());

        verify(emailService, never())
                .sendWorkshopRegistrationAcceptedEmail(
                        any(),
                        any(),
                        any()
                );

        verify(emailService, never())
                .sendWorkshopRegistrationRejectedEmail(
                        any(),
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void shouldReturnNotFoundWhenWorkshopDoesNotExist() {
        // Arrange
        Long workshopId = 1L;
        WorkshopRegistrationDTO registrationDTO = buildValidRegistrationDTO();

        when(idaoWorkshop.findById(workshopId))
                .thenReturn(null);

        // Act
        LogicResult<Void> result =
                workshopRegistrationService.registerToWorkshop(
                        workshopId,
                        registrationDTO,
                        null
                );

        // Assert
        assertEquals("404", result.getCode());
        assertEquals("Workshop not found", result.getMessage());
        assertNull(result.getData());

        verify(idaoWorkshop).findById(workshopId);

        verifyNoInteractions(
                idaoWorkshopRegistration,
                idaoMember,
                emailService
        );
    }

    @Test
    void shouldNotRegisterVisitorTwiceToSameWorkshop() {
        // Arrange
        Long workshopId = 1L;

        Workshop workshop = new Workshop();
        WorkshopRegistrationDTO registrationDTO = buildValidRegistrationDTO();

        when(idaoWorkshop.findById(workshopId))
                .thenReturn(workshop);

        when(idaoWorkshopRegistration.existsByWorkshopAndEmail(
                workshop,
                registrationDTO.getEmail()
        )).thenReturn(true);

        // Act
        LogicResult<Void> result =
                workshopRegistrationService.registerToWorkshop(
                        workshopId,
                        registrationDTO,
                        null
                );

        // Assert
        assertEquals("400", result.getCode());
        assertEquals(
                "This email is already registered for this workshop",
                result.getMessage()
        );
        assertNull(result.getData());

        verify(idaoWorkshop).findById(workshopId);

        verify(idaoWorkshopRegistration)
                .existsByWorkshopAndEmail(
                        workshop,
                        registrationDTO.getEmail()
                );

        verify(idaoWorkshopRegistration, never())
                .create(any(WorkshopRegistration.class));

        verify(idaoWorkshopRegistration, never())
                .existsByWorkshopAndMember(
                        any(Workshop.class),
                        any(Member.class)
                );

        verifyNoInteractions(idaoMember, emailService);
    }

    @Test
    void shouldApproveWorkshopRegistrationSuccessfully() {
        // Arrange
        Long registrationId = 10L;

        Workshop workshop = new Workshop();
        workshop.setTitle("Spring Boot Workshop");

        WorkshopRegistration registration = new WorkshopRegistration();
        registration.setFirstName("Iman");
        registration.setEmail("iman@example.com");
        registration.setStatus(RegistrationStatus.PENDING);
        registration.setWorkshop(workshop);

        when(idaoWorkshopRegistration.findById(registrationId))
                .thenReturn(registration);

        when(idaoWorkshopRegistration.update(registration))
                .thenReturn(registration);

        // Act
        LogicResult<Void> result =
                workshopRegistrationService.approveRegistration(registrationId);

        // Assert
        assertEquals("200", result.getCode());
        assertEquals(
                "Workshop registration approved successfully",
                result.getMessage()
        );
        assertNull(result.getData());

        assertEquals(
                RegistrationStatus.APPROVED,
                registration.getStatus()
        );

        verify(idaoWorkshopRegistration)
                .findById(registrationId);

        verify(idaoWorkshopRegistration)
                .update(registration);

        verify(emailService)
                .sendWorkshopRegistrationAcceptedEmail(
                        "iman@example.com",
                        "Iman",
                        "Spring Boot Workshop"
                );

        verifyNoInteractions(idaoWorkshop, idaoMember);
    }

    @Test
    void shouldRejectWorkshopRegistrationSuccessfully() {
        // Arrange
        Long registrationId = 10L;
        String refusalReason = "The workshop is already full";

        Workshop workshop = new Workshop();
        workshop.setTitle("Spring Boot Workshop");

        WorkshopRegistration registration = new WorkshopRegistration();
        registration.setFirstName("Iman");
        registration.setEmail("iman@example.com");
        registration.setStatus(RegistrationStatus.PENDING);
        registration.setWorkshop(workshop);

        when(idaoWorkshopRegistration.findById(registrationId))
                .thenReturn(registration);

        when(idaoWorkshopRegistration.update(registration))
                .thenReturn(registration);

        // Act
        LogicResult<Void> result =
                workshopRegistrationService.rejectRegistration(
                        registrationId,
                        refusalReason
                );

        // Assert
        assertEquals("200", result.getCode());
        assertEquals(
                "Workshop registration rejected successfully",
                result.getMessage()
        );
        assertNull(result.getData());

        assertEquals(
                RegistrationStatus.REJECTED,
                registration.getStatus()
        );

        verify(idaoWorkshopRegistration)
                .findById(registrationId);

        verify(idaoWorkshopRegistration)
                .update(registration);

        verify(emailService)
                .sendWorkshopRegistrationRejectedEmail(
                        "iman@example.com",
                        "Iman",
                        "Spring Boot Workshop",
                        refusalReason
                );

        verifyNoInteractions(idaoWorkshop, idaoMember);
    }
}