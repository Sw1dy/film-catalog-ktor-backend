# Бэкенд для "Каталог фильмов"

Стек:
- Kotlin
- Ktor Server
- PostgreSQL
- Exposed
- HikariCP
- kotlinx.serialization
- BCrypt
- JWT

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


## Авторизация

Авторизация реализована на сервере.

Пользователи хранятся в PostgreSQL в таблице `users`, пароли хранятся как BCrypt hash. Сервер выдаёт JWT, который Android-клиент передаёт в заголовке `Authorization: Bearer <token>`.

Тестовый администратор:

- email: `admin@test.ru`
- password: `admin123`

### POST /auth/register

Регистрирует пользователя с ролью `USER`.

```json
{
  "firstName": "Никита",
  "lastName": "Породин",
  "email": "nikita@test.ru",
  "password": "123456"
}
```

Успешный ответ: `201 Created`.

### POST /auth/login

Выполняет вход по email и паролю.

```json
{
  "email": "admin@test.ru",
  "password": "admin123"
}
```

Успешный ответ: `200 OK`.

```json
{
  "token": "jwt-token",
  "user": {
    "id": 1,
    "firstName": "Админ",
    "lastName": "Каталога",
    "email": "admin@test.ru",
    "role": "ADMIN"
  }
}
```

### GET /auth/me

Возвращает текущего пользователя по JWT.

```http
Authorization: Bearer <token>
```

## Фильмы

Публичные endpoints без авторизации:

- `GET /movies`
- `GET /movies?genre=Комедия`
- `GET /movies?year=2017`
- `GET /movies?genre=Комедия&year=2017`
- `GET /movies/genres`
- `GET /movies/years`
- `GET /movies/{id}`
- `GET /movies/search?query=`

Админские endpoints требуют роль `ADMIN` и header:

```http
Authorization: Bearer <token>
```

- `POST /movies`
- `PUT /movies/{id}`
- `DELETE /movies/{id}`

Если токен не передан или невалиден, сервер вернёт `401 Unauthorized`.

Если пользователь авторизован, но не является администратором, сервер вернёт `403 Forbidden`.

### GET /movies

Без query-параметров возвращает весь каталог.

### GET /movies?genre=Комедия

Возвращает фильмы выбранного жанра. Фильтрация по жанру выполняется без учёта регистра.

### GET /movies?year=2017

Возвращает фильмы выбранного года выпуска.

Если `year` передан не числом, сервер вернёт:

```json
{
  "message": "Некорректный год"
}
```

### GET /movies?genre=Комедия&year=2017

Возвращает фильмы, которые одновременно подходят по жанру и году.

### GET /movies/genres

Возвращает уникальные жанры из таблицы фильмов, отсортированные по алфавиту.

```json
[
  "Аниме",
  "Драма",
  "Комедия",
  "Криминал"
]
```

### GET /movies/years

Возвращает уникальные годы выпуска, отсортированные по убыванию.

```json
[
  2026,
  2019,
  2018,
  2017,
  2009
]
```

`/movies/genres` и `/movies/years` нужны клиентскому приложению для отображения доступных фильтров на главной странице. Android-клиент может загрузить эти списки, показать пользователю варианты в UI, а затем вызвать `GET /movies` с выбранными query-параметрами.

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

## Тесты

```bash
./gradlew test
```

На Windows:

```bash
gradlew.bat test
```
