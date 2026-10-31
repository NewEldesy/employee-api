# Employee Management API

API REST de gestion des employés, développée avec **Spring Boot 4** et sécurisée par **JWT**. Ce projet est conçu comme portfolio technique — il illustre une architecture Spring Boot moderne, un pipeline Docker complet et une documentation OpenAPI interactive.

> **Interface web :** une interface d'aperçu minimale est en cours de développement et sera accessible directement depuis le navigateur.

---

## Stack technique

| Couche           | Technologie                             |
|------------------|-----------------------------------------|
| Runtime          | Java 25                                 |
| Framework        | Spring Boot 4.0.8 (Spring Framework 7)  |
| Sécurité         | Spring Security 7 · JWT (jjwt 0.12)     |
| Persistance      | Spring Data JPA · Hibernate · MySQL 8.4 |
| Migrations       | Flyway                                  |
| Documentation    | SpringDoc OpenAPI 3 (Swagger UI)        |
| Conteneurisation | Docker · Docker Compose                 |

---

## Prérequis

- [Docker Desktop](https://www.docker.com/products/docker-desktop) installé et démarré
- *(développement local uniquement)* Java 25 · Maven · MySQL 8.4

---

## Démarrage rapide avec Docker

### 1. Configurer les variables d'environnement

Copier le fichier d'exemple et renseigner les valeurs :

```bash
cp .env.example .env
```

Variables requises dans `.env` :

| Variable                     | Description                                                |
|------------------------------|------------------------------------------------------------|
|`DB_USERNAME`                 | Utilisateur MySQL de l'application                         |
|`DB_PASSWORD`                 | Mot de passe de cet utilisateur                            |
|`MYSQL_ROOT_PASSWORD`         | Mot de passe root MySQL                                    |
|`JWT_SECRET`                  | Secret JWT — **minimum 32 caractères**                     |
|`JWT_EXPIRATION_MS`           | Durée de validité du token en ms(défaut: `86400000` = 24h) |
|`APP_BOOTSTRAP_ENABLED`       | `true` pour créer les comptes de démo au premier démarrage |
|`APP_BOOTSTRAP_ADMIN_PASSWORD`| Mot de passe du compte `admin` (si bootstrap activé)       |
|`APP_BOOTSTRAP_USER_PASSWORD` | Mot de passe du compte `user` (si bootstrap activé)        |

> **Générer un secret JWT** sous PowerShell :
> ```powershell
> [Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }) -as [byte[]])
> ```

### 2. Lancer l'application

```bash
docker compose up --build
```

Les services démarrent dans l'ordre : MySQL → Application.

| Service    | URL                                                       |
|------------|-----------------------------------------------------------|
| API        | http://localhost:8080                                     |
| Swagger UI | http://localhost:8080/swagger-ui.html                     |
| phpMyAdmin | http://localhost:8081 *(décommenter dans `compose.yaml`)* |

---

## Endpoints

### Authentification

| Méthode | Endpoint          | Description          | Auth |
|---------|-------------------|----------------------|------|
| `POST`  | `/api/auth/login` | Obtenir un token JWT | —    |

### Employés

Tous les endpoints nécessitent un token JWT dans le header :
```
Authorization: Bearer <token>
```

| Méthode | Endpoint | Description |
|---|---|---|
| `GET` | `/api/employees` | Lister les employés (paginé) |
| `GET` | `/api/employees/{id}` | Afficher un employé |
| `POST` | `/api/employees` | Créer un employé |
| `PUT` | `/api/employees/{id}` | Modifier un employé |
| `DELETE` | `/api/employees/{id}` | Supprimer un employé |

### Utilitaires

| Méthode | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/api/health` | État de l'application | — |

---

## Exemple d'utilisation

### 1 — Obtenir un token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "VotreMotDePasse"}'
```

Réponse :
```json
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

### 2 — Appeler un endpoint protégé

```bash
curl http://localhost:8080/api/employees \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## Développement local

### Lancer avec le profil `dev`

Le profil `dev` active les logs SQL et le bootstrap des comptes de démo.

```powershell
$env:APP_BOOTSTRAP_ADMIN_PASSWORD="Admin.1234!"
$env:APP_BOOTSTRAP_USER_PASSWORD="User.1234!"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

### Lancer les tests

```bash
./mvnw test
```

---

## Structure du projet

```
src/
├── main/
│   ├── java/com/liceli/employees/
│   │   ├── config/          # Sécurité, OpenAPI, initialisation des données
│   │   ├── controller/      # AuthController, EmployeeController, HealthController
│   │   ├── dto/             # Requêtes et réponses (records Java)
│   │   ├── exception/       # EmployeeNotFoundException, GlobalExceptionHandler
│   │   ├── model/           # Employee, User, Role
│   │   ├── repository/      # Spring Data JPA
│   │   ├── security/        # JwtUtil, JwtAuthFilter, CustomUserDetailsService
│   │   └── service/         # EmployeeService
│   └── resources/
│       ├── db/migration/    # Scripts Flyway (V1__init_schema.sql)
│       ├── application.properties
│       └── application-dev.properties
└── test/                    # Tests unitaires et d'intégration (39 tests)
```

---

## Gestion des erreurs

| Code | Cas |
|---|---|
| `400` | Données de requête invalides (validation des champs) |
| `401` | Token absent, invalide ou expiré |
| `404` | Ressource introuvable |

---

## Licence

Projet portfolio — libre de réutilisation à des fins éducatives.
