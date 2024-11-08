package org.techtask.monolit.request;

import lombok.Builder;

@Builder
public record CreateOrUpdateIngredientRequest(String name,
                                              int volume,
                                              String unit,
                                              int calories) {
}
