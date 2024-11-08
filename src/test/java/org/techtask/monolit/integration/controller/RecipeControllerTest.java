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
import org.techtask.monolit.db.entity.RecipeDifficulty;
import org.techtask.monolit.db.entity.RecipeEntity;
import org.techtask.monolit.db.repository.IngredientRepository;
import org.techtask.monolit.db.repository.RecipeRepository;
import org.techtask.monolit.dto.IngredientDTO;
import org.techtask.monolit.request.CreateOrUpdateRecipeRequest;

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
public class RecipeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private IngredientRepository ingredientRepository;


    @AfterEach
    void cleanUp() {
        recipeRepository.deleteAll();
    }

    @Test
    void postRequestTestSuccess() throws Exception {
        CreateOrUpdateRecipeRequest request = CreateOrUpdateRecipeRequest.builder()
                .title("Delicious Pancakes")
                .description("Fluffy pancakes for breakfast")
                .instruction("Mix ingredients and cook on skillet.")
                .ingredients(List.of(
                        IngredientDTO.builder().name("ingr1").volume(2).unit("un").calories(2).build()
                ))
                .difficulty(RecipeDifficulty.EASY)
                .image("pancakes.jpg")
                .cookTime(15)
                .build();

        ResultActions resultActions = mockMvc.perform(post("/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        List<RecipeEntity> recipeEntityList = recipeRepository.findAll();
        assertEquals(1, recipeEntityList.size());
        RecipeEntity recipeEntity = recipeEntityList.get(0);
        assertNotNull(recipeEntity.getId());
        assertEquals(request.title(), recipeEntity.getTitle());
        assertEquals(request.description(), recipeEntity.getDescription());
        assertEquals(request.instruction(), recipeEntity.getInstruction());
        assertEquals(request.difficulty(), recipeEntity.getDifficulty());
        assertEquals(request.image(), recipeEntity.getImage());
        assertEquals(request.cookTime(), recipeEntity.getCookTime());

        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(recipeEntity.getId()))
                .andExpect(jsonPath("$.title").value(recipeEntity.getTitle()))
                .andExpect(jsonPath("$.description").value(recipeEntity.getDescription()))
                .andExpect(jsonPath("$.instruction").value(recipeEntity.getInstruction()))
                .andExpect(jsonPath("$.difficulty").value(recipeEntity.getDifficulty().name())) // Assuming difficulty is an enum
                .andExpect(jsonPath("$.image").value(recipeEntity.getImage()))
                .andExpect(jsonPath("$.cookTime").value(recipeEntity.getCookTime()));
    }

    @Test
    public void getRequestTestSuccess() throws Exception {
        List<CreateOrUpdateRecipeRequest> requestList = List.of(
                CreateOrUpdateRecipeRequest.builder()
                        .title("Delicious Pancakes1")
                        .description("Fluffy pancakes for breakfast1")
                        .instruction("Mix ingredients and cook on skillet1")
                        .ingredients(List.of(
                                IngredientDTO.builder().name("ingr1").volume(2).unit("un").calories(2).build()
                        ))
                        .difficulty(RecipeDifficulty.EASY)
                        .image("pancakes.jpg1")
                        .cookTime(15)
                        .build(),
                CreateOrUpdateRecipeRequest.builder()
                        .title("Delicious Pancakes2")
                        .description("Fluffy pancakes for breakfast2")
                        .instruction("Mix ingredients and cook on skillet2")
                        .ingredients(List.of(
                                IngredientDTO.builder().name("ingr1").volume(2).unit("un").calories(2).build()
                        ))
                        .difficulty(RecipeDifficulty.EASY)
                        .image("pancakes.jpg2")
                        .cookTime(15)
                        .build()
        );
        for (CreateOrUpdateRecipeRequest request : requestList) {
            mockMvc.perform(post("/recipes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is(200));
        }
        ResultActions resultActions = mockMvc.perform(get("/recipes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        List<RecipeEntity> recipeEntityList = recipeRepository.findAll();
        assertEquals(requestList.size(), recipeEntityList.size());

        for (int i = 0; i < requestList.size(); i++) {
            assertNotNull(recipeEntityList.get(i).getId());
            assertEquals(requestList.get(i).title(), recipeEntityList.get(i).getTitle());
            assertEquals(requestList.get(i).description(), recipeEntityList.get(i).getDescription());
            assertEquals(requestList.get(i).instruction(), recipeEntityList.get(i).getInstruction());
            assertEquals(requestList.get(i).difficulty(), recipeEntityList.get(i).getDifficulty());
            assertEquals(requestList.get(i).image(), recipeEntityList.get(i).getImage());
            assertEquals(requestList.get(i).cookTime(), recipeEntityList.get(i).getCookTime());

            resultActions.andDo(print())
                    .andExpect(jsonPath("$[" + i + "].title").value(requestList.get(i).title()))
                    .andExpect(jsonPath("$[" + i + "].description").value(requestList.get(i).description()))
                    .andExpect(jsonPath("$[" + i + "].instruction").value(requestList.get(i).instruction()))
                    .andExpect(jsonPath("$[" + i + "].difficulty").value(requestList.get(i).difficulty().name())) // Assuming difficulty is an enum
                    .andExpect(jsonPath("$[" + i + "].image").value(requestList.get(i).image()))
                    .andExpect(jsonPath("$[" + i + "].cookTime").value(requestList.get(i).cookTime()));
        }
    }

    @Test
    void putRequestTestSuccess() throws Exception {
        IngredientEntity ingredientEntity = IngredientEntity.builder().name("ingr1").volume(2).unit("un").calories(2).build();
        IngredientDTO ingredientDTO = IngredientDTO.builder().name("ingr1").volume(2).unit("un").calories(2).build();
        RecipeEntity entity = RecipeEntity.builder()
                .title("Spaghetti Carbonara")
                .description("A classic Italian pasta dish.")
                .instruction("Cook the spaghetti and mix with eggs, cheese, pancetta, and pepper.")
                .ingredients(List.of(ingredientEntity))
                .difficulty(RecipeDifficulty.MEDIUM)
                .image("carbonara.jpg")
                .cookTime(20)
                .build();
        CreateOrUpdateRecipeRequest request = CreateOrUpdateRecipeRequest.builder()
                .title("Delicious Pancakes")
                .description("Fluffy pancakes for breakfast")
                .instruction("Mix ingredients and cook on skillet.")
                .ingredients(List.of(ingredientDTO))
                .difficulty(RecipeDifficulty.EASY)
                .image("pancakes.jpg")
                .cookTime(15)
                .build();

        ingredientRepository.save(ingredientEntity);
        RecipeEntity existingEntity = recipeRepository.save(entity);

        ResultActions resultActions = mockMvc.perform(put("/recipes/{id}", existingEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        List<RecipeEntity> bookEntityList = recipeRepository.findAll();
        assertEquals(1, bookEntityList.size());
        RecipeEntity recipeEntity = bookEntityList.get(0);
        assertEquals(existingEntity.getId(), recipeEntity.getId());
        assertEquals(request.title(), recipeEntity.getTitle());
        assertEquals(request.description(), recipeEntity.getDescription());
        assertEquals(request.instruction(), recipeEntity.getInstruction());
        assertEquals(request.difficulty(), recipeEntity.getDifficulty());
        assertEquals(request.image(), recipeEntity.getImage());
        assertEquals(request.cookTime(), recipeEntity.getCookTime());

        resultActions.andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(recipeEntity.getId()))
                .andExpect(jsonPath("$.title").value(request.title()))
                .andExpect(jsonPath("$.description").value(request.description()))
                .andExpect(jsonPath("$.instruction").value(request.instruction()))
                .andExpect(jsonPath("$.difficulty").value(request.difficulty().name())) // Assuming difficulty is an enum
                .andExpect(jsonPath("$.image").value(request.image()))
                .andExpect(jsonPath("$.cookTime").value(request.cookTime()));
    }

    @Test
    void putRequestTestEntityNotFound() throws Exception {
        IngredientEntity ingredientEntity = IngredientEntity.builder().name("ingr1").volume(2).unit("un").calories(2).build();
        RecipeEntity entity = RecipeEntity.builder()
                .title("Spaghetti Carbonara")
                .description("A classic Italian pasta dish.")
                .instruction("Cook the spaghetti and mix with eggs, cheese, pancetta, and pepper.")
                .ingredients(List.of(ingredientEntity))
                .difficulty(RecipeDifficulty.MEDIUM)
                .image("carbonara.jpg")
                .cookTime(20)
                .build();
        CreateOrUpdateRecipeRequest request = CreateOrUpdateRecipeRequest.builder()
                .title("Delicious Pancakes")
                .description("Fluffy pancakes for breakfast")
                .instruction("Mix ingredients and cook on skillet.")
                .difficulty(RecipeDifficulty.EASY)
                .image("pancakes.jpg")
                .cookTime(15)
                .build();
        ingredientRepository.save(ingredientEntity);
        recipeRepository.save(entity);

        ResultActions resultActions = mockMvc.perform(put("/recipes/{id}", 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(404));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(responseBody.contains("Recipe with id 3 not found"));
    }

    @Test
    void deleteRequestTestSuccess() throws Exception {
        IngredientEntity ingredientEntity = IngredientEntity.builder().name("ingr1").volume(2).unit("un").calories(2).build();
        RecipeEntity entity = RecipeEntity.builder()
                .title("Spaghetti Carbonara")
                .description("A classic Italian pasta dish.")
                .instruction("Cook the spaghetti and mix with eggs, cheese, pancetta, and pepper.")
                .ingredients(List.of(ingredientEntity))
                .difficulty(RecipeDifficulty.MEDIUM)
                .image("carbonara.jpg")
                .cookTime(20)
                .build();
        ingredientRepository.save(ingredientEntity);
        RecipeEntity savedEntity = recipeRepository.save(entity);
        List<RecipeEntity> recipeEntityList = recipeRepository.findAll();
        assertEquals(1, recipeEntityList.size());
        assertEquals(savedEntity.getId(), recipeEntityList.get(0).getId());

        mockMvc.perform(delete("/recipes/{id}", savedEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void deleteRequestTestFail() throws Exception {
        IngredientEntity ingredientEntity = IngredientEntity.builder().name("ingr1").volume(2).unit("un").calories(2).build();
        RecipeEntity entity = RecipeEntity.builder()
                .title("Spaghetti Carbonara")
                .description("A classic Italian pasta dish.")
                .instruction("Cook the spaghetti and mix with eggs, cheese, pancetta, and pepper.")
                .ingredients(List.of(ingredientEntity))
                .difficulty(RecipeDifficulty.MEDIUM)
                .image("carbonara.jpg")
                .cookTime(20)
                .build();

        ingredientRepository.save(ingredientEntity);
        recipeRepository.save(entity);
        ResultActions resultActions = mockMvc.perform(delete("/recipes/{id}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(404));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        assertTrue(responseBody.contains("Recipe with id 5 not found"));
    }

}