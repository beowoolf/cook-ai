package pl.mojezapiski.ai.cook;

import java.util.List;

record RecipeResponse(
        String title,
        int prepTimeMinutes,
        List<String> instructionSteps
) {
}
