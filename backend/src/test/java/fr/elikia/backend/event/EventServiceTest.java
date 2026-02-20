package fr.elikia.backend.event;

import fr.elikia.backend.bll.EventService;
import fr.elikia.backend.bll.MediaService;
import fr.elikia.backend.bo.*;
import fr.elikia.backend.bo.enums.Visibility;
import fr.elikia.backend.dao.idao.IDAOEvent;
import fr.elikia.backend.dto.EventDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    // MOCKED IDAO DEPENDENCIES
    @Mock
    private IDAOEvent idaoEvent;

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private EventService eventService;


    // Helper methods
    private EventDTO buildValidEventDTO() {
        EventDTO dto = new EventDTO();
        dto.setTitle("Festival international du conte");
        dto.setDescription("Un grand festival culturel réunissant conteurs, artistes et passionnés autour de la tradition orale");
        dto.setStartDate(LocalDateTime.now().plusDays(2));
        dto.setEndDate(LocalDateTime.now().plusDays(3));
        dto.setLocation("Centre Culturel de Yaounde");
        dto.setAddress("12 rue Rene Dumont, 75012 Paris");
        dto.setCapacity(200);
        dto.setVisibility(Visibility.PUBLIC);

        return dto;
    }


    private MultipartFile mockImage() {
        return new MockMultipartFile(
                "file",
                "bad.exe",
                "image/png",
                "fake-image-content".getBytes()
        );
    }


    // Success case
    @Test
    void shouldCreateEventWithoutMediaSuccessfully() {
        // Arrange
        EventDTO eventDTO =buildValidEventDTO();

        // Mock DAO create behavior
        doAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            ReflectionTestUtils.setField(event, "eventId", 1L);
            return null;
        }).when(idaoEvent).create(any(Event.class));

        when(idaoEvent.update(any(Event.class)))
                .thenAnswer(invocation ->invocation.getArgument(0));

        // Act
        LogicResult<Void> result =
                eventService.createEvent(eventDTO, null, null);

        // Assert
        assertEquals("201", result.getCode());
        assertEquals("Event created successfully with uploaded medias", result.getMessage());

        verify(idaoEvent).create(any(Event.class));
        verify(idaoEvent).update(any(Event.class));
        verifyNoInteractions(mediaService);
    }


    // Validation error case
    @Test
    void shouldFailWhenTitleIsBlank() {
        // Arrange
        EventDTO eventDTO = buildValidEventDTO();
        eventDTO.setTitle(" "); // invalid

        // Act
        LogicResult<Void> result =
                eventService.createEvent(eventDTO, null, null);

        // Assert
        assertEquals("400", result.getCode());
        assertEquals("The title is required", result.getMessage());

        verifyNoInteractions(idaoEvent);
        verifyNoInteractions(mediaService);
    }


    // Media failure propagation
    @Test
    void shouldFailWhenMediaCreationFails() {

        // Arrange
        EventDTO eventDTO = buildValidEventDTO();

        MultipartFile file = mockImage();
        List<MultipartFile> files = List.of(file);

        doAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            ReflectionTestUtils.setField(event, "eventId", 1L);
            return null;
        }).when(idaoEvent).create(any(Event.class));

        when(mediaService.createMedia(
                any(),
                any(),
                any(),
                any(),
                anyLong(),
                any(),
                any()
        )).thenReturn(
                new LogicResult<>("400", "Invalid image", null)
        );

        // Act
        LogicResult<Void> result =
                eventService.createEvent(eventDTO, null, files);

        // Assert
        assertEquals("400", result.getCode());
        assertTrue(result.getMessage().startsWith("Invalid image"));

        verify(idaoEvent).create(any(Event.class));
        verify(mediaService).createMedia(
                any(),
                any(),
                any(),
                any(),
                anyLong(),
                any(),
                any()
        );
        verify(idaoEvent, never()).update(any());
    }

}
