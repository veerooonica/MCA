package org.techtask.monolit.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.techtask.monolit.db.entity.IngredientEntity;
import org.techtask.monolit.db.repository.IngredientRepository;
import org.techtask.monolit.db.repository.RecipeRepository;
import org.techtask.monolit.dto.IngredientDTO;
import org.techtask.monolit.exception.BadRequestException;
import org.techtask.monolit.exception.NotFoundException;
import org.techtask.monolit.mapper.IngredientMapper;
import org.techtask.monolit.request.CreateOrUpdateIngredientRequest;
import org.techtask.monolit.service.IngredientService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class IngredientServiceTest {
    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientMapper ingredientMapper;

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private IngredientService ingredientService;

    @Test
    public void getAllIngredients() {
        IngredientEntity entity = IngredientEntity.builder()
                .id(1L)
                .name("Sugar")
                .volume(100)
                .unit("g")
                .calories(387)
                .build();
        IngredientDTO dto = IngredientDTO.builder()
                .id(1L)
                .name("Sugar")
                .volume(100)
                .unit("g")
                .calories(387)
                .build();

        when(ingredientRepository.findAll()).thenReturn(List.of(entity));
        when(ingredientMapper.mapEntitiesToDtoList(any())).thenReturn(List.of(dto));

        List<IngredientDTO> availableIngredients = ingredientService.getAllIngredients();

        assertNotNull(availableIngredients);
        assertEquals(1, availableIngredients.size());
        assertEquals("Sugar", availableIngredients.get(0).name());
    }

    @Test
    public void getIngredientByIdTest() {
        Long ingredientId = 1L;
        IngredientEntity entity = IngredientEntity.builder()
                .id(ingredientId)
                .name("Sugar")
                .volume(100)
                .unit("g")
                .calories(387)
                .build();
        IngredientDTO dto = IngredientDTO.builder()
                .id(ingredientId)
                .name("Sugar")
                .volume(100)
                .unit("g")
                .calories(387)
                .build();

        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(entity));
        when(ingredientMapper.mapEntityToDto(entity)).thenReturn(dto);

        IngredientDTO returnedDto = ingredientService.getIngredientById(ingredientId);
        assertNotNull(returnedDto);
        assertEquals("Sugar", returnedDto.name());
    }

    @Test
    public void createIngredientTest() {
        CreateOrUpdateIngredientRequest request = CreateOrUpdateIngredientRequest.builder()
                .name("Sugar")
                .volume(100)
                .unit("g")
                .calories(387)
                .build();
        IngredientEntity entity = IngredientEntity.builder()
                .id(1L)
                .name("Sugar")
                .volume(100)
                .unit("g")
                .calories(387)
                .build();
        IngredientDTO dto = IngredientDTO.builder()
                .id(1L)
                .name("Sugar")
                .volume(100)
                .unit("g")
                .calories(387)
                .build();

        when(ingredientMapper.mapRequestToEntity(request)).thenReturn(entity);
        when(ingredientRepository.save(any())).thenReturn(entity);
        when(ingredientMapper.mapEntityToDto(entity)).thenReturn(dto);

        IngredientDTO savedDto = ingredientService.createIngredient(request);
        assertNotNull(savedDto);
        assertEquals("Sugar", savedDto.name());
    }

    @Test
    public void updateIngredientTest() {
        Long ingredientId = 1L;
        IngredientEntity existingEntity = IngredientEntity.builder()
                .id(ingredientId)
                .name("Sugar")
                .volume(100)
                .unit("g")
                .calories(387)
                .build();
        CreateOrUpdateIngredientRequest request = CreateOrUpdateIngredientRequest.builder()
                .name("Brown Sugar")
                .volume(120)
                .unit("g")
                .calories(387)
                .build();
        IngredientDTO expectedDto = IngredientDTO.builder()
                .id(ingredientId)
                .name("Brown Sugar")
                .volume(120)
                .unit("g")
                .calories(387)
                .build();

        given(ingredientRepository.findById(ingredientId)).willReturn(Optional.of(existingEntity));
        given(ingredientMapper.updateIngredient(existingEntity, request)).willReturn(existingEntity);
        given(ingredientRepository.save(existingEntity)).willReturn(existingEntity);
        given(ingredientMapper.mapEntityToDto(existingEntity)).willReturn(expectedDto);

        IngredientDTO updatedDto = ingredientService.updateIngredient(ingredientId, request);
        assertNotNull(updatedDto);
        assertEquals(ingredientId, updatedDto.id());
        assertEquals("Brown Sugar", updatedDto.name());
    }

    @Test
    public void deleteIngredientTest() {
        Long ingredientId = 1L;
        IngredientEntity entity = IngredientEntity.builder()
                .id(ingredientId)
                .name("Sugar")
                .volume(100)
                .unit("g")
                .calories(387)
                .build();

        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(entity));
        when(recipeRepository.existsByIngredientsId(ingredientId)).thenReturn(false); // Ингредиент не используется

        assertDoesNotThrow(() -> ingredientService.deleteIngredient(ingredientId));
        verify(ingredientRepository, times(1)).delete(entity);
    }

    @Test
    public void deleteIngredientNotFoundTest() {
        Long ingredientId = 1L;
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> ingredientService.deleteIngredient(ingredientId));
        assertEquals("Ingredient with id " + ingredientId + " not found", exception.getMessage());
    }

    @Test
    public void deleteIngredientUsedInRecipesTest() {
        Long ingredientId = 1L;
        IngredientEntity entity = IngredientEntity.builder()
                .id(ingredientId)
                .name("Sugar")
                .build();

        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(entity));
        when(recipeRepository.existsByIngredientsId(ingredientId)).thenReturn(true); // Ингредиент используется

        BadRequestException exception = assertThrows(BadRequestException.class, () -> ingredientService.deleteIngredient(ingredientId));
        assertEquals("Ingredient with id " + ingredientId + " cannot be deleted because it is still referenced in recipes.", exception.getMessage());
    }
}
