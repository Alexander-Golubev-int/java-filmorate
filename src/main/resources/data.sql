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