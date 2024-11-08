package org.techtask.monolit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.techtask.monolit.db.entity.IngredientEntity;
import org.techtask.monolit.db.repository.IngredientRepository;
import org.techtask.monolit.db.repository.RecipeRepository;
import org.techtask.monolit.dto.IngredientDTO;
import org.techtask.monolit.exception.BadRequestException;
import org.techtask.monolit.exception.NotFoundException;
import org.techtask.monolit.mapper.IngredientMapper;
import org.techtask.monolit.request.CreateOrUpdateIngredientRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;
    private final RecipeRepository recipeRepository;

    public List<IngredientDTO> getAllIngredients() {
        return ingredientMapper.mapEntitiesToDtoList(ingredientRepository.findAll());
    }

    public IngredientDTO getIngredientById(Long id) {
        IngredientEntity entity = ingredientRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Ingredient with id " + id + " not found"));
        return ingredientMapper.mapEntityToDto(entity);
    }

    public IngredientDTO createIngredient(CreateOrUpdateIngredientRequest recipe) {
        IngredientEntity entity = ingredientMapper.mapRequestToEntity(recipe);
        return ingredientMapper.mapEntityToDto(ingredientRepository.save(entity));
    }

    public IngredientDTO updateIngredient(Long id, CreateOrUpdateIngredientRequest request) {
        IngredientEntity entity = ingredientRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Ingredient with id " + id + " not found"));
        IngredientEntity updatedEntity = ingredientMapper.updateIngredient(entity, request);
        return ingredientMapper.mapEntityToDto(ingredientRepository.save(updatedEntity));
    }

    public void deleteIngredient(Long id) {
        IngredientEntity entity = ingredientRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Ingredient with id " + id + " not found"));

        if (isIngredientUsedInRecipes(id)) {
            throw new BadRequestException("Ingredient with id " + id + " cannot be deleted because it is still referenced in recipes.");
        }

        ingredientRepository.delete(entity);
    }

    private boolean isIngredientUsedInRecipes(Long ingredientId) {
        return recipeRepository.existsByIngredientsId(ingredientId);
    }

    public List<IngredientEntity> findEntitiesByIdsIn(Collection<Long> ids) {
        List<IngredientEntity> categoryEntities = ingredientRepository.findAllById(ids);
        List<Long> notExistingIds = new ArrayList<>();
        categoryEntities.forEach(categoryEntity -> {
            if (!ids.contains(categoryEntity.getId())) {
                notExistingIds.add(categoryEntity.getId());
            }
        });
        if (!notExistingIds.isEmpty()) {
            throw new NotFoundException("Recipes " + notExistingIds + " not found");
        }
        return categoryEntities;
    }
}
