# cook-ai

[![Java](https://img.shields.io/badge/Java-25-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.16-brightgreen)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1.8-blue)](https://spring.io/projects/spring-ai)

## Table of contents

- [Project description](#project-description)
- [Tech stack](#tech-stack)
- [Getting started locally](#getting-started-locally)
- [Available scripts](#available-scripts)
- [Project scope](#project-scope)
- [Project status](#project-status)
- [License](#license)

## Project description

**cook-ai** is a small Spring Boot REST service that generates recipe suggestions using OpenAI through [Spring AI](https://docs.spring.io/spring-ai/reference/). You send available ingredients, a diet preference, and a meal type; the model returns a structured recipe (title, preparation time, and step-by-step instructions) in Polish.

The project demonstrates how to:

- Prompt an LLM with a `PromptTemplate` and user-specific variables.
- Parse model output into a typed Java object with `BeanOutputConverter`.
- Expose the result as JSON from a single REST endpoint.

Maven coordinates: `pl.mojezapiski:ai:0.0.1-SNAPSHOT` (application name: `ai`).

## Tech stack

| Layer | Technology |
| --- | --- |
| Language | Java 25 |
| Framework | Spring Boot 3.5.16 |
| AI integration | Spring AI 1.1.8 (`spring-ai-starter-model-openai`) |
| Web | Spring Web (REST), Spring WebFlux (transitive stack) |
| Packaging | WAR (runnable as embedded app or deployable to an external servlet container via `ServletInitializer`) |
| Build | Maven (Maven Wrapper included) |
| Utilities | Lombok |
| Testing | Spring Boot Test, Reactor Test |

External service: **OpenAI API** (API key required).

## Getting started locally

### Prerequisites

- JDK 25
- An [OpenAI API key](https://platform.openai.com/api-keys)

### Configuration

Set the OpenAI API key as an environment variable (recommended):

```bash
# Linux / macOS
export OPENAI_API_KEY=your-api-key-here

# Windows (PowerShell)
$env:OPENAI_API_KEY = "your-api-key-here"
```

The application reads the key from `spring.ai.openai.api-key`, which defaults to the `OPENAI_API_KEY` environment variable (see `src/main/resources/application.properties`).

### Run the application

From the project root:

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

By default the server listens on **http://localhost:8080**.

### Try the API

**Endpoint:** `POST /recipeSuggestions`  
**Content-Type:** `application/json`

Example request:

```bash
curl -X POST http://localhost:8080/recipeSuggestions \
  -H "Content-Type: application/json" \
  -d '{
    "ingredients": [
      { "name": "jajka", "weight": 3 },
      { "name": "szpinak", "weight": 200 }
    ],
    "diet": "wegetariańska",
    "mealType": "śniadanie"
  }'
```

Example response shape:

```json
{
  "title": "…",
  "prepTimeMinutes": 15,
  "instructionSteps": [
    "…",
    "…"
  ]
}
```

Field mapping:

| JSON field | Type | Description |
| --- | --- | --- |
| `ingredients` | array of `{ "name", "weight" }` | Available ingredients (weight is a numeric amount in your chosen unit) |
| `diet` | string | Diet constraint (free text, e.g. vegetarian, gluten-free) |
| `mealType` | string | Preferred meal type (e.g. breakfast, lunch, dinner) |

Recipe text and steps are generated **in Polish**, as defined in the controller prompt.

## Available scripts

This project uses the Maven Wrapper. Common commands:

| Command | Description |
| --- | --- |
| `./mvnw spring-boot:run` / `mvnw.cmd spring-boot:run` | Start the application locally |
| `./mvnw test` / `mvnw.cmd test` | Run tests (`AiApplicationTests` — context load smoke test) |
| `./mvnw clean package` / `mvnw.cmd clean package` | Build the WAR artifact under `target/` |
| `./mvnw clean verify` / `mvnw.cmd clean verify` | Run tests and package in one step |

On Windows, use `mvnw.cmd` instead of `./mvnw`.

## Project scope

**In scope**

- One REST controller (`CookRestController`) with `POST /recipeSuggestions`.
- Structured request/response models: `RecipeRequest`, `RecipeResponse`, `Ingredient`.
- OpenAI chat completion via `OpenAiChatModel` and prompt formatting for ingredients, diet, and meal type.
- Spring Boot application entry point and WAR deployment support.

**Out of scope (current codebase)**

- Authentication, rate limiting, or API keys for clients.
- Persistence (database), user accounts, or recipe history.
- Frontend UI, OpenAPI/Swagger documentation, or production deployment configs.
- Automated integration tests against the live OpenAI API.

## Project status

| Item | Value |
| --- | --- |
| Version | `0.0.1-SNAPSHOT` |
| Maturity | Early-stage / example project |
| CI | Not configured in this repository |
| Tests | Minimal (`contextLoads` only) |

The POM description matches the project goal: an example of formatting and consuming structured AI responses in a Spring application.

## License

No `LICENSE` file is present in this repository, and the Maven POM does not declare a specific license. If you plan to publish or reuse this code, add an appropriate license file and update `pom.xml` accordingly.
