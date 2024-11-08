package org.techtask.monolit.unit.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.techtask.monolit.db.entity.IngredientEntity;
import org.techtask.monolit.db.entity.RecipeEntity;
import org.techtask.monolit.db.repository.RecipeRepository;
import org.techtask.monolit.db.repository.IngredientRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ActiveProfiles("test")
public class RecipeRepositoryTest {
    @Autowired
    private final RecipeRepository recipeRepository;

    @Autowired
    private final IngredientRepository ingredientRepository;

    @Test
    public void saveRecipeTest() {
        IngredientEntity ingredient = IngredientEntity.builder()
                .name("Sugar").volume(100).unit("g").calories(387).build();
        ingredientRepository.save(ingredient);

        RecipeEntity recipe = RecipeEntity.builder()
                .title("Cake")
                .ingredients(List.of(ingredient))
                .build();
        RecipeEntity savedRecipe = recipeRepository.save(recipe);
        assertNotNull(savedRecipe.getId());
    }

    @Test
    public void getRecipeTest() {
        IngredientEntity ingredient = IngredientEntity.builder()
                .name("Sugar").volume(100).unit("g").calories(387).build();
        ingredientRepository.save(ingredient);

        RecipeEntity recipe = RecipeEntity.builder()
                .title("Cake")
                .ingredients(List.of(ingredient))
                .build();
        RecipeEntity savedRecipe = recipeRepository.save(recipe);

        RecipeEntity retrievedRecipe = recipeRepository.findById(savedRecipe.getId()).get();
        assertEquals("Cake", retrievedRecipe.getTitle());
    }

    @Test
    public void readRecipesTest() {
        IngredientEntity ingredient1 = IngredientEntity.builder()
                .name("Sugar").volume(100).unit("g").calories(387).build();
        IngredientEntity ingredient2 = IngredientEntity.builder()
                .name("Flour").volume(200).unit("g").calories(364).build();
        ingredientRepository.save(ingredient1);
        ingredientRepository.save(ingredient2);

        RecipeEntity recipe1 = RecipeEntity.builder()
                .title("Cake")
                .ingredients(List.of(ingredient1))
                .build();
        RecipeEntity recipe2 = RecipeEntity.builder()
                .title("Bread")
                .ingredients(List.of(ingredient2))
                .build();
        recipeRepository.saveAll(List.of(recipe1, recipe2));

        List<RecipeEntity> recipes = recipeRepository.findAll();
        assertEquals(2, recipes.size());
    }

    @Test
    public void updateRecipeTest() {
        IngredientEntity ingredient = IngredientEntity.builder()
                .name("Sugar").volume(100).unit("g").calories(387).build();
        ingredientRepository.save(ingredient);

        RecipeEntity recipe = RecipeEntity.builder()
                .title("Cake")
                .ingredients(new ArrayList<>(List.of(ingredient)))
                .build();
        RecipeEntity savedRecipe = recipeRepository.save(recipe);

        RecipeEntity retrievedRecipe = recipeRepository.findById(savedRecipe.getId()).get();
        retrievedRecipe.setTitle("Chocolate Cake");
        RecipeEntity updatedRecipe = recipeRepository.save(retrievedRecipe);
        assertEquals("Chocolate Cake", updatedRecipe.getTitle());
    }

    @Test
    public void deleteRecipeTest() {
        IngredientEntity ingredient = IngredientEntity.builder()
                .name("Sugar").volume(100).unit("g").calories(387).build();
        ingredientRepository.save(ingredient);

        RecipeEntity recipe = RecipeEntity.builder()
                .title("Cake")
                .ingredients(List.of(ingredient))
                .build();
        RecipeEntity savedRecipe = recipeRepository.save(recipe);
        recipeRepository.deleteById(savedRecipe.getId());

        Optional<RecipeEntity> recipeOptional = recipeRepository.findById(savedRecipe.getId());
        assertThat(recipeOptional).isEmpty();
    }

    @Test
    public void existsByIngredientsIdTest() {
        IngredientEntity ingredient = IngredientEntity.builder()
                .name("Sugar").volume(100).unit("g").calories(387).build();
        ingredientRepository.save(ingredient);

        RecipeEntity recipe = RecipeEntity.builder()
                .title("Cake")
                .ingredients(List.of(ingredient))
                .build();
        recipeRepository.save(recipe);

        boolean exists = recipeRepository.existsByIngredientsId(ingredient.getId());
        assertThat(exists).isTrue();

        boolean notExists = recipeRepository.existsByIngredientsId(999L);
        assertThat(notExists).isFalse();
    }
}