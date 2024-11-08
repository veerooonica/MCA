package org.techtask.monolit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.techtask.monolit.dto.IngredientDTO;
import org.techtask.monolit.request.CreateOrUpdateIngredientRequest;
import org.techtask.monolit.service.IngredientService;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
public class IngredientController {
    private final IngredientService ingredientService;

    @GetMapping
    public List<IngredientDTO> getAllRecipes() {
        return ingredientService.getAllIngredients();
    }

    @GetMapping("/{id}")
    public IngredientDTO getRecipeById(@PathVariable Long id) {
        return ingredientService.getIngredientById(id);
    }

    @PostMapping
    public IngredientDTO createRecipe(@RequestBody CreateOrUpdateIngredientRequest request) {
        return ingredientService.createIngredient(request);
    }

    @PutMapping("/{id}")
    public IngredientDTO updateRecipe(@PathVariable Long id, @RequestBody CreateOrUpdateIngredientRequest request) {
        return ingredientService.updateIngredient(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteRecipe(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
    }
}
