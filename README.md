# cook-ai

[![Java](https://img.shields.io/badge/Java-25-orange.svg?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.16-brightgreen.svg?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1.8-6DB33F.svg?logo=spring&logoColor=white)](https://spring.io/projects/spring-ai)
[![Docker](https://img.shields.io/badge/Docker-Multi--stage-2496ED.svg?logo=docker&logoColor=white)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-Unlicensed-lightgrey.svg)](#license)

An AI-powered recipe generation REST service built with **Spring Boot 3.5.16**, **Java 25**, and **Spring AI 1.1.8**, integrating with the **OpenAI API** to generate structured culinary recipes tailored to available ingredients and dietary preferences.

---

## Table of Contents

- [Project description](#project-description)
- [Tech stack](#tech-stack)
- [Getting started locally](#getting-started-locally)
  - [Prerequisites](#prerequisites)
  - [Configuration](#configuration)
  - [Run with Maven Wrapper](#run-with-maven-wrapper)
  - [Run with Docker Compose](#run-with-docker-compose)
  - [Run with Docker standalone](#run-with-docker-standalone)
  - [API Reference and Testing](#api-reference-and-testing)
- [Available scripts](#available-scripts)
- [Project scope](#project-scope)
- [Project status](#project-status)
- [License](#license)

---

## Project description

**cook-ai** is a backend service designed to eliminate food waste and culinary indecision. By taking an inventory of available ingredients, dietary constraints, and target meal types, the service communicates with OpenAI's language models via [Spring AI](https://docs.spring.io/spring-ai/reference/) to generate personalized, step-by-step recipes formatted in Polish.

### Key Highlights

- **Dynamic Prompt Engineering**: Employs `PromptTemplate` to construct contextual prompts parameterized with available ingredients, diet type, meal category, and explicit response format instructions.
- **Strict Structured Outputs**: Leverages Spring AI's `BeanOutputConverter` with `ParameterizedTypeReference` to enforce strongly-typed JSON schema responses from the LLM, deserializing directly into modern Java records (`RecipeResponse`).
- **Container-Ready Architecture**: Packaged as an executable WAR with multi-stage Alpine Docker containerization and Docker Compose orchestration.
- **Stateless RESTful Design**: Exposes an intuitive `POST /recipeSuggestions` endpoint for easy integration into web, mobile, or smart kitchen clients.

### Processing Workflow

```text
Client Request (JSON)
       │
       ▼
CookRestController  ──►  PromptTemplate (Variables + JSON Format Spec)
                                  │
                                  ▼
                          OpenAiChatModel  ──►  OpenAI API
                                                     │
Client Response (JSON) ◄── BeanOutputConverter ◄─────┘
```

---

## Tech stack

| Category | Technology | Version / Details |
| --- | --- | --- |
| **Language** | Java | 25 (Java Records, Text Blocks) |
| **Framework** | Spring Boot | 3.5.16 |
| **AI Integration** | Spring AI | 1.1.8 (`spring-ai-starter-model-openai`) |
| **Web Layer** | Spring Web | Spring MVC (RESTful endpoints), Spring WebFlux |
| **Application Server** | Apache Tomcat | Embedded Tomcat (`spring-boot-starter-tomcat`, `provided` scope) |
| **Containerization** | Docker & Docker Compose | Multi-stage build (`eclipse-temurin:25-jdk-alpine` -> `eclipse-temurin:25-jre-alpine`) |
| **Build Tool** | Apache Maven | Maven Wrapper (`mvnw`, `mvnw.cmd`) |
| **Boilerplate Reduction** | Project Lombok | Lombok annotation processor |
| **Testing** | JUnit 5 & Spring Boot Test | `spring-boot-starter-test`, `reactor-test` |
| **External AI Provider** | OpenAI API | Chat completion endpoint (requires API key) |

---

## Getting started locally

### Prerequisites

Ensure you have the following installed on your local machine:

- **Java Development Kit (JDK)**: Version 25 or higher
- **OpenAI API Key**: Active API key from the [OpenAI Platform](https://platform.openai.com/api-keys)
- **Docker & Docker Compose** *(optional)*: For running in a containerized environment

### Configuration

The application requires an OpenAI API key. In `src/main/resources/application.properties`, the key is bound as:

```properties
spring.application.name=ai
spring.ai.openai.api-key=${OPENAI_API_KEY:set_env_var}
```

Export the key in your terminal before running:

**Linux / macOS:**
```bash
export OPENAI_API_KEY="your-actual-openai-api-key"
```

**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="your-actual-openai-api-key"
```

**Windows (Command Prompt):**
```cmd
set OPENAI_API_KEY=your-actual-openai-api-key
```

---

### Run with Maven Wrapper

Execute the application using the included Maven wrapper from the project root:

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

**Windows:**
```powershell
.\mvnw.cmd spring-boot:run
```

Once started, the application will listen on **`http://localhost:8080`**.

---

### Run with Docker Compose

A pre-configured `docker-compose.yml` file is provided in the repository root.

1. Ensure `OPENAI_API_KEY` is set in your environment (or create a `.env` file in the root containing `OPENAI_API_KEY=your_key_here`).
2. Build and start the container:

```bash
docker compose up --build
```

To run in the background (detached mode):
```bash
docker compose up -d
```

To stop the service:
```bash
docker compose down
```

---

### Run with Docker standalone

You can build and run the multi-stage Docker container manually:

```bash
# Build the Docker image
docker build -t cook-ai:latest .

# Run the container exposing port 8080
docker run -d --name cook-ai -p 8080:8080 -e OPENAI_API_KEY="your-api-key-here" cook-ai:latest
```

---

### API Reference and Testing

#### **Endpoint:** `POST /recipeSuggestions`

Generates a structured recipe based on provided ingredients and preferences.

- **URL:** `http://localhost:8080/recipeSuggestions`
- **Method:** `POST`
- **Headers:** `Content-Type: application/json`

#### Example Request (`cURL`)

```bash
curl -X POST http://localhost:8080/recipeSuggestions \
  -H "Content-Type: application/json" \
  -d '{
    "ingredients": [
      { "name": "jajka", "weight": 3 },
      { "name": "szpinak", "weight": 150 },
      { "name": "czosnek", "weight": 10 },
      { "name": "oliwa z oliwek", "weight": 15 }
    ],
    "diet": "wegetariańska",
    "mealType": "śniadanie"
  }'
```

#### Request Payload Structure

| Field | Type | Description |
| --- | --- | --- |
| `ingredients` | `Array<Ingredient>` | List of available ingredients. Each item contains `name` (`String`) and `weight` (`Double`). |
| `diet` | `String` | Dietary requirement (e.g., `"wegetariańska"`, `"wegańska"`, `"bezglutenowa"`, `"keto"`). |
| `mealType` | `String` | Target meal type (e.g., `"śniadanie"`, `"obiad"`, `"kolacja"`, `"deser"`). |

#### Example Response Body

```json
{
  "title": "Aromatyczna jajecznica ze świeżym szpinakiem i czosnkiem",
  "prepTimeMinutes": 15,
  "instructionSteps": [
    "Umyj i osusz liście szpinaku, a ząbek czosnku drobno posiekaj.",
    "Na patelni rozgrzej oliwę z oliwek i podsmaż posiekany czosnek przez około 30 sekund.",
    "Dodaj świeży szpinak i duś na małym ogniu, aż zmniejszy swoją objętość (około 2 minuty).",
    "W misce roztrzep jajka ze szczyptą soli i pieprzu, a następnie wlej na patelnię ze szpinakiem.",
    "Smaż na małym ogniu delikatnie mieszając do uzyskania pożądanej konsystencji.",
    "Podawaj natychmiast po przygotowaniu."
  ]
}
```

#### Response Payload Structure

| Field | Type | Description |
| --- | --- | --- |
| `title` | `String` | The generated recipe title in Polish. |
| `prepTimeMinutes` | `Integer` | Estimated total preparation and cooking time in minutes. |
| `instructionSteps` | `List<String>` | Ordered step-by-step cooking instructions in Polish. |

---

## Available scripts

### Maven Commands

| Command (Unix) | Command (Windows) | Description |
| --- | --- | --- |
| `./mvnw spring-boot:run` | `.\mvnw.cmd spring-boot:run` | Runs the application locally using the embedded Tomcat server |
| `./mvnw test` | `.\mvnw.cmd test` | Executes unit and context load smoke tests |
| `./mvnw clean package` | `.\mvnw.cmd clean package` | Compiles source code and packages a WAR archive in `target/` |
| `./mvnw clean package -DskipTests` | `.\mvnw.cmd clean package -DskipTests` | Builds the WAR archive bypassing test execution (used in Dockerfile) |
| `./mvnw clean verify` | `.\mvnw.cmd clean verify` | Runs tests, verifies packaging, and executes build verifications |

### Docker Commands

| Command | Description |
| --- | --- |
| `docker compose up --build` | Builds the image and starts the containerized service |
| `docker compose up -d` | Starts the containerized service in background / detached mode |
| `docker compose down` | Stops and removes containers and networks created by Docker Compose |
| `docker compose logs -f` | Follows real-time logs from the running `cook-ai-backend` container |
| `docker build -t cook-ai .` | Manually builds the multi-stage Docker image |
| `docker run -p 8080:8080 -e OPENAI_API_KEY=... cook-ai` | Runs the Docker container locally |

---

## Project scope

### Currently Implemented (In Scope)

- **AI-Powered Recipe Generation**: Integration with OpenAI chat models using Spring AI.
- **Dynamic Variable Prompting**: Constructing tailored prompts from ingredient lists, dietary requirements, and meal categories.
- **Type-Safe Structured Output**: Utilizing `BeanOutputConverter` to map AI text responses directly to Java records (`RecipeResponse`).
- **REST API Endpoint**: Exposed `POST /recipeSuggestions` endpoint.
- **Flexible Packaging & Deployment**:
  - Standalone executable WAR with embedded Tomcat.
  - Deployable to external servlet containers via `SpringBootServletInitializer`.
  - Multi-stage Docker image packaging via Eclipse Temurin 25 Alpine.
  - Docker Compose orchestration with environment variable delegation.
- **Basic Context Testing**: Spring Boot smoke test (`AiApplicationTests`) verifying application context loading.

### Future Enhancements (Out of Scope for Current Release)

- **User Authentication & Authorization**: API Key management, JWT, or OAuth2 access controls.
- **Data Persistence**: Database storage (JPA / PostgreSQL) for saving user recipes, favorite dishes, or ingredient history.
- **Frontend User Interface**: Web dashboard (React, Vue) or mobile client for visual ingredient selection.
- **API Documentation**: OpenAPI 3 / Swagger UI (`springdoc-openapi`) integration.
- **Rate Limiting & Caching**: Redis-backed caching for frequent ingredient combinations and IP/User rate limits.
- **Multilingual Support**: Supporting recipe prompts and instructions in multiple languages (currently Polish only).
- **Mock Testing for AI Client**: Comprehensive integration tests with mocked OpenAI responses.

---

## Project status

| Metric | Status |
| --- | --- |
| **Current Version** | `0.0.1-SNAPSHOT` |
| **Development Phase** | Active Proof-of-Concept / Functional Prototype |
| **Build Status** | Passing (`mvnw clean package`) |
| **CI/CD Pipeline** | Not configured |
| **OpenAI Integration** | Functional (requires valid API credentials) |

---

## License

This repository does not currently include a standalone `LICENSE` file, and no specific license is declared in `pom.xml`. All rights are reserved by the author.

If you intend to use, modify, or distribute this codebase as an open-source project, please consider adding a standard open-source license such as the [MIT License](https://opensource.org/licenses/MIT) or the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0).
