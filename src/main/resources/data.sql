-- Заполнение таблицы mpa_ratings
INSERT INTO mpa_ratings (mpa_id, name, code, description) VALUES
(1, 'G', 'G', 'У фильма нет возрастных ограничений'),
(2, 'PG', 'PG', 'Детям рекомендуется смотреть фильм с родителями'),
(3, 'PG-13', 'PG-13', 'Детям до 13 лет просмотр не желателен'),
(4, 'R', 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
(5, 'NC-17', 'NC-17', 'Лицам до 18 лет просмотр запрещён');

-- Заполнение таблицы genres
INSERT INTO genres (genre_id, name, russian_name) VALUES
(1, 'COMEDY', 'Комедия'),
(2, 'DRAMA', 'Драма'),
(3, 'ANIMATION', 'Мультфильм'),
(4, 'THRILLER', 'Триллер'),
(5, 'DOCUMENTARY', 'Документальный'),
(6, 'ACTION', 'Боевик');

-- Заполнение таблицы friendship_statuses
INSERT INTO friendship_statuses (status_id, name)
VALUES (1, 'неподтверждённая'),
       (2, 'подтверждённая');