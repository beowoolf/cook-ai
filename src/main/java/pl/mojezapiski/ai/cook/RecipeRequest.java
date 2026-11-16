package pl.mojezapiski.ai.cook;

import java.util.List;

record RecipeRequest(
        List<Ingredient> ingredients,
        String diet,
        String mealType
) {
}
