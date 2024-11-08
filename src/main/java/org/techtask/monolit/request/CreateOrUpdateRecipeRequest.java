package org.techtask.monolit.request;

import lombok.Builder;
import org.techtask.monolit.db.entity.RecipeDifficulty;
import org.techtask.monolit.dto.IngredientDTO;

import java.util.List;

@Builder
public record CreateOrUpdateRecipeRequest(String title,
                                          String description,
                                          String instruction,
                                          List<IngredientDTO> ingredients,
                                          RecipeDifficulty difficulty,
                                          String image,
                                          int cookTime) {
}
