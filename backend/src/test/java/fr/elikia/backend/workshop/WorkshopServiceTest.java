package fr.elikia.backend.workshop;

import fr.elikia.backend.bll.MediaService;
import fr.elikia.backend.bll.WorkshopService;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.enums.Visibility;
import fr.elikia.backend.bo.Workshop;
import fr.elikia.backend.dao.idao.IDAOWorkshop;
import fr.elikia.backend.dto.WorkshopDTO;
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
class WorkshopServiceTest {
    // MOCKED IDAO DEPENDENCIES
    @Mock
    private IDAOWorkshop idaoWorkshop;

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private WorkshopService workshopService;


    // Helper methods
    private WorkshopDTO buildValidWorkshopDTO() {
        WorkshopDTO dto = new WorkshopDTO();
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
    void shouldCreateWorkshopWithoutMediaSuccessfully() {
        // Arrange
        WorkshopDTO workshopDTO =buildValidWorkshopDTO();

        // Mock DAO create behavior
        doAnswer(invocation -> {
            Workshop workshop = invocation.getArgument(0);
            ReflectionTestUtils.setField(workshop, "workshopId", 1L);
            return null;
        }).when(idaoWorkshop).create(any(Workshop.class));

        when(idaoWorkshop.update(any(Workshop.class)))
                .thenAnswer(invocation ->invocation.getArgument(0));

        // Act
        LogicResult<Void> result =
                workshopService.createWorkshop(workshopDTO, null, null);

        // Assert
        assertEquals("201", result.getCode());
        assertEquals("Workshop created successfully with uploaded medias", result.getMessage());

        verify(idaoWorkshop).create(any(Workshop.class));
        verify(idaoWorkshop).update(any(Workshop.class));
        verifyNoInteractions(mediaService);
    }


    // Validation error case
    @Test
    void shouldFailWhenTitleIsBlank() {
        // Arrange
        WorkshopDTO workshopDTO = buildValidWorkshopDTO();
        workshopDTO.setTitle(" "); // invalid

        // Act
        LogicResult<Void> result =
                workshopService.createWorkshop(workshopDTO, null, null);

        // Assert
        assertEquals("400", result.getCode());
        assertEquals("The title is required", result.getMessage());

        verifyNoInteractions(idaoWorkshop);
        verifyNoInteractions(mediaService);
    }


    // Media failure propagation
    @Test
    void shouldFailWhenMediaCreationFails() {

        // Arrange
        WorkshopDTO workshopDTO = buildValidWorkshopDTO();

        MultipartFile file = mockImage();
        List<MultipartFile> files = List.of(file);

        doAnswer(invocation -> {
            Workshop workshop = invocation.getArgument(0);
            ReflectionTestUtils.setField(workshop, "workshopId", 1L);
            return null;
        }).when(idaoWorkshop).create(any(Workshop.class));

        when(mediaService.createMedia(
                any(),
                any(),
                any(),
                any(),
                any(),
                anyLong(),
                any()
        )).thenReturn(
                new LogicResult<>("400", "Invalid image", null)
        );

        // Act
        LogicResult<Void> result =
                workshopService.createWorkshop(workshopDTO, null, files);

        // Assert
        assertEquals("400", result.getCode());
        assertTrue(result.getMessage().startsWith("Invalid image"));

        verify(idaoWorkshop).create(any(Workshop.class));
        verify(mediaService).createMedia(
                any(),
                any(),
                any(),
                any(),
                any(),
                anyLong(),
                any()
        );
        verify(idaoWorkshop, never()).update(any());
    }

}
