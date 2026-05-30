# Бэкенд для "Каталог фильмов"

Kotlin Ktor backend для мобильного приложения "Каталог фильмов".

Стек:

- Kotlin
- Ktor Server
- PostgreSQL
- Exposed
- HikariCP
- kotlinx.serialization
- BCrypt

## Запуск PostgreSQL

```bash
docker compose up -d
```

База данных будет доступна на `localhost:5432`.

Параметры подключения:

- database: `film_catalog`
- user: `postgres`
- password: `postgres`

## Запуск сервера

```bash
./gradlew run
```

На Windows:

```bash
gradlew.bat run
```

Сервер запускается на `http://localhost:8080`.

При первом запуске сервер создаёт таблицы, добавляет начальные фильмы и тестового администратора.

Начальные фильмы:

- Грязные деньги
- Детство Шелдона
- Джентльмены
- Хвост Феи

## Авторизация

Авторизация реализована на сервере без Firebase.

Пользователи хранятся в PostgreSQL в таблице `users`, пароли хранятся как BCrypt hash. Сервер выдаёт JWT, который Android-клиент передаёт в заголовке `Authorization: Bearer <token>`.

Тестовый администратор:

- email: `admin@test.ru`
- password: `admin123`

### POST /auth/register

Регистрирует пользователя с ролью `USER`.

Пример запроса:

```json
{
  "firstName": "Никита",
  "lastName": "Породин",
  "email": "nikita@test.ru",
  "password": "123456"
}
```

Успешный ответ: `201 Created`.

```json
{
  "token": "generated-token",
  "user": {
    "id": 1,
    "firstName": "Никита",
    "lastName": "Породин",
    "email": "nikita@test.ru",
    "role": "USER"
  }
}
```

### POST /auth/login

Выполняет вход по email и паролю.

Пример запроса:

```json
{
  "email": "nikita@test.ru",
  "password": "123456"
}
```

Успешный ответ: `200 OK`.

```json
{
  "token": "generated-token",
  "user": {
    "id": 1,
    "firstName": "Никита",
    "lastName": "Породин",
    "email": "nikita@test.ru",
    "role": "USER"
  }
}
```

### GET /auth/me

Возвращает текущего пользователя по токену.

Header:

```http
Authorization: Bearer <token>
```

Успешный ответ: `200 OK`.

```json
{
  "id": 1,
  "firstName": "Никита",
  "lastName": "Породин",
  "email": "nikita@test.ru",
  "role": "USER"
}
```

## Фильмы

Публичные endpoints без авторизации:

- `GET /movies`
- `GET /movies/{id}`
- `GET /movies/search?query=`

Админские endpoints требуют роль `ADMIN` и header:

```http
Authorization: Bearer <token>
```

- `POST /movies`
- `PUT /movies/{id}`
- `DELETE /movies/{id}`

Если токен не передан или не найден, сервер вернёт `401 Unauthorized`.

Если пользователь авторизован, но не является администратором, сервер вернёт `403 Forbidden`.

### GET /movies

Возвращает список всех фильмов.

### GET /movies/{id}

Возвращает фильм по id.

Если фильм не найден:

```json
{
  "message": "Фильм не найден"
}
```

### GET /movies/search?query=

Ищет фильмы по названию без учёта регистра.

Если `query` пустой:

```json
{
  "message": "Параметр query не должен быть пустым"
}
```

### POST /movies

Добавляет фильм. Требуется токен администратора.

Пример запроса:

```json
{
  "title": "Интерстеллар",
  "description": "Фантастический фильм о космосе, времени и семье.",
  "year": 2014,
  "genre": "Фантастика",
  "rating": 8.7,
  "imageUrl": "https://example.com/images/interstellar.jpg"
}
```

Успешный ответ: `201 Created`.

### PUT /movies/{id}

Обновляет фильм. Требуется токен администратора.

Пример запроса:

```json
{
  "title": "Интерстеллар",
  "description": "Обновлённое описание фильма.",
  "year": 2014,
  "genre": "Научная фантастика",
  "rating": 8.8,
  "imageUrl": "https://example.com/images/interstellar-updated.jpg"
}
```

Успешный ответ: `200 OK`.

### DELETE /movies/{id}

Удаляет фильм. Требуется токен администратора.

Перед удалением сервер удаляет связанные записи из `watchlist`.

Успешный ответ: `204 No Content`.

## Валидация фильмов

- `title` не пустой
- `description` не пустой
- `genre` не пустой
- `year > 1888`
- `rating` от `0.0` до `10.0`

Все ошибки возвращаются в формате:

```json
{
  "message": "Описание ошибки"
}
```
