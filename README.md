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
└── ebank-ai-agent/       # Chatbot IA avec Ollama (Port 8085)
    ├── REST API          # Endpoint de chat HTTP
    ├── MCP Client        # Client MCP connecté à account-service
    ├── Mémoire           # Historique persistant en base H2
    └── Discord Bot       # ⚠️ Temporairement désactivé
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
- **Ollama** — [Installer Ollama](https://ollama.com/download)
- **Modèle Ollama** : `llama3.1:8b` (recommandé pour les outils MCP)

### Installer Ollama et le modèle

```bash
# Installer Ollama
# Télécharger depuis https://ollama.com/download

# Télécharger le modèle Llama 3.1 8B (environ 5 Go)
ollama pull llama3.1:8b
```

---

## Configuration

### 1. Ollama

Assurez-vous que Ollama est installé et en cours d'exécution :

```bash
# Vérifier que Ollama tourne
ollama list

# Télécharger le modèle si ce n'est pas déjà fait
ollama pull llama3.1:8b
```

Ollama démarre automatiquement un serveur local sur `http://localhost:11434`.

### 2. Fichier de configuration

Le fichier `ebank-ai-agent/src/main/resources/application.properties` est déjà configuré pour utiliser Ollama par défaut. Aucune clé API n'est nécessaire car le modèle tourne localement.

Pour personnaliser le modèle, modifiez :

```properties
spring.ai.ollama.chat.model=llama3.1:8b
```

> ⚠️ **Important** : Le fichier `application.properties` est exclu de Git via `.gitignore`. Utilisez `application.properties.example` comme modèle.

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

Chatbot IA alimenté par Ollama (modèle local) avec :

- **Modèle IA** : Ollama avec Llama 3.1 8B (exécutable localement, sans clé API)
- **MCP Client** : Connecté à account-service pour les opérations bancaires
- **Mémoire de conversation** : Historique persistant en base H2 par session utilisateur
- **Discord Bot** : ⚠️ Temporairement désactivé (token invalide)
- **REST API** : Interface web pour le chat

#### Mémoire de conversation

Le chatbot conserve l'historique des échanges pour chaque session utilisateur dans une base H2 persistante. Cela permet à l'IA de se souvenir du contexte des conversations précédentes.

**Endpoints de la mémoire :**

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/chat/memory/history?sessionId=...` | Récupère l'historique d'une session |
| `DELETE` | `/api/chat/memory/clear?sessionId=...` | Efface l'historique d'une session |
| `GET` | `/api/chat/memory/count?sessionId=...` | Compte le nombre de messages d'une session |

**Exemples :**

```bash
# Voir l'historique d'une session
curl http://localhost:8085/api/chat/memory/history?sessionId=romaric

# Compter les messages
curl http://localhost:8085/api/chat/memory/count?sessionId=romaric

# Effacer l'historique
curl -X DELETE http://localhost:8085/api/chat/memory/clear?sessionId=romaric
```

**Console H2 :**

La base de données H2 est accessible via :
- **URL** : `http://localhost:8085/h2-console`
- **JDBC URL** : `jdbc:h2:file:./data/ebank-ai-agent-db`
- **Utilisateur** : `sa`
- **Mot de passe** : (vide)

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
| `GET` | `/api/chat/memory/history?sessionId=...` | Historique d'une session |
| `DELETE` | `/api/chat/memory/clear?sessionId=...` | Efface l'historique d'une session |
| `GET` | `/api/chat/memory/count?sessionId=...` | Nombre de messages d'une session |

---

## Discord Bot

> ⚠️ **Temporairement désactivé** — Le bot Discord est actuellement désactivé car le token de connexion est invalide. La fonctionnalité sera réactivée une fois un nouveau token configuré.

### Configuration (lors de la réactivation)

1. Créez une application sur le [Portail développeur Discord](https://discord.com/developers/applications)
2. Générez un token Bot
3. Définissez la variable `DISCORD_TOKEN` ou modifiez `discord.token` dans `application.properties`
4. Invitez le bot sur votre serveur Discord
5. Réactivez le bot en retirant l'exclusion de `DiscordBot` dans `EbankAiAgentApplication.java`

### Utilisation (lors de la réactivation)

Envoyez un message dans un canal où le bot est présent :

```
> Donne-moi la liste des comptes
> Quel est le solde du compte 1 ?
> Effectue un crédit de 50000 sur le compte 1
```

Le bot répondra automatiquement en utilisant le modèle Ollama local et les outils bancaires MCP.

---

## Technologies utilisées

| Technologie | Version | Rôle |
|-------------|---------|------|
| Spring Boot | 4.1.2 | Framework principal |
| Spring AI | 2.0.1 | Intégration IA et MCP |
| Ollama | - | Serveur de modèles IA locaux |
| Llama 3.1 8B | - | Modèle de langage (function calling) |
| H2 Database | - | Base de données (mémoire + fichier) |
| Spring Data JPA | - | Persistance des données |
| Spring Cloud | 2025.1.4 | Configuration et découverte |
| Discord4J / JDA | - | Bot Discord (désactivé) |
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
        │   ├── domain/              # Entités JPA (ConversationMessage)
        │   ├── repository/          # Repositories JPA
        │   ├── Rest/                # Contrôleur REST du chat
        │   └── discord/             # Contrôleur Discord (désactivé)
        └── resources/
            ├── application.properties
            └── application.properties.example
```

---

## ⚠️ Notes importantes

- **Base de données** : Account-Service utilise H2 en mémoire (données perdues au redémarrage). Ebank-AI-Agent utilise H2 en fichier pour la mémoire de conversation (données persistantes).
- **Modèle IA** : `llama3.1:8b` via Ollama est utilisé par défaut. D'autres modèles compatibles avec le function calling sont disponibles (ex: `qwen2.5`, `mistral`).
- **Mémoire de conversation** : Chaque session de chat conserve l'historique des échanges dans une base H2 persistante. Les données survivent aux redémarrages de l'application.
- **Discord Bot** : Temporairement désactivé en attendant un token valide.

---

## 📄 Licence

Ce projet est développé dans le cadre de l'apprentissage de Spring Boot et de l'IA.
