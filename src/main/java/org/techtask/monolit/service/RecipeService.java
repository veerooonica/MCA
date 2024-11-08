package org.techtask.monolit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.techtask.monolit.db.entity.IngredientEntity;
import org.techtask.monolit.db.entity.RecipeEntity;
import org.techtask.monolit.db.repository.IngredientRepository;
import org.techtask.monolit.db.repository.RecipeRepository;
import org.techtask.monolit.dto.RecipeDTO;
import org.techtask.monolit.exception.NotFoundException;
import org.techtask.monolit.mapper.RecipeMapper;
import org.techtask.monolit.request.CreateOrUpdateRecipeRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeMapper recipeMapper;

    public List<RecipeDTO> getAllRecipes() {
        return recipeMapper.mapEntitiesToDtoList(recipeRepository.findAll());
    }

    public RecipeDTO getRecipeById(Long id) {
        RecipeEntity entity = recipeRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Recipe with id " + id + " not found"));
        return recipeMapper.mapEntityToDto(entity);
    }

    public RecipeDTO createRecipe(final CreateOrUpdateRecipeRequest recipe) {
        RecipeEntity entity = recipeMapper.mapRequestToEntity(recipe);
        List<IngredientEntity> ingredientsToSave = checkIngredients(entity);
        ingredientRepository.saveAll(ingredientsToSave);
        entity.setIngredients(ingredientsToSave);
        return recipeMapper.mapEntityToDto(recipeRepository.save(entity));
    }

    public RecipeDTO updateRecipe(Long id, CreateOrUpdateRecipeRequest request) {
        RecipeEntity entity = recipeRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Recipe with id " + id + " not found"));
        List<IngredientEntity> existingIngredients = entity.getIngredients();

        RecipeEntity updatedEntity = recipeMapper.updateRecipe(entity, request);
        List<IngredientEntity> ingredientsToSave = checkIngredients(updatedEntity);
        updatedEntity.setIngredients(ingredientsToSave);

        ingredientRepository.saveAll(updatedEntity.getIngredients());
        return recipeMapper.mapEntityToDto(recipeRepository.save(updatedEntity));
    }

    public void deleteRecipe(Long id) {
        RecipeEntity entity = recipeRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Recipe with id " + id + " not found"));
        entity.getIngredients().clear();
        recipeRepository.delete(entity);
    }

    public List<IngredientEntity> checkIngredients(RecipeEntity entity) {
        List<IngredientEntity> existingIngredients = ingredientRepository.findAllById(
                entity.getIngredients().stream()
                        .map(IngredientEntity::getId)
                        .filter(Objects::nonNull)
                        .toList()
        );

        List<IngredientEntity> ingredientsToSave = new ArrayList<>(existingIngredients);

        for (IngredientEntity ingredient : entity.getIngredients()) {
            if (ingredient.getId() == null) {
                ingredientsToSave.add(ingredient);
            }
        }

        return ingredientsToSave;
    }
}
