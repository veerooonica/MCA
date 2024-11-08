package org.techtask.monolit.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.techtask.monolit.db.entity.IngredientEntity;
import org.techtask.monolit.db.repository.IngredientRepository;
import org.techtask.monolit.request.CreateOrUpdateIngredientRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class IngredientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IngredientRepository ingredientRepository;

    @AfterEach
    void cleanUp() {
        ingredientRepository.deleteAll();
    }

    @Test
    void postRequestTestSuccess() throws Exception {
        CreateOrUpdateIngredientRequest request = CreateOrUpdateIngredientRequest.builder()
                .name("ingr1").volume(2).unit("un").calories(2).build();

        ResultActions resultActions = mockMvc.perform(post("/ingredients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        List<IngredientEntity> recipeEntityList = ingredientRepository.findAll();
        assertEquals(1, recipeEntityList.size());
        IngredientEntity recipeEntity = recipeEntityList.get(0);
        assertNotNull(recipeEntity.getId());
        assertEquals(request.name(), recipeEntity.getName());
        assertEquals(request.volume(), recipeEntity.getVolume());
        assertEquals(request.unit(), recipeEntity.getUnit());
        assertEquals(request.calories(), recipeEntity.getCalories());

        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(recipeEntity.getId()))
                .andExpect(jsonPath("$.name").value(recipeEntity.getName()))
                .andExpect(jsonPath("$.volume").value(recipeEntity.getVolume()))
                .andExpect(jsonPath("$.unit").value(recipeEntity.getUnit()))
                .andExpect(jsonPath("$.calories").value(recipeEntity.getCalories()));
    }

    @Test
    public void getIngredientRequestTestSuccess() throws Exception {
        List<CreateOrUpdateIngredientRequest> requestList = List.of(
                CreateOrUpdateIngredientRequest.builder()
                        .name("ingr1")
                        .volume(2)
                        .unit("un")
                        .calories(2)
                        .build(),
                CreateOrUpdateIngredientRequest.builder()
                        .name("ingr2")
                        .volume(3)
                        .unit("kg")
                        .calories(5)
                        .build()
        );

        for (CreateOrUpdateIngredientRequest request : requestList) {
            mockMvc.perform(post("/ingredients")  // Изменено на /ingredients
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(200));
        }

        ResultActions resultActions = mockMvc.perform(get("/ingredients")  // Изменено на /ingredients
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));

        List<IngredientEntity> ingredientEntityList = ingredientRepository.findAll();  // Изменено на ingredientEntityList
        assertEquals(requestList.size(), ingredientEntityList.size());

        for (int i = 0; i < requestList.size(); i++) {
            assertNotNull(ingredientEntityList.get(i).getId());
            assertEquals(requestList.get(i).name(), ingredientEntityList.get(i).getName());
            assertEquals(requestList.get(i).volume(), ingredientEntityList.get(i).getVolume());
            assertEquals(requestList.get(i).unit(), ingredientEntityList.get(i).getUnit());
            assertEquals(requestList.get(i).calories(), ingredientEntityList.get(i).getCalories());

            resultActions.andDo(print())
                    .andExpect(jsonPath("$[" + i + "].name").value(requestList.get(i).name()))
                    .andExpect(jsonPath("$[" + i + "].volume").value(requestList.get(i).volume()))
                    .andExpect(jsonPath("$[" + i + "].unit").value(requestList.get(i).unit()))
                    .andExpect(jsonPath("$[" + i + "].calories").value(requestList.get(i).calories()));
        }
    }

    @Test
    void putIngredientRequestTestSuccess() throws Exception {
        IngredientEntity ingredientEntity = IngredientEntity.builder()
                .name("ingr1")
                .volume(2)
                .unit("un")
                .calories(2)
                .build();

        ingredientRepository.save(ingredientEntity);

        CreateOrUpdateIngredientRequest request = CreateOrUpdateIngredientRequest.builder()
                .name("updated_ingr")
                .volume(3)
                .unit("kg")
                .calories(5)
                .build();

        ResultActions resultActions = mockMvc.perform(put("/ingredients/{id}", ingredientEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        List<IngredientEntity> ingredientEntityList = ingredientRepository.findAll();
        assertEquals(1, ingredientEntityList.size());
        IngredientEntity updatedIngredientEntity = ingredientEntityList.get(0);
        assertEquals(ingredientEntity.getId(), updatedIngredientEntity.getId());
        assertEquals(request.name(), updatedIngredientEntity.getName());
        assertEquals(request.volume(), updatedIngredientEntity.getVolume());
        assertEquals(request.unit(), updatedIngredientEntity.getUnit());
        assertEquals(request.calories(), updatedIngredientEntity.getCalories());

        resultActions.andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(updatedIngredientEntity.getId()))
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.volume").value(request.volume()))
                .andExpect(jsonPath("$.unit").value(request.unit()))
                .andExpect(jsonPath("$.calories").value(request.calories()));
    }

    @Test
    void putIngredientRequestTestEntityNotFound() throws Exception {
        IngredientEntity ingredientEntity = IngredientEntity.builder()
                .name("ingr1")
                .volume(2)
                .unit("un")
                .calories(2)
                .build();

        ingredientRepository.save(ingredientEntity);

        CreateOrUpdateIngredientRequest request = CreateOrUpdateIngredientRequest.builder()
                .name("updated_ingr")
                .volume(3)
                .unit("kg")
                .calories(5)
                .build();

        ResultActions resultActions = mockMvc.perform(put("/ingredients/{id}", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(404));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(responseBody.contains("Ingredient with id 3 not found"));
    }

    @Test
    void deleteIngredientRequestTestSuccess() throws Exception {
        IngredientEntity ingredientEntity = IngredientEntity.builder()
                .name("ingr1")
                .volume(2)
                .unit("un")
                .calories(2)
                .build();

        ingredientRepository.save(ingredientEntity);
        List<IngredientEntity> ingredientEntityList = ingredientRepository.findAll();
        assertEquals(1, ingredientEntityList.size());
        assertEquals(ingredientEntity.getId(), ingredientEntityList.get(0).getId());

        mockMvc.perform(delete("/ingredients/{id}", ingredientEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteIngredientRequestTestFail() throws Exception {
        IngredientEntity ingredientEntity = IngredientEntity.builder()
                .name("ingr1")
                .volume(2)
                .unit("un")
                .calories(2)
                .build();

        ingredientRepository.save(ingredientEntity);
        ResultActions resultActions = mockMvc.perform(delete("/ingredients/{id}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(404));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(responseBody.contains("Ingredient with id 5 not found"));
    }

}