
const ingredientsTable = (ingredient, table) => {
    const tr = $('<tr></tr>');
    const td1 = $('<td>' + ingredient.name + '</td>');
    const td2 = $('<td>' + ingredient.volume + '</td>');
    const td3 = $('<td>' + ingredient.unit + '</td>');
    const td4 = $('<td>' + ingredient.calories + '</td>');

    const tdActions = $('<td></td>');
    const editButton = $('<button>Изменить</button>');
    const deleteButton = $('<button>Удалить</button>');

    editButton.click(() => {
        openIngredientModal(ingredient, (updatedIngredient) => {
            ajaxPUTWithoutResponse('/ingredients/' + updatedIngredient.id, updatedIngredient, () => {
                refreshIngredientsTable();
            });
        });
    });

    deleteButton.click(() => {
        ajaxDELETE('/ingredients/' + ingredient.id, () => {
            refreshIngredientsTable();
        });
    });

    tdActions.append(editButton);
    tdActions.append(deleteButton);
    tr.append(td1, td2, td3, td4, tdActions);
    table.append(tr);
};

const openIngredientModal = (ingredient = null, submitAction = (ingredient) => {}) => {
    if (!ingredient) {
        $('#ingredientModalLabel').text('Добавить ингредиент');
        $('#ingredientForm').trigger('reset');
    } else {
        $('#ingredientModalLabel').text('Изменить ингредиент');
        $('#ingredientName').val(ingredient.name);
        $('#ingredientVolume').val(ingredient.volume);
        $('#ingredientUnit').val(ingredient.unit);
        $('#ingredientCalories').val(ingredient.calories);
    }

    $('#ingredientModal').modal('show');
    $('#ingredientForm').off('submit');

    $('#ingredientForm').submit((event) => {
        event.preventDefault();
        const newIngredient = {
            id: ingredient ? ingredient.id : undefined,
            name: $('#ingredientName').val(),
            volume: $('#ingredientVolume').val(),
            unit: $('#ingredientUnit').val(),
            calories: $('#ingredientCalories').val(),
        };
        if (newIngredient.volume <= 0 || newIngredient.calories <= 0) {
            event.preventDefault();
            alert("Пожалуйста, введите значение больше нуля.");
            return;
        }

        submitAction(newIngredient);
        $('#ingredientModal').modal('hide');
    });
};

const refreshIngredientsTable = () => {
    $('#ingredientsTable').empty().append(`
        <tr>
            <th>Название</th>
            <th>Объем</th>
            <th>Единица измерения</th>
            <th>Калории</th>
            <th>
            <button id="createIngredientBtn">Добавить</button>
            </th>
        </tr>
    `);
    ajaxGET('/ingredients', ingredients => {
        console.log(ingredients);
        const table = $('#ingredientsTable');
        ingredients.forEach(ingredient =>{
            ingredientsTable(ingredient, table);
        })
        $('#createIngredientBtn').click(() => {
            openIngredientModal(null, (ingredient) => {
                ajaxPOSTWithoutResponse('/ingredients', ingredient, () => {
                    refreshIngredientsTable();
                });
            });
        });

    })
};

$(document).ready(() => {
    ajaxGET('/ingredients', ingredients => {
        console.log(ingredients);
        const table = $('#ingredientsTable');
        ingredients.forEach(ingredient =>{
            ingredientsTable(ingredient, table);
        })
        $('#createIngredientBtn').click(() => {
            openIngredientModal(null, (ingredient) => {
                ajaxPOSTWithoutResponse('/ingredients', ingredient, () => {
                    refreshIngredientsTable();
                });
            });
        });

    })
});