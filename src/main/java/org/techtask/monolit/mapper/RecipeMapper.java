package org.techtask.monolit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.techtask.monolit.db.entity.RecipeEntity;
import org.techtask.monolit.dto.RecipeDTO;
import org.techtask.monolit.request.CreateOrUpdateRecipeRequest;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RecipeMapper {
    RecipeDTO mapEntityToDto(RecipeEntity recipe);

    List<RecipeDTO> mapEntitiesToDtoList(List<RecipeEntity> entities);

    @Mapping(target = "id", ignore = true)
    RecipeEntity mapRequestToEntity(CreateOrUpdateRecipeRequest request);

    @Mapping(target = "id", ignore = true)
    RecipeEntity updateRecipe(@MappingTarget RecipeEntity entity, CreateOrUpdateRecipeRequest request);
}
