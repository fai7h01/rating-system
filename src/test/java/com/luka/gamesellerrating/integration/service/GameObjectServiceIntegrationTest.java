package com.luka.gamesellerrating.integration.service;

import com.luka.gamesellerrating.dto.GameObjectDTO;
import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.exception.GameObjectAlreadyExistsException;
import com.luka.gamesellerrating.repository.GameObjectRepository;
import com.luka.gamesellerrating.service.AuthenticationService;
import com.luka.gamesellerrating.service.GameObjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class GameObjectServiceIntegrationTest {

    @Autowired
    private GameObjectService gameObjectService;

    @Autowired
    private GameObjectRepository gameObjectRepository;

    @MockitoBean
    private AuthenticationService authenticationService;

    private UserDTO currentUser;

    @BeforeEach
    void setUp() {

        currentUser = new UserDTO();
        currentUser.setId(1L);
        currentUser.setUsername("testUser");
        currentUser.setEmail("test@example.com");


        Mockito.when(authenticationService.getLoggedInUser()).thenReturn(currentUser);
    }

    @Test
    void shouldSaveGameObjectSuccessfully() {

        GameObjectDTO inputDto = createValidGameObjectDTO();

        GameObjectDTO savedDto = gameObjectService.save(inputDto);

        Assertions.assertNotNull(savedDto);
        Assertions.assertNotNull(savedDto.getId());
        Assertions.assertEquals("Test Game", savedDto.getTitle());
        Assertions.assertEquals("This is a test game", savedDto.getText());
        Assertions.assertEquals(currentUser.getId(), savedDto.getSeller().getId());

        Assertions.assertTrue(gameObjectRepository.existsById(savedDto.getId()));
    }

    @Test
    void shouldThrowExceptionWhenSavingDuplicateGameObject() {

        GameObjectDTO inputDto = createValidGameObjectDTO();
        gameObjectService.save(inputDto);

        Assertions.assertThrows(GameObjectAlreadyExistsException.class, () -> {
            gameObjectService.save(inputDto);
        });
    }

    @Test
    void shouldSaveMultipleGameObjectsWithDifferentTitles() {
        GameObjectDTO firstDto = createValidGameObjectDTO();
        GameObjectDTO secondDto = createValidGameObjectDTO();
        secondDto.setTitle("Another Game");

        GameObjectDTO firstSaved = gameObjectService.save(firstDto);
        GameObjectDTO secondSaved = gameObjectService.save(secondDto);

        Assertions.assertNotNull(firstSaved.getId());
        Assertions.assertNotNull(secondSaved.getId());
        Assertions.assertNotEquals(firstSaved.getId(), secondSaved.getId());
    }

    private GameObjectDTO createValidGameObjectDTO() {
        GameObjectDTO gameObjectDTO = new GameObjectDTO();
        gameObjectDTO.setTitle("Test Game");
        gameObjectDTO.setText("This is a test game");
        return gameObjectDTO;
    }
}