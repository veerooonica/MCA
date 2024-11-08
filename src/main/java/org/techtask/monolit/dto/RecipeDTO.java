package org.techtask.monolit.dto;

import lombok.Builder;
import org.techtask.monolit.db.entity.RecipeDifficulty;

import java.util.List;

@Builder
public record RecipeDTO(Long id,
                        String title,
                        String description,
                        String instruction,
                        List<IngredientDTO> ingredients,
                        RecipeDifficulty difficulty,
                        String image,
                        int cookTime) {
}
