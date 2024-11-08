package org.techtask.monolit.unit.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.techtask.monolit.db.entity.IngredientEntity;
import org.techtask.monolit.db.repository.IngredientRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@ActiveProfiles("test")
public class IngredientRepositoryTest {
    @Autowired
    private final IngredientRepository ingredientRepository;

    @Test
    public void saveIngredientTest() {
        IngredientEntity entity = IngredientEntity.builder().name("Sugar").volume(100).unit("g").calories(387).build();
        IngredientEntity savedEntity = ingredientRepository.save(entity);
        assertNotNull(savedEntity.getId());
    }

    @Test
    public void getIngredientTest() {
        IngredientEntity entity = IngredientEntity.builder().name("Sugar").volume(100).unit("g").calories(387).build();
        IngredientEntity savedEntity = ingredientRepository.save(entity);
        IngredientEntity retrievedEntity = ingredientRepository.findById(savedEntity.getId()).get();
        assertEquals("Sugar", retrievedEntity.getName());
    }

    @Test
    public void readIngredientsTest() {
        List<IngredientEntity> requestList = List.of(
                IngredientEntity.builder().name("Sugar").volume(100).unit("g").calories(387).build(),
                IngredientEntity.builder().name("Flour").volume(200).unit("g").calories(364).build()
        );
        ingredientRepository.saveAll(requestList);
        List<IngredientEntity> entityList = ingredientRepository.findAll();
        assertEquals(2, entityList.size());
    }

    @Test
    public void updateIngredientTest() {
        IngredientEntity entity = IngredientEntity.builder().name("Sugar").volume(100).unit("g").calories(387).build();
        IngredientEntity savedEntity = ingredientRepository.save(entity);
        IngredientEntity readEntity = ingredientRepository.findById(savedEntity.getId()).get();
        readEntity.setName("Brown Sugar");
        IngredientEntity updatedEntity = ingredientRepository.save(readEntity);
        assertEquals("Brown Sugar", updatedEntity.getName());
    }

    @Test
    public void deleteIngredientTest() {
        IngredientEntity entity = IngredientEntity.builder().name("Sugar").volume(100).unit("g").calories(387).build();
        ingredientRepository.save(entity);
        ingredientRepository.deleteById(entity.getId());
        Optional<IngredientEntity> ingredientOptional = ingredientRepository.findById(entity.getId());
        assertThat(ingredientOptional).isEmpty();
    }
}