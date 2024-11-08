package org.techtask.monolit.dto;

import lombok.Builder;

@Builder
public record IngredientDTO(Long id,
                            String name,
                            int volume,
                            String unit,
                            int calories) {
}
