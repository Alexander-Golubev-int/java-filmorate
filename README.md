# Схема базы данных для приложения Filmorate

> База данных приложения Filmorate предназначена для хранения информации о пользователях, фильмах, жанрах, возрастных рейтингах, лайках, избранных фильмах и дружеских связях между пользователями.

**Интерактивная схема базы данных:**
<img width="1000" height="739" alt="Untitled (1)" src="https://github.com/user-attachments/assets/8a3d12cd-0d12-470d-a88d-1d81bf6ae52d" />


[Открыть схему в dbdiagram.io](https://dbdiagram.io/d/6a791360829f06bdc8b47ca8)

## Структура базы данных

База данных хранит информацию о пользователях, фильмах, жанрах и возрастных рейтингах, а также связи между пользователями и фильмами.

Для связи фильмов с жанрами используется промежуточная таблица `FilmGenre`, так как один фильм может иметь несколько жанров.

Аналогично таблица `FavoriteFilms` хранит связь пользователей с понравившимися фильмами.

Информация о дружбе хранится в таблице `Friendship`. В ней указываются пользователь, отправивший заявку, пользователь-получатель и текущий статус дружбы (`PENDING` или `ACCEPTED`).

## Основные SQL-запросы

### Получение всех фильмов

```sql
SELECT *
FROM Films;
```

### Получение информации о фильме с возрастным рейтингом

```sql
SELECT f.*, ar.age_rating
FROM Films f
JOIN AgeRating ar
    ON f.age_rating_id = ar.id
WHERE f.film_id = ?;
```

> `?` — идентификатор фильма, информацию о котором необходимо получить.

### Получение жанров фильма

```sql
SELECT g.*
FROM Genre g
JOIN FilmGenre fg
    ON fg.genre_id = g.id
WHERE fg.film_id = ?;
```

> `?` — идентификатор фильма.

### Получение 10 самых популярных фильмов

```sql
SELECT f.film_id, f.name, COUNT(ff.user_id) AS likes
FROM Films f
LEFT JOIN FavoriteFilms ff ON f.film_id = ff.film_id
GROUP BY f.film_id, f.name
ORDER BY likes DESC
LIMIT 5;
```

### Получение информации о пользователе

```sql
SELECT *
FROM User
WHERE user_id = ?;
```

> `?` — идентификатор пользователя.

### Получение информации о пользователе

```sql
SELECT u.user_id, u.name
FROM User u
JOIN Friendship f1
    ON (f1.from_user_id = 1 AND f1.to_user_id = u.user_id)
    OR (f1.to_user_id = 1 AND f1.from_user_id = u.user_id)
JOIN FriendshipStatus fs1
    ON f1.friendship_status_id = fs1.id
JOIN Friendship f2
    ON (f2.from_user_id = 2 AND f2.to_user_id = u.user_id)
    OR (f2.to_user_id = 2 AND f2.from_user_id = u.user_id)
JOIN FriendshipStatus fs2
    ON f2.friendship_status_id = fs2.id
WHERE fs1.status = 'ACCEPTED'
  AND fs2.status = 'ACCEPTED';
```

### Получение списка любимых фильмов пользователя

```sql
SELECT f.*
FROM Films f
JOIN FavoriteFilms ff
    ON ff.film_id = f.film_id
WHERE ff.user_id = ?;
```

> `?` — идентификатор пользователя.
