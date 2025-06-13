package at.technikumwien.backend.controller;

import at.technikumwien.backend.service.EnergyService;
import at.technikumwien.database.entity.CurrentEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class EnergyControllerTest {

    @Mock
    private EnergyService energyService;

    @InjectMocks
    private EnergyController energyController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // 1. ObjectMapper für Java-Time konfigurieren
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 2. Jackson-Converter mit diesem Mapper anlegen
        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter(mapper);

        // 3. MockMvc mit Controller und Converter initialisieren
        mockMvc = MockMvcBuilders
                .standaloneSetup(energyController)
                .setMessageConverters(converter)
                .build();
    }

    @Test
    void getCurrentData_returnsJsonArray_andDelegatesToService() throws Exception {
        // Arrange
        LocalDateTime now = LocalDateTime.of(2025, 1, 10, 14, 0);
        CurrentEntry entry = new CurrentEntry();
        entry.setHour(now);
        entry.setCommunityDepleted(100.00);
        entry.setGridPortion(5.63);

        when(energyService.getCurrentData()).thenReturn(List.of(entry));

        // Act & Assert
        mockMvc.perform(get("/energy/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hour").value("2025-01-10T14:00:00"))
                .andExpect(jsonPath("$[0].communityDepleted").value(100.00))
                .andExpect(jsonPath("$[0].gridPortion").value(5.63));

        // Verify delegation
        verify(energyService).getCurrentData();
    }
}
