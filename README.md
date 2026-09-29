# 💕 Dating Website — REST API сайта знакомств

Backend-приложение сайта знакомств на **Java 21 + Spring Boot**. Пользователи регистрируются, создают анкеты, ищут друг друга по фильтрам и ставят лайки. При взаимном лайке автоматически создаётся **матч**.

Проект сделан как MVP для портфолио: только серверная часть (REST API), без фронтенда.

## 📋 Содержание

- [Возможности](#-возможности)
- [Технологии](#-технологии)
- [Архитектура](#-архитектура)
- [Модель данных](#-модель-данных)
- [Запуск проекта](#-запуск-проекта)
- [Конфигурация](#-конфигурация)
- [API](#-api)
- [Примеры запросов](#-примеры-запросов)
- [Особенности реализации](#-особенности-реализации)
- [Тесты](#-тесты)
- [Планы развития](#-планы-развития)

## ✨ Возможности

- Регистрация и вход по email и паролю, аутентификация через **JWT**
- Создание и редактирование анкеты (можно менять только свою)
- Просмотр анкет с пагинацией
- Поиск анкет по фильтрам: пол, возраст (от/до), город, имя
- Лайки: поставить, отозвать, посмотреть свои
- Автоматическое создание матча при взаимном лайке и удаление матча при отзыве лайка
- Список своих матчей
- Кэширование анкет в **Redis**
- Версионирование схемы БД через **Flyway**
- Интерактивная документация **Swagger UI**

## 🛠 Технологии

| Категория | Стек |
|---|---|
| Язык | Java 21 |
| Фреймворк | Spring Boot 4.1.1 (Web MVC, Data JPA, Security, Validation, Cache) |
| База данных | PostgreSQL |
| Кэш | Redis |
| Миграции | Flyway |
| Безопасность | Spring Security, JWT (jjwt 0.12.6), BCrypt (strength 12) |
| Документация | springdoc-openapi (Swagger UI) |
| Сборка | Maven (Maven Wrapper) |
| Инфраструктура | Docker Compose (PostgreSQL + Redis) |
| Прочее | Lombok, spring-dotenv |
| Тесты | JUnit 5, Spring Boot Test, Spring Security Test |

## 🏗 Архитектура

Классическая слоистая архитектура:

```
Controller  →  Service  →  Repository  →  PostgreSQL
    ↓             ↓
   DTO        Mapper (Entity ↔ DTO)
                  ↓
                Redis (кэш)
```

Структура проекта:

```
datingWebsite/
├── docker-compose.yaml          # PostgreSQL + Redis для разработки
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/example/datingWebsite/
    │   │   ├── DatingWebsiteApplication.java
    │   │   ├── config/              # Security, JWT-фильтр, CORS
    │   │   ├── controller/          # REST-контроллеры
    │   │   ├── dto/                 # Request/Response (records)
    │   │   ├── exception/           # Кастомные исключения
    │   │   ├── mapper/              # Entity ↔ DTO
    │   │   ├── model/               # JPA-сущности
    │   │   ├── repository/          # Spring Data JPA репозитории
    │   │   └── service/             # Бизнес-логика
    │   └── resources/
    │       ├── application.yml
    │       ├── application-dev.yml
    │       ├── application-prod.yml
    │       └── db/migration/        # Flyway-миграции (V1–V4)
    └── test/java/com/example/datingWebsite/service/
        ├── UserServiceTest.java
        ├── ProfileServiceTest.java
        ├── LikeServiceTest.java
        └── MatchServiceTest.java
```

## 🗄 Модель данных

```
users (1) ───── (1) profiles
  │
  ├──< likes    (from_user_id, to_user_id)   UNIQUE(from_user_id, to_user_id)
  └──< matches  (user1_id, user2_id)         UNIQUE(user1_id, user2_id)
```

| Таблица | Назначение | Основные поля |
|---|---|---|
| `users` | Учётные записи | `id`, `email`, `password` (BCrypt), `role`, `is_active`, `created_at` |
| `profiles` | Анкеты (связь 1:1 с `users`) | `id`, `user_id`, `firstname`, `lastname`, `age`, `gender`, `bio`, `city`, `updated_at` |
| `likes` | Лайки | `id`, `from_user_id`, `to_user_id`, `created_at` |
| `matches` | Взаимные лайки | `id`, `user1_id`, `user2_id`, `created_at` |

Схема создаётся миграциями `V1`–`V4` в `src/main/resources/db/migration/`.

## 🚀 Запуск проекта

### Требования

- JDK 21
- Docker и Docker Compose

### 1. Клонировать репозиторий

```bash
git clone <ссылка-на-репозиторий>
cd datingWebsite
```

### 2. Поднять PostgreSQL и Redis

```bash
docker compose -f docker-compose.yaml up -d
```

По умолчанию PostgreSQL доступен на порту `5433`, Redis на `6379`. База данных называется `dating`.

### 3. Создать файл `.env` в корне проекта

```env
# --- dev ---
DEV_DB_HOST=localhost
DEV_DB_PORT=5433
DEV_DB_NAME=dating
DEV_DB_USERNAME=postgres
DEV_DB_PASSWORD=your_password
DEV_SPRING_REDIS_HOST=localhost
DEV_SPRING_REDIS_PORT=6379

# --- prod ---
DB_HOST=
DB_PORT=
DB_NAME=
DB_USERNAME=
DB_PASSWORD=
SPRING_REDIS_HOST=
SPRING_REDIS_PORT=
```

> ⚠️ Файл `.env` добавлен в `.gitignore` и не должен попадать в репозиторий. Пароль от БД должен совпадать с `POSTGRES_PASSWORD` в `docker-compose.yaml`.

### 4. Запустить приложение

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

На Windows: `mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"`

Приложение будет доступно на `http://localhost:8080`.

### 5. Открыть Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

Для защищённых эндпоинтов нажмите **Authorize** и вставьте JWT-токен, полученный при логине.

## ⚙️ Конфигурация

Проект использует Spring-профили:

| Профиль | `ddl-auto` | SQL в логах | Назначение |
|---|---|---|---|
| `dev` | `update` | включены | Локальная разработка |
| `prod` | `validate` | выключены | Продакшен: схему создаёт только Flyway |

Все секреты (доступы к БД и Redis) берутся из переменных окружения / `.env`.

## 📡 API

### Аутентификация — `/auth`

| Метод | Путь | Описание | Доступ |
|---|---|---|---|
| `POST` | `/auth/register` | Регистрация пользователя | Публичный |
| `POST` | `/auth/login` | Логин, возвращает JWT | Публичный |

### Профили — `/api/profiles`

| Метод | Путь | Описание | Доступ |
|---|---|---|---|
| `POST` | `/api/profiles` | Создать профиль для текущего пользователя | JWT |
| `PUT` | `/api/profiles/{id}` | Обновить свой профиль | JWT |
| `GET` | `/api/profiles` | Все профили (пагинация, по умолчанию 20 на страницу) | Публичный |
| `GET` | `/api/profiles/search` | Поиск по `gender`, `minAge`, `maxAge`, `city`, `firstname` | Публичный |
| `GET` | `/api/profiles/{id}` | Профиль по ID (кэшируется в Redis) | JWT |

### Лайки — `/api/likes`

| Метод | Путь | Описание | Доступ |
|---|---|---|---|
| `POST` | `/api/likes/{toUserId}` | Лайкнуть пользователя (при взаимности создаётся матч) | JWT |
| `DELETE` | `/api/likes/{toUserId}` | Отозвать лайк (матч удаляется) | JWT |
| `GET` | `/api/likes/my` | Мои лайки | JWT |

### Матчи — `/api/matches`

| Метод | Путь | Описание | Доступ |
|---|---|---|---|
| `GET` | `/api/matches/my` | Мои матчи | JWT |

### Коды ответов

| Код | Когда возвращается |
|---|---|
| `200 / 201 / 204` | Успех |
| `400` | Невалидные данные, попытка лайкнуть себя |
| `401` | Неверный email/пароль или отсутствует токен |
| `403` | Попытка изменить чужой профиль |
| `404` | Пользователь, профиль или лайк не найден |
| `409` | Email уже занят, профиль или лайк уже существует |

## 📨 Примеры запросов

### Регистрация

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "anna@example.com", "password": "strongpass123"}'
```

Ответ `201 Created`:

```json
{
  "id": 1,
  "email": "anna@example.com",
  "role": "USER",
  "isActive": true,
  "createdAt": "2026-09-29T12:30:00"
}
```

### Логин

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "anna@example.com", "password": "strongpass123"}'
```

Ответ `200 OK` — JWT-токен строкой:

```
eyJhbGciOiJIUzI1NiJ9...
```

### Создание профиля

```bash
curl -X POST http://localhost:8080/api/profiles \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Анна",
    "lastName": "Иванова",
    "age": 25,
    "gender": "FEMALE",
    "city": "Москва",
    "bio": "Люблю путешествия, кофе и хорошие книги"
  }'
```

Ответ `201 Created`:

```json
{
  "id": 1,
  "firstName": "Анна",
  "lastName": "Иванова",
  "age": 25,
  "gender": "FEMALE",
  "city": "Москва",
  "bio": "Люблю путешествия, кофе и хорошие книги"
}
```

### Поиск профилей

```bash
curl "http://localhost:8080/api/profiles/search?gender=FEMALE&minAge=20&maxAge=30&city=Москва&page=0&size=10"
```

Ответ `200 OK`:

```json
{
  "content": [
    {
      "id": 1,
      "firstName": "Анна",
      "lastName": "Иванова",
      "age": 25,
      "gender": "FEMALE",
      "city": "Москва",
      "bio": "Люблю путешествия, кофе и хорошие книги"
    }
  ],
  "page": {
    "size": 10,
    "number": 0,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### Лайк пользователя

```bash
curl -X POST http://localhost:8080/api/likes/2 \
  -H "Authorization: Bearer <TOKEN>"
```

Ответ: `201 Created` (без тела). Если пользователь с ID `2` уже лайкнул вас, матч создаётся автоматически.

### Мои матчи

```bash
curl http://localhost:8080/api/matches/my \
  -H "Authorization: Bearer <TOKEN>"
```

## 🔍 Особенности реализации

### Логика матчей

При постановке лайка в одной транзакции (`@Transactional`) выполняются проверки и действия:

1. Определяется текущий пользователь по email из JWT.
2. Проверяется, что пользователь не лайкает сам себя и лайк ещё не существует.
3. Сохраняется лайк.
4. Если существует встречный лайк, создаётся запись в `matches`.

Уникальные ограничения на уровне БД (`from_user_id + to_user_id`) защищают от дублей даже при гонках запросов.

### Динамический поиск (Criteria API)

Поиск в `ProfileSearchDao` строится на **JPA Criteria API**: условия фильтрации добавляются только для переданных параметров, а сортировка берётся из `Pageable`. Отдельным запросом считается общее количество записей, результат возвращается в виде `PagedModel`.

### Кэширование в Redis

Кэшируются анкеты по ID:

- `@Cacheable("profile")` на `findProfileById`
- `@CacheEvict("profile")` на `updateProfile`, чтобы после обновления не отдавались устаревшие данные

### Безопасность

- Stateless-аутентификация: токен передаётся в заголовке `Authorization: Bearer <token>`
- Пароли хэшируются через BCrypt (strength 12)
- Собственный `JwtFilter` встроен в цепочку Spring Security перед `UsernamePasswordAuthenticationFilter`
- Настроен CORS для локального фронтенда (`http://localhost:5173`)
- Владелец профиля проверяется в сервисе: чужую анкету изменить нельзя (`403`)

### Валидация

Входящие данные проверяются через Jakarta Validation (`@Valid`): формат email, длина пароля (от 8 символов), ограничения на имя, возраст, город и описание анкеты.

## 🧪 Тесты

Юнит-тесты сервисного слоя (JUnit 5):

- `UserServiceTest`
- `ProfileServiceTest`
- `LikeServiceTest`
- `MatchServiceTest`

Запуск:

```bash
./mvnw test
```

## 🗺 Планы развития

- [ ] Пагинация для `/api/likes/my` и `/api/matches/my`
- [ ] Чат между пользователями с матчем
- [ ] Загрузка фотографий профиля
- [ ] Интеграционные тесты с Testcontainers
- [ ] Dockerfile и запуск всего приложения через Docker Compose
- [ ] Роли и админ-панель (блокировка пользователей)

## 👤 Автор

**Ряшенцев Денис** — Java-разработчик
- Telegram / Email: @LGDOK / denisrrezencev227@gmail.com
