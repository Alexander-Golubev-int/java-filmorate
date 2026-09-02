INSERT INTO "Users" (email, login, name, birthday)
VALUES ('brat2000@mail.ru',
        'zeus',
        'Alexander',
        '1998-12-02'),
       ('jorjik@mail.ru',
        'Jorjik',
        'Jorjio',
        '2014-01-20'),
       ('ananas@mail.ru',
        'Perec',
        'Dron',
        '2000-01-22'),
       ('bublik@mail.ru',
        'Pechka',
        'Drova',
        '1966-01-20'),
       ('voronvova@mail.ru',
        'Voron',
        'Vova',
        '1991-01-20'),
       ('keksFM@mail.ru',
        'Keksik',
        'Vova',
        '1970-01-20');

INSERT INTO "FriendshipStatus" (status)
VALUES ('PENDING'),
    ('ACCEPTED');


INSERT INTO "Friendship" (from_user_id, to_user_id, friendship_status_id)
VALUES (3, 4, 2),
       (4, 3, 2),
       (2, 4, 2),
       (4, 2, 2),
       (1, 2, 1),
       (3, 6, 2),
       (6, 3, 2),
       (2, 6, 2),
       (6, 2, 2);

INSERT INTO "IncomingRequestToFriends" (user_id, from_user_id)
VALUES (2, 1);

INSERT INTO "Genre" (genre)
VALUES ('Комедия'),
    ('Драма'),
    ('Мультфильм'),
    ('Триллер'),
    ('Документальный'),
    ('Боевик');

INSERT INTO "AgeRating" (id, age_rating)
VALUES (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17');

INSERT INTO "Films" (name, description, release_date, duration, age_rating_id)
VALUES ('Мальчишник в Вегасе', 'Комедия о мальчишнике, который пошёл не по плану', '2009-06-05', 100, 4),
    ('Побег из Шоушенка', 'История надежды и дружбы в тюрьме строгого режима', '1994-09-23', 142, 4),
    ('Король Лев', 'Анимационный фильм о львёнке Симбе и его пути к трону', '1994-06-15', 88, 1),
    ('Молчание ягнят', 'Психологический триллер о поиске серийного убийцы', '1991-02-14', 118, 4),
    ('Земля: Один потрясающий день', 'Документальный фильм о жизни животных на планете', '2017-10-06', 95, 1),
    ('Форсаж 9', 'Экшн о гонках, семье и невероятных трюках', '2021-05-19', 143, 3);

INSERT INTO "FilmGenre" (film_id, genre_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (3, 2),
       (4, 4),
       (4, 2),
       (5, 5),
       (6, 6),
       (6, 4);