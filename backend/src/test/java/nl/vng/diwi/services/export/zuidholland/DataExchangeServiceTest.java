package nl.vng.diwi.services.export.zuidholland;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.DataExchange;
import nl.vng.diwi.dal.entities.DataExchangeState;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.models.DataExchangeModel;
import nl.vng.diwi.services.DataExchangeService;
import nl.vng.diwi.services.export.ArcGisProjectExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.ZonedDateTime;
import java.util.UUID;

import static nl.vng.diwi.dal.entities.DataExchangeType.ESRI_ZUID_HOLLAND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class DataExchangeServiceTest {

    private static final String TEST_NAME = "Test Name";
    private static final String TEST_API_KEY = "TestApiKey";
    private static final String CLIENT_ID = "ClientId123";
    private static final String PROJECT_URL = "http://project-url.test";


    private VngRepository mockRepo;
    private DataExchangeService service;
    private DataExchangeModel mockModel;
    private DataExchange mockDataExchange;
    private ArcGisProjectExporter mockArcGisProjectExporter;

    @BeforeEach
    void setUp() {
        mockRepo = mock(VngRepository.class);
        mockArcGisProjectExporter = new ArcGisProjectExporter();
        service = new DataExchangeService(mockArcGisProjectExporter);
        mockModel = mock(DataExchangeModel.class);
        mockDataExchange = mock(DataExchange.class);
    }

    @Test
    void shouldCreateDataExchangeSet() {
        //Given
        UUID dataExchangeUuid = UUID.randomUUID();
        UUID loggedUserUuid = UUID.randomUUID();
        ZonedDateTime zdtNow = ZonedDateTime.now();
        boolean isUpdate = false;

        when(mockModel.getName()).thenReturn(TEST_NAME);
        when(mockModel.getType()).thenReturn(ESRI_ZUID_HOLLAND);
        when(mockModel.getApiKey()).thenReturn(TEST_API_KEY);
        when(mockModel.getClientId()).thenReturn(CLIENT_ID);
        when(mockModel.getProjectUrl()).thenReturn(PROJECT_URL);

        User mockUser = mock(User.class);

        when(mockRepo.getReferenceById(DataExchange.class, dataExchangeUuid)).thenReturn(mockDataExchange);
        when(mockRepo.getReferenceById(User.class, loggedUserUuid)).thenReturn(mockUser);

        //When
        service.createDataExchangeState(mockRepo, dataExchangeUuid, mockModel, zdtNow, loggedUserUuid, isUpdate);

        //Then
        ArgumentCaptor<DataExchangeState> stateCaptor = ArgumentCaptor.forClass(DataExchangeState.class);
        verify(mockRepo).persist(stateCaptor.capture());

        DataExchangeState capturedState = stateCaptor.getValue();

        assertNotNull(capturedState);
        assertFalse(capturedState.getValid());
        assertEquals(mockDataExchange, capturedState.getDataExchange());
        assertEquals(TEST_NAME, capturedState.getName());
        assertEquals(ESRI_ZUID_HOLLAND, capturedState.getType());
        assertEquals(TEST_API_KEY, capturedState.getApiKey());
        assertEquals(CLIENT_ID, capturedState.getClientId());
        assertEquals(loggedUserUuid, capturedState.getUserId());
        assertEquals(PROJECT_URL, capturedState.getProjectUrl());
        assertEquals(zdtNow, capturedState.getChangeStartDate());
        assertEquals(mockUser, capturedState.getCreateUser());

        verify(mockRepo).getReferenceById(DataExchange.class, dataExchangeUuid);
        verify(mockRepo).getReferenceById(User.class, loggedUserUuid);
        verifyNoMoreInteractions(mockRepo);

    }


    @Test
    void testCreateDataExchangeStateWithIsUpdateTrue() {
        //Given
        UUID dataExchangeUuid = UUID.randomUUID();
        UUID loggedUserUuid = UUID.randomUUID();
        ZonedDateTime zdtNow = ZonedDateTime.now();
        boolean isUpdate = true;

        when(mockModel.getName()).thenReturn(TEST_NAME);
        when(mockModel.getType()).thenReturn(ESRI_ZUID_HOLLAND);
        when(mockModel.getApiKey()).thenReturn(TEST_API_KEY);
        when(mockModel.getClientId()).thenReturn(CLIENT_ID);
        when(mockModel.getProjectUrl()).thenReturn(PROJECT_URL);

        User mockUser = mock(User.class);

        when(mockRepo.getReferenceById(DataExchange.class, dataExchangeUuid)).thenReturn(mockDataExchange);
        when(mockRepo.getReferenceById(User.class, loggedUserUuid)).thenReturn(mockUser);

        //When
        service.createDataExchangeState(mockRepo, dataExchangeUuid, mockModel, zdtNow, loggedUserUuid, isUpdate);

        ArgumentCaptor<DataExchangeState> stateCaptor = ArgumentCaptor.forClass(DataExchangeState.class);
        verify(mockRepo).persist(stateCaptor.capture());

        DataExchangeState capturedState = stateCaptor.getValue();

        //Then
        assertNotNull(capturedState);
        assertEquals(mockDataExchange, capturedState.getDataExchange());
        assertEquals(TEST_NAME, capturedState.getName());
        assertEquals(ESRI_ZUID_HOLLAND, capturedState.getType());
        assertEquals(TEST_API_KEY, capturedState.getApiKey());
        assertEquals(CLIENT_ID, capturedState.getClientId());
        assertEquals(loggedUserUuid, capturedState.getUserId());
        assertEquals(PROJECT_URL, capturedState.getProjectUrl());
        assertEquals(zdtNow, capturedState.getChangeStartDate());
        assertEquals(mockUser, capturedState.getCreateUser());

        verify(mockRepo).getReferenceById(DataExchange.class, dataExchangeUuid);
        verify(mockRepo).getReferenceById(User.class, loggedUserUuid);
        verifyNoMoreInteractions(mockRepo);
    }

}
