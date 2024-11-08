package org.techtask.monolit.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.techtask.monolit.controller.IngredientController;
import org.techtask.monolit.dto.IngredientDTO;
import org.techtask.monolit.request.CreateOrUpdateIngredientRequest;
import org.techtask.monolit.service.IngredientService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(IngredientController.class)
public class IngredientControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngredientService ingredientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllIngredientsTest() throws Exception {
        IngredientDTO ingredient1 = IngredientDTO.builder()
                .id(1L).name("Sugar").volume(100).unit("g").calories(387)
                .build();
        IngredientDTO ingredient2 = IngredientDTO.builder()
                .id(2L).name("Flour").volume(200).unit("g").calories(364)
                .build();
        List<IngredientDTO> ingredientsList = List.of(ingredient1, ingredient2);
        given(ingredientService.getAllIngredients()).willReturn(ingredientsList);

        ResultActions response = mockMvc.perform(get("/ingredients"));
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()").value(ingredientsList.size()));
    }

    @Test
    public void getIngredientByIdTest() throws Exception {
        Long ingredientId = 1L;
        IngredientDTO ingredient = IngredientDTO.builder()
                .id(ingredientId).name("Sugar").volume(100).unit("g").calories(387)
                .build();
        given(ingredientService.getIngredientById(ingredientId)).willReturn(ingredient);

        ResultActions response = mockMvc.perform(get("/ingredients/{id}", ingredientId));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(ingredientId))
                .andExpect(jsonPath("$.name").value(ingredient.name()))
                .andExpect(jsonPath("$.volume").value(ingredient.volume()))
                .andExpect(jsonPath("$.unit").value(ingredient.unit()))
                .andExpect(jsonPath("$.calories").value(ingredient.calories()));
    }

    @Test
    public void addIngredientTest() throws Exception {
        CreateOrUpdateIngredientRequest request = CreateOrUpdateIngredientRequest.builder()
                .name("New Ingredient").volume(150).unit("g").calories(600)
                .build();

        IngredientDTO createdIngredient = IngredientDTO.builder()
                .id(1L).name("New Ingredient").volume(150).unit("g").calories(600)
                .build();
        given(ingredientService.createIngredient(request)).willReturn(createdIngredient);

        ResultActions response = mockMvc.perform(post("/ingredients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(createdIngredient.id()))
                .andExpect(jsonPath("$.name").value(createdIngredient.name()))
                .andExpect(jsonPath("$.volume").value(createdIngredient.volume()))
                .andExpect(jsonPath("$.unit").value(createdIngredient.unit()))
                .andExpect(jsonPath("$.calories").value(createdIngredient.calories()));
    }

    @Test
    public void updateIngredientTest() throws Exception {
        Long ingredientId = 1L;
        CreateOrUpdateIngredientRequest request = CreateOrUpdateIngredientRequest.builder()
                .name("Updated Ingredient").volume(200).unit("g").calories(500)
                .build();
        IngredientDTO updatedIngredient = IngredientDTO.builder()
                .id(ingredientId).name("Updated Ingredient").volume(200).unit("g").calories(500)
                .build();
        given(ingredientService.updateIngredient(ingredientId, request)).willReturn(updatedIngredient);

        ResultActions response = mockMvc.perform(put("/ingredients/{id}", ingredientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name").value(updatedIngredient.name()))
                .andExpect(jsonPath("$.volume").value(updatedIngredient.volume()))
                .andExpect(jsonPath("$.unit").value(updatedIngredient.unit()))
                .andExpect(jsonPath("$.calories").value(updatedIngredient.calories()));
    }

    @Test
    public void deleteIngredientTest() throws Exception {
        Long ingredientId = 1L;
        doNothing().when(ingredientService).deleteIngredient(ingredientId);

        mockMvc.perform(delete("/ingredients/{id}", ingredientId))
                .andExpect(status().isOk())
                .andDo(print());

        verify(ingredientService, times(1)).deleteIngredient(ingredientId);
    }
}