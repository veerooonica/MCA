function readImage(inputElement) {
    let deferred = $.Deferred();

    let files = inputElement.get(0).files;
    if (files && files[0]) {
        let fr = new FileReader();
        fr.onload = function (e) {
            deferred.resolve(e.target.result);
        };
        fr.readAsDataURL(files[0]);
    } else {
        deferred.resolve(undefined);
    }
    return deferred.promise();
}

const extractSelectedItems = (selectId) => {
    const selected = $('#' + selectId).val()
    return selected ? selected : [];
}

const recipesTable = (recipe, table) => {
    const tr1 = $('<tr></tr>');
    const td1 = $('<img src="' + recipe.image + '" alt="">')
    const td2 = $('<td>' + recipe.title + '</td>');
    const td3 = $('<td>' + recipe.description + '</td>');
    const td4 = $('<td>' + recipe.instruction + '</td>');
    const ingredientNames = recipe.ingredients.map(ingredient => ingredient.name).join(', ');
    const td5 = $('<td>' + ingredientNames + '</td>');
    /*const td6 = $('<td>' + recipe.difficulty + '</td>');*/
    const difficultyText = (recipe.difficulty === 'EASY') ? 'Легко' :
        (recipe.difficulty === 'MEDIUM') ? 'Средне' :
            (recipe.difficulty === 'HARD') ? 'Сложно' :
                'Неизвестно';

    const td6 = $('<td>' + difficultyText + '</td>');

    const td7 = $('<td>' + recipe.cookTime + '</td>');

    const tdDOP1 = $('<td></td>');
    const tdDOP2 = $('<td></td>');
    const editWay = $('<button>Изменить</button>')
    const deleteWay = $('<button>Удалить</button>')
    editWay.click(() => {
        console.log("edit")
        openRecipeModal(recipe, (recipe) => {
            ajaxPUTWithoutResponse('/recipes/' + recipe.id, recipe, () => {
                refreshRecipesTable();
            })
        });
    })
    deleteWay.click(() => {
        console.log("delete")
         ajaxDELETE('/recipes/' + recipe.id, () => {
             refreshRecipesTable();
         })
    })
    tdDOP1.append(editWay);
    tdDOP2.append(deleteWay);
    tr1.append(td1, td2, td3, td4, td5, td6, td7, tdDOP1, tdDOP2);
    table.append(tr1);
}

const refreshRecipesTable = () => {
    $('#recipesTable').empty().append(`
        <tr>
            <th>Изображение</th>
            <th>Название</th>
            <th>Описание</th>
            <th>Инструкция</th>
            <th>Ингредиенты</th>
            <th>Сложность</th>
            <th>Время приготовления (мин)</th>
            <th>
            <button id="createRecipeBtn">Добавить</button>
            </th>
        </tr>
    `);
    ajaxGET('/recipes', recipes => {
        const table = $('#recipesTable');
        recipes.forEach(recipe =>{
            console.log("recipe");
            console.log(recipe);
            recipesTable(recipe, table);
        })
        $('#createRecipeBtn').click(() => {
            console.log("button");
            $('#recipeModal').modal('show');
            openRecipeModal(null, (recipe) => {
                ajaxPOSTWithoutResponse('/recipes', recipe, () => {
                    refreshRecipesTable();
                })
            });
        })
    })
};

const openRecipeModal = (recipe = null, submitAction = (recipe) => {
}) => {
    if (!recipe) {
        $('#recipeModalLabel').text('Создать рецепт');
    } else {
        $('#recipeModalLabel').text('Изменить рецепт');
    }

    recipe = recipe || {};
    console.log("modal recipe");
    console.log(recipe);

    $('#recipeName').val(recipe.title || '');
    $('#recipeDecryption').val(recipe.description || '');
    $('#recipeInstruction').val(recipe.instruction || '');


    $('#recipeIngredients').empty();

    ajaxGET('/ingredients', ingredients => {
        const uniqueIngredients = new Set();

        ingredients.forEach(ingredient => {
            if (!uniqueIngredients.has(ingredient.id)) {
                uniqueIngredients.add(ingredient.id);
                $('#recipeIngredients').append('<option value="' + ingredient.id + '">' + ingredient.name + '</option>');
            }
        });

        if (recipe.ingredients) {
            $('#recipeIngredients').val(recipe.ingredients.map(ingredient => ingredient.id));
        }
    });

    $('#recipeDifficulty').val(recipe.difficulty || '');
    $('#recipeTime').val(recipe.cookTime || '');
    $('#recipeImage').val(recipe.image || '');

    $('#recipeModal').modal('show');
    $('#recipeForm').off('submit');

    $('#recipeForm').submit((event) => {
        event.preventDefault();
        readImage($('#recipeModalImage')).done(base64Data => {
            const newRecipe = {};
            if (base64Data) {
                newRecipe.image = base64Data;
            } else {
                newRecipe.image = recipe ? recipe.image : '';
            }
            newRecipe.id = recipe ? recipe.id : undefined;
            newRecipe.title = $('#recipeName').val();
            newRecipe.description = $('#recipeDecryption').val();
            newRecipe.instruction = $('#recipeInstruction').val();
            newRecipe.ingredients = extractSelectedItems('recipeIngredients')
                .map(id => ({
                    id: id,

                }));
            newRecipe.difficulty = $('#recipeDifficulty').val();
            newRecipe.cookTime = $('#recipeTime').val();

            if (newRecipe.cookTime <= 0) {
                event.preventDefault();
                alert("Пожалуйста, введите значение больше нуля.");
                return;
            }

            submitAction(newRecipe);
            $('#recipeModal').modal('hide');
        });
    });
};

$(document).ready( () => {
    ajaxGET('/recipes', recipes => {
        const table = $('#recipesTable');
        recipes.forEach(recipe =>{
            console.log("recipe");
            console.log(recipe);
            recipesTable(recipe, table);
        })
        $('#createRecipeBtn').click(() => {
            console.log("button");
            $('#recipeModal').modal('show');
            openRecipeModal(null, (recipe) => {
                ajaxPOSTWithoutResponse('/recipes', recipe, () => {
                    refreshRecipesTable();
                })
            });
        })
    })
})


