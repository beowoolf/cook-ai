package pl.mojezapiski.ai.cook;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CookRestController {
    private final OpenAiChatModel openAiChatClient;

    private PromptTemplate getPromptTemplate() {
        String promptString = """
                Na podstawie dostępnych składników: {skladniki},
                oraz z uwzględnieniem diety: {dieta}
                i preferowanego typu posiłku: {posilek},
                daj mi przepis kulinarny, który spełnia te kryteria.
                Nie musisz wykorzystywać wszystkich składników.
                Odpowiedz po Polsku.
                Zwróć to w formacie: {format}
                """;

        PromptTemplate template = new PromptTemplate(promptString);
        return template;
    }

    @PostMapping("/recipeSuggestions")
    RecipeResponse suggestRecipe(@RequestBody RecipeRequest request) {
        BeanOutputConverter<RecipeResponse> outputConverter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<>() {
                });
        PromptTemplate template = getPromptTemplate();
        template.add("skladniki", request.ingredients());
        template.add("dieta", request.diet());
        template.add("posilek", request.mealType());
        template.add("format", outputConverter.getFormat());

        var prompt = template.create();
        var response = openAiChatClient.call(prompt);

        var content = response.getResult().getOutput().getText();
        return outputConverter.convert(content);
    }
}
