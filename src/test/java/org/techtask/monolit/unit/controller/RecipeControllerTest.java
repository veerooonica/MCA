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
import org.techtask.monolit.controller.RecipeController;
import org.techtask.monolit.db.entity.RecipeDifficulty;
import org.techtask.monolit.dto.RecipeDTO;
import org.techtask.monolit.request.CreateOrUpdateRecipeRequest;
import org.techtask.monolit.service.RecipeService;

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
@WebMvcTest(RecipeController.class)
public class RecipeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllRecipesTest() throws Exception {
        RecipeDTO recipe1 = RecipeDTO.builder()
                .id(1L).title("Pasta").description("Delicious pasta recipe")
                .instruction("Boil and serve").difficulty(RecipeDifficulty.EASY)
                .image("pasta.jpg").cookTime(30)
                .build();
        RecipeDTO recipe2 = RecipeDTO.builder()
                .id(2L).title("Salad").description("Fresh salad recipe")
                .instruction("Mix and serve").difficulty(RecipeDifficulty.EASY)
                .image("salad.jpg").cookTime(15)
                .build();
        List<RecipeDTO> recipesList = List.of(recipe1, recipe2);
        given(recipeService.getAllRecipes()).willReturn(recipesList);

        ResultActions response = mockMvc.perform(get("/recipes"));
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()").value(recipesList.size()));
    }

    @Test
    public void getRecipeByIdTest() throws Exception {
        Long recipeId = 1L;
        RecipeDTO recipe = RecipeDTO.builder()
                .id(recipeId).title("Pasta").description("Delicious pasta recipe")
                .instruction("Boil and serve").difficulty(RecipeDifficulty.EASY)
                .image("pasta.jpg").cookTime(30)
                .build();
        given(recipeService.getRecipeById(recipeId)).willReturn(recipe);

        ResultActions response = mockMvc.perform(get("/recipes/{id}", recipeId));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(recipeId))
                .andExpect(jsonPath("$.title").value(recipe.title()))
                .andExpect(jsonPath("$.description").value(recipe.description()))
                .andExpect(jsonPath("$.instruction").value(recipe.instruction()))
                .andExpect(jsonPath("$.difficulty").value(recipe.difficulty().name()))
                .andExpect(jsonPath("$.image").value(recipe.image()))
                .andExpect(jsonPath("$.cookTime").value(recipe.cookTime()));
    }

    @Test
    public void addRecipeTest() throws Exception {
        CreateOrUpdateRecipeRequest request = CreateOrUpdateRecipeRequest.builder()
                .title("New Recipe").description("New recipe description")
                .instruction("Prepare and serve").difficulty(RecipeDifficulty.EASY)
                .image("new_recipe.jpg").cookTime(20)
                .build();

        RecipeDTO createdRecipe = RecipeDTO.builder()
                .id(1L).title("New Recipe").description("New recipe description")
                .instruction("Prepare and serve").difficulty(RecipeDifficulty.EASY)
                .image("new_recipe.jpg").cookTime(20)
                .build();
        given(recipeService.createRecipe(request)).willReturn(createdRecipe);

        ResultActions response = mockMvc.perform(post("/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(createdRecipe.id()))
                .andExpect(jsonPath("$.title").value(createdRecipe.title()))
                .andExpect(jsonPath("$.description").value(createdRecipe.description()))
                .andExpect(jsonPath("$.instruction").value(createdRecipe.instruction()))
                .andExpect(jsonPath("$.difficulty").value(createdRecipe.difficulty().name()))
                .andExpect(jsonPath("$.image").value(createdRecipe.image()))
                .andExpect(jsonPath("$.cookTime").value(createdRecipe.cookTime()));
    }

    @Test
    public void updateRecipeTest() throws Exception {
        Long recipeId = 1L;
        CreateOrUpdateRecipeRequest request = CreateOrUpdateRecipeRequest.builder()
                .title("Updated Recipe").description("Updated description")
                .instruction("Prepare and serve").difficulty(RecipeDifficulty.EASY)
                .image("updated_recipe.jpg").cookTime(25)
                .build();
        RecipeDTO updatedRecipe = RecipeDTO.builder()
                .id(recipeId).title("Updated Recipe").description("Updated description")
                .instruction("Prepare and serve").difficulty(RecipeDifficulty.EASY)
                .image("updated_recipe.jpg").cookTime(25)
                .build();
        given(recipeService.updateRecipe(recipeId, request)).willReturn(updatedRecipe);

        ResultActions response = mockMvc.perform(put("/recipes/{id}", recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title").value(updatedRecipe.title()))
                .andExpect(jsonPath("$.description").value(updatedRecipe.description()))
                .andExpect(jsonPath("$.instruction").value(updatedRecipe.instruction()))
                .andExpect(jsonPath("$.difficulty").value(updatedRecipe.difficulty().name()))
                .andExpect(jsonPath("$.image").value(updatedRecipe.image()))
                .andExpect(jsonPath("$.cookTime").value(updatedRecipe.cookTime()));
    }

    @Test
    public void deleteRecipeTest() throws Exception {
        Long recipeId = 1L;
        doNothing().when(recipeService).deleteRecipe(recipeId);

        mockMvc.perform(delete("/recipes/{id}", recipeId))
                .andExpect(status().isOk())
                .andDo(print());

        verify(recipeService, times(1)).deleteRecipe(recipeId);
    }
}