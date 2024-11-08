package org.techtask.monolit.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.techtask.monolit.db.entity.IngredientEntity;
import org.techtask.monolit.dto.IngredientDTO;
import org.techtask.monolit.request.CreateOrUpdateIngredientRequest;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface IngredientMapper {
    IngredientDTO mapEntityToDto(IngredientEntity ingredient);

    List<IngredientDTO> mapEntitiesToDtoList(List<IngredientEntity> entities);

    @Mapping(target = "id", ignore = true)
    IngredientEntity mapRequestToEntity(CreateOrUpdateIngredientRequest request);

    @Mapping(target = "id", ignore = true)
    IngredientEntity updateIngredient(@MappingTarget IngredientEntity entity, CreateOrUpdateIngredientRequest request);
}
