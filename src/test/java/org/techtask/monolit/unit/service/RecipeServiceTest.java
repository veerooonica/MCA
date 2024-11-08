package org.techtask.monolit.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.techtask.monolit.db.entity.RecipeEntity;
import org.techtask.monolit.db.repository.IngredientRepository;
import org.techtask.monolit.db.repository.RecipeRepository;
import org.techtask.monolit.dto.RecipeDTO;
import org.techtask.monolit.exception.NotFoundException;
import org.techtask.monolit.mapper.RecipeMapper;
import org.techtask.monolit.request.CreateOrUpdateRecipeRequest;
import org.techtask.monolit.service.RecipeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {
    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    public void getAllRecipes() {
        RecipeEntity entity = RecipeEntity.builder()
                .title("Pizza")
                .ingredients(new ArrayList<>())
                .build();
        RecipeDTO dto = RecipeDTO.builder()
                .title("Pizza")
                .ingredients(new ArrayList<>())
                .build();

        when(recipeRepository.findAll()).thenReturn(List.of(entity));
        when(recipeMapper.mapEntitiesToDtoList(any())).thenReturn(List.of(dto));

        List<RecipeDTO> availableRecipes = recipeService.getAllRecipes();

        assertNotNull(availableRecipes);
        assertEquals(1, availableRecipes.size());
        assertEquals("Pizza", availableRecipes.get(0).title());
    }

    @Test
    public void getRecipeByIdTest() {
        Long recipeId = 1L;
        RecipeEntity entity = RecipeEntity.builder()
                .id(recipeId)
                .title("Pizza")
                .ingredients(new ArrayList<>())
                .build();
        RecipeDTO dto = RecipeDTO.builder()
                .title("Pizza")
                .ingredients(new ArrayList<>())
                .build();

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(entity));
        when(recipeMapper.mapEntityToDto(entity)).thenReturn(dto);

        RecipeDTO returnedDto = recipeService.getRecipeById(recipeId);
        assertNotNull(returnedDto);
        assertEquals("Pizza", returnedDto.title());
    }

    @Test
    public void createRecipeTest() {
        CreateOrUpdateRecipeRequest request = CreateOrUpdateRecipeRequest.builder()
                .title("Pizza")
                .ingredients(new ArrayList<>())
                .build();
        RecipeEntity entity = RecipeEntity.builder()
                .id(1L)
                .title("Pizza")
                .ingredients(new ArrayList<>())
                .build();
        RecipeDTO dto = RecipeDTO.builder()
                .id(1L)
                .title("Pizza")
                .ingredients(new ArrayList<>())
                .build();

        when(recipeMapper.mapRequestToEntity(request)).thenReturn(entity);
        when(recipeRepository.save(any())).thenReturn(entity);
        when(recipeMapper.mapEntityToDto(entity)).thenReturn(dto);

        RecipeDTO savedDto = recipeService.createRecipe(request);
        assertNotNull(savedDto);
        assertEquals("Pizza", savedDto.title());
    }

    @Test
    public void updateRecipeTest() {
        Long recipeId = 1L;
        RecipeEntity existingEntity = RecipeEntity.builder()
                .id(recipeId)
                .title("Old Pizza")
                .ingredients(new ArrayList<>())
                .build();
        CreateOrUpdateRecipeRequest request = CreateOrUpdateRecipeRequest.builder()
                .title("Updated Pizza")
                .ingredients(new ArrayList<>())
                .build();
        RecipeDTO expectedDto = RecipeDTO.builder()
                .id(recipeId)
                .title("Updated Pizza")
                .ingredients(new ArrayList<>())
                .build();

        given(recipeRepository.findById(recipeId)).willReturn(Optional.of(existingEntity));
        given(recipeMapper.updateRecipe(existingEntity, request)).willReturn(existingEntity);
        given(recipeRepository.save(existingEntity)).willReturn(existingEntity);
        given(recipeMapper.mapEntityToDto(existingEntity)).willReturn(expectedDto);

        RecipeDTO updatedDto = recipeService.updateRecipe(recipeId, request);
        assertNotNull(updatedDto);
        assertEquals(recipeId, updatedDto.id());
        assertEquals("Updated Pizza", updatedDto.title());
    }

    @Test
    public void deleteRecipeTest() {
        Long recipeId = 1L;
        RecipeEntity entity = RecipeEntity.builder()
                .id(recipeId)
                .title("Pizza")
                .ingredients(new ArrayList<>())
                .build();

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(entity));

        assertDoesNotThrow(() -> recipeService.deleteRecipe(recipeId));
        verify(recipeRepository, times(1)).delete(entity);
    }

    @Test
    public void getRecipeByIdNotFoundTest() {
        Long recipeId = 1L;
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> recipeService.getRecipeById(recipeId));
        assertEquals("Recipe with id " + recipeId + " not found", exception.getMessage());
    }
}