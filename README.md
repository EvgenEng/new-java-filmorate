# Схема базы данных java-filmorate

##ER-диаграмма

![ER-диаграмма.](src/main/resources/ER-diagram.png)

### Описание к схеме:

Данная диаграмма представляет структуру базы данных для приложения, связанного с фильмами и пользователями.

## Таблицы

### film
Содержит данные о фильмах: название, описание, дата выпуска, продолжительность, жанры, рейтинг MPA.

### film_genre
Реализует связь многие-ко-многим между фильмами и жанрами.

### genre
Список жанров (например, "Комедия", "Драма").

### mpa_rating
Возрастные рейтинги (например, "PG-13", "R").

### user
Данные пользователей: email, логин, имя, дата рождения.

### friends
Хранит связи дружбы между пользователями и их статусы (принято/в ожидании).

### friendship_status
Справочник статусов дружбы.

### user_like
Лайки пользователей к фильмам (связь многие-ко-многим).

## Связи

- Фильмы и жанры связаны через `film_genre`.
- Фильмы имеют один рейтинг MPA (`mpa_rating`).
- Пользователи ставят лайки фильмам через `user_like`.
- Дружба между пользователями управляется через `friends` и `friendship_status`.

## Примеры SQL-запросов

### Работа с фильмами
- Добавление нового фильма

INSERT INTO films (name, description, release_date, duration, mpa_rating_id)
VALUES ('Интерстеллар', 'Фантастика про космос', '2014-11-06', 169, 3);

- Получение топ-10 фильмов по лайкам

SELECT f.name, COUNT(l.user_id) AS likes
FROM films f
LEFT JOIN film_likes l ON f.film_id = l.film_id
GROUP BY f.film_id
ORDER BY likes DESC
LIMIT 10;

### Работа с пользователями
- Регистрация нового пользователя

INSERT INTO users (email, login, name, birthday)
VALUES ('user@mail.ru', 'user123', 'Иван Иванов', '1990-05-15');

- Поиск всех друзей пользователя

SELECT u.name
FROM friends f
JOIN users u ON f.friend_id = u.user_id
WHERE f.user_id = 101 AND f.status_id = 2;

### Взаимодействия

- Добавление лайка фильму

INSERT INTO film_likes (film_id, user_id)
VALUES (5, 101);

- Добавление в друзья

INSERT INTO friends (user_id, friend_id, status_id)
VALUES (101, 102, 1); -- status_id 1 = запрос отправлен

- Подтверждение дружбы

UPDATE friends
SET status_id = 2 -- status_id 2 = друзья
WHERE user_id = 102 AND friend_id = 101;

### Поисковые запросы

- Фильмы определённого жанра

SELECT f.name
FROM films f
JOIN film_genres fg ON f.film_id = fg.film_id
WHERE fg.genre_id = 2; -- 2 = Драма

- Общие друзья двух пользователей

SELECT u.name
FROM friends f1
JOIN friends f2 ON f1.friend_id = f2.friend_id
JOIN users u ON f1.friend_id = u.user_id
WHERE f1.user_id = 101 AND f2.user_id = 102;
