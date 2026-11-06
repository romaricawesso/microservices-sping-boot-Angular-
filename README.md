# 🏦 E-Bank BDCC App

Application bancaire intelligente avec chatbot IA intégré, permettant de gérer des comptes bancaires via une interface conversationnelle (navigateur web et Discord).

## 📋 Table des matières

- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Configuration](#configuration)
- [Lancement](#lancement)
- [Services](#services)
- [Utilisation du Chatbot](#utilisation-du-chatbot)
- [API REST](#api-rest)
- [Discord Bot](#discord-bot)

---

## Architecture

Le projet est composé de deux modules Spring Boot :

```
ebank-bdcc-app/
├── account-service/      # Service de gestion des comptes bancaires (Port 8080)
│   ├── REST API          # Endpoints HTTP pour la gestion des comptes
│   └── MCP Server        # Serveur MCP exposant les outils bancaires
│
└── ebank-ai-agent/       # Chatbot IA avec Google Gemini (Port 8085)
    ├── REST API          # Endpoint de chat HTTP
    ├── MCP Client        # Client MCP connecté à account-service
    └── Discord Bot       # Bot Discord pour le chat
```

### Flux de données

```
Utilisateur → ebank-ai-agent (Chatbot IA) → MCP → account-service (Banque)
     ↑                                                    │
     └──────────── Réponse IA ←───────────────────────────┘
```

---

## Prérequis

- **Java 21** (JDK)
- **Maven 3.8+**
- **Google Gemini API Key** — [Obtenir une clé](https://aistudio.google.com/apikey)
- **Discord Bot Token** (optionnel) — [Créer un bot Discord](https://discord.com/developers/applications)

---

## Configuration

### 1. Variables d'environnement

Avant de démarrer l'application, définissez les variables d'environnement suivantes :

#### Windows (PowerShell)

```powershell
[System.Environment]::SetEnvironmentVariable('GOOGLE_GENAI_API_KEY', 'VOTRE_CLE_API', [System.EnvironmentVariableTarget]::User)
[System.Environment]::SetEnvironmentVariable('DISCORD_TOKEN', 'VOTRE_TOKEN_DISCORD', [System.EnvironmentVariableTarget]::User)
```

#### Linux / macOS

```bash
export GOOGLE_GENAI_API_KEY="VOTRE_CLE_API"
export DISCORD_TOKEN="VOTRE_TOKEN_DISCORD"
```

> ⚠️ **Important** : Les fichiers `application.properties` sont exclus de Git pour protéger les secrets. Ne commitez jamais vos clés API.

### 2. Fichier de configuration

Copiez le fichier exemple et configurez-le :

```bash
cd ebank-ai-agent/src/main/resources/
cp application.properties.example application.properties
```

Puis remplacez les placeholders par vos vraies clés ou utilisez les variables d'environnement.

---

## Lancement

### Compiler le projet

```bash
mvn clean install
```

### Démarrer les services

Les deux services doivent être démarrés pour que le chatbot fonctionne.

#### 1. Account-Service (Port 8080)

```bash
cd account-service
mvn spring-boot:run
```

#### 2. Ebank-AI-Agent (Port 8085)

```bash
cd ebank-ai-agent
mvn spring-boot:run
```

### Vérification

- **Account-Service** : `http://localhost:8080/api/accounts`
- **H2 Console** : `http://localhost:8080/h2-console` (JDBC URL : `jdbc:h2:mem:accounts-db`)
- **Chatbot** : `http://localhost:8085/api/chat?query=Bonjour`
- **Swagger UI (account-service)** : `http://localhost:8080/swagger-ui.html`
- **Swagger UI (ebank-ai-agent)** : `http://localhost:8085/swagger-ui.html`

---

## Services

### Account-Service

Service de gestion des comptes bancaires avec :

- **Base de données** : H2 en mémoire
- **Persistance** : Spring Data JPA
- **API** : REST + GraphQL + gRPC
- **MCP Server** : Expose 5 outils bancaires au chatbot
- **Découverte** : Spring Cloud Eureka (désactivé par défaut)
- **Configuration** : Spring Cloud Config (désactivé par défaut)

#### Outils MCP exposés

| Outil | Description |
|-------|-------------|
| `getAllAccounts` | Récupérer tous les comptes |
| `getAccountByid` | Récupérer un compte par son ID |
| `saveNewAccount` | Créer un nouveau compte bancaire |
| `credit` | Effectuer un crédit sur un compte |
| `debit` | Effectuer un débit sur un compte |

### Ebank-AI-Agent

Chatbot IA alimenté par Google Gemini avec :

- **Modèle IA** : Google Gemini (gemini-3.5-flash-lite)
- **MCP Client** : Connecté à account-service pour les opérations bancaires
- **Mémoire de conversation** : Historique des échanges par session
- **Discord Bot** : Interface Discord pour le chat
- **REST API** : Interface web pour le chat

---

## Utilisation du Chatbot

### Via le navigateur

**GET** — Posez une question dans l'URL :

```
http://localhost:8085/api/chat?query=Donne-moi+la+liste+des+comptes
```

Avec session persistante :

```
http://localhost:8085/api/chat?query=Quel+est+mon+solde&sessionId=utilisateur123
```

**POST** — Envoyez un message en corps de requête :

```bash
curl -X POST http://localhost:8085/api/chat \
  -H "Content-Type: text/plain" \
  -d "Transfère 10000 FCFA du compte 1 vers le compte 2"
```

### Exemples de questions

- `"Donne-moi la liste des comptes"`
- `"Quel est le solde du compte 1 ?"`
- `"Créer un nouveau compte avec 50000 FCFA"`
- `"Effectue un crédit de 100000 FCFA sur le compte 1"`
- `"Transfère 5000 FCFA du compte 1 vers le compte 2"`

---

## API REST

### Account-Service (`/api/accounts`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/accounts` | Liste tous les comptes |
| `GET` | `/api/accounts/{id}` | Récupère un compte par ID |
| `POST` | `/api/accounts` | Crée un nouveau compte |
| `POST` | `/api/accounts/{id}/credit` | Crédite un compte |
| `POST` | `/api/accounts/{id}/debit` | Débite un compte |

#### Créer un compte

```bash
curl -X POST "http://localhost:8080/api/accounts?initialbalance=50000&type=SAVING_ACCOUNT&currency=FCFA&customerId=1"
```

#### Créditer un compte

```bash
curl -X POST "http://localhost:8080/api/accounts/1/credit?amount=10000"
```

### Ebank-AI-Agent (`/api/chat`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/chat?query=...&sessionId=...` | Chat avec paramètre URL |
| `POST` | `/api/chat` | Chat avec corps de requête |

---

## Discord Bot

Le bot Discord permet d'interagir avec le chatbot IA directement depuis Discord.

### Configuration

1. Créez une application sur le [Portail développeur Discord](https://discord.com/developers/applications)
2. Générez un token Bot
3. Définissez la variable `DISCORD_TOKEN`
4. Invitez le bot sur votre serveur Discord

### Utilisation

Envoyez un message dans un canal où le bot est présent :

```
> Donne-moi la liste des comptes
> Quel est le solde du compte 1 ?
> Effectue un crédit de 50000 sur le compte 1
```

Le bot répondra automatiquement en utilisant l'IA Google Gemini et les outils bancaires MCP.

---

## Technologies utilisées

| Technologie | Version | Rôle |
|-------------|---------|------|
| Spring Boot | 4.1.2 | Framework principal |
| Spring AI | 2.0.1 | Intégration IA et MCP |
| Google GenAI | - | Modèle de langage Gemini |
| H2 Database | - | Base de données en mémoire |
| Spring Data JPA | - | Persistance des données |
| Spring Cloud | 2025.1.4 | Configuration et découverte |
| Discord4J / JDA | - | Bot Discord |
| Lombok | - | Réduction du code boilerplate |
| SpringDoc OpenAPI | 3.2.0 | Documentation Swagger |

---

## Structure du projet

```
ebank-bdcc-app/
├── pom.xml                          # Parent Maven
├── account-service/
│   ├── pom.xml                      # Dépendances account-service
│   └── src/main/
│       ├── java/.../accountservice/
│       │   ├── domain/              # Entités (Account, Transaction, etc.)
│       │   ├── repository/          # Repositories JPA
│       │   ├── service/             # Logique métier
│       │   ├── rest/                # Contrôleurs REST
│       │   └── mcp/                 # Contrôleurs MCP
│       └── resources/
│           └── application.properties
│
└── ebank-ai-agent/
    ├── pom.xml                      # Dépendances ebank-ai-agent
    └── src/main/
        ├── java/.../ebankaiagent/
        │   ├── agents/              # Agent IA et mémoire de conversation
        │   ├── Rest/                # Contrôleur REST du chat
        │   └── discord/             # Contrôleur Discord
        └── resources/
            ├── application.properties
            └── application.properties.example
```

---

## ⚠️ Notes importantes

- **Secrets** : Les clés API et tokens sont stockés dans des variables d'environnement, jamais dans le code source.
- **Base de données** : H2 en mémoire — les données sont perdues à chaque redémarrage.
- **Modèle IA** : `gemini-3.5-flash-lite` est utilisé par défaut. D'autres modèles sont disponibles.
- **Mémoire de conversation** : Chaque session de chat conserve l'historique des échanges.

---

## 📄 Licence

Ce projet est développé dans le cadre de l'apprentissage de Spring Boot et de l'IA.
