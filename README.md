# Бэкэнд для "Каталог фильмов"
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

При первом запуске сервер создаёт таблицы и добавляет начальные фильмы:

- Грязные деньги
- Детство Шелдона
- Джентльмены
- Хвост Феи

## Endpoints

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

Добавляет фильм.

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

Обновляет фильм.

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

Удаляет фильм.

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

## Авторизация

Сейчас авторизация сделана заглушкой:

```kotlin
fun isAdmin(call: ApplicationCall): Boolean = true
```

`POST`, `PUT` и `DELETE` уже проверяют эту функцию. Позже её можно заменить на проверку Firebase token.
