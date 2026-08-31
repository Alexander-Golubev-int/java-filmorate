package ru.yandex.practicum.filmorate.storage.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dal.dto.*;
import ru.yandex.practicum.filmorate.storage.dal.mappers.PopularFilmRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.RowMapperFilm;
import ru.yandex.practicum.filmorate.storage.dal.mappers.RowMapperGenreDto;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;


@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmRepository {
    private final JdbcTemplate jdbc;
    private final RowMapperFilm rowMapperFilm;
    private static final String FIND_ALL_QUERY = "SELECT * FROM \"Films\"";
    private static final String FIND_LIKE =
            "SELECT COUNT(*) FROM \"FavoriteFilms\" " +
                    "WHERE user_id = ? AND film_id = ?";
    private static final String FIND_POPULAR_FILMS =
            """
                            SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.age_rating_id,\s
                                   COUNT(ff.user_id) AS likes
                            FROM "Films" f
                            LEFT JOIN "FavoriteFilms" ff ON f.film_id = ff.film_id
                            GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.age_rating_id
                            ORDER BY likes DESC
                            LIMIT ?;
                    """;
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM \"Genre\" WHERE id = ?;";
    private static final String FIND_ALL_GENRE = "SELECT * FROM \"Genre\";";
   private static final String FIND_BY_ID_QUERY = "SELECT * FROM \"Films\" WHERE film_id = ?";
    //    private static final String FIND_BY_ID_INCOMING_REQUEST = "SELECT COUNT(*) FROM \"IncomingRequestToFriends\" WHERE user_id = ? AND from_user_id = ?";
//    private static final String FIND_COMMON_FRIENDS = """
//    SELECT DISTINCT (u.*)
//    FROM "Users" u
//    JOIN "Friendship" f1 ON (
//            (f1.from_user_id = ? AND f1.to_user_id = u.user_id)
//    OR
//            (f1.to_user_id = ? AND f1.from_user_id = u.user_id)
//        )
//    JOIN "Friendship" f2 ON (
//            (f2.from_user_id = ? AND f2.to_user_id = u.user_id)
//    OR
//            (f2.to_user_id = ? AND f2.from_user_id = u.user_id)
//        )
//    WHERE f1.friendship_status_id = 2
//    AND f2.friendship_status_id = 2
//    AND u.user_id NOT IN (?, ?)
//    """;
//    private static final String INSERT_QUERY_PENDING = "INSERT INTO \"Friendship\"(from_user_id, to_user_id, friendship_status_id)" +
//            "VALUES (?, ?, 1)";
//    private static final String INSERT_QUERY_INCOMING_REQUEST_TO_FRIENDS = "INSERT INTO \"IncomingRequestToFriends\"(user_id, from_user_id)" +
//            "VALUES (?, ?)";
//    private static final String INSERT_QUERY_ACCEPTED = "INSERT INTO \"Friendship\"(from_user_id, to_user_id, " +
//            "friendship_status_id)" +
//            "VALUES (?, ?, 2)";
    private static final String INSERT_NEW_FILM = "INSERT INTO \"Films\"(name, description, release_date, duration, age_rating_id)" +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_NEW_LIKE = "INSERT INTO \"FavoriteFilms\"(user_id, film_id)" +
            "VALUES (?, ?)";
    //
//    private static final String UPDATE_QUERY_FRIENDSHIP = "UPDATE \"Friendship\" SET friendship_status_id = 2 WHERE " +
//            "from_user_id = ? AND to_user_id = ?;";
    private static final String UPDATE_FILM =
            """
                    UPDATE \"Films\" 
                    SET name = ?, description = ?, release_date = ?, 
                    duration = ?, age_rating_id = ? 
                    WHERE film_id = ?
                    """;

    //
//    private static final String DELETE_QUERY_INCOMING_REQUEST_TO_FRIENDS = "DELETE FROM \"IncomingRequestToFriends\" " +
//            "WHERE user_id = ? AND from_user_id = ?;";
    private static final String DELETE_LIKE_FROM_FILM = "DELETE FROM \"FavoriteFilms\" " +
            "WHERE user_id = ? AND film_id = ?;";

    public List<FilmDto> findAll() {
        return jdbc.query(FIND_ALL_QUERY, rowMapperFilm).stream()
                .map(rowMapperFilm::mapToFilmDto)
                .toList();
    }


    public List<FilmDto> findMostPopularFilms(Long count) {
        return jdbc.query(FIND_POPULAR_FILMS, new PopularFilmRowMapper(), count);
    }

    public List<GenreDto> findAllGenre() {
        return jdbc.query(FIND_ALL_GENRE, new RowMapperGenreDto());
    }

    public GenreDto findGenreById(Long id) {
        return jdbc.queryForObject(FIND_GENRE_BY_ID_QUERY, new RowMapperGenreDto(), id);
    }

    public Optional<FilmDto> findById(long filmId) {
        List<FilmDto> list = jdbc.query(FIND_BY_ID_QUERY, rowMapperFilm, filmId).stream()
                .map(rowMapperFilm::mapToFilmDto)
                .toList();
        return list.stream().findFirst();
    }

    public FilmDto addNewFilm(NewFilmRequest film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    INSERT_NEW_FILM,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setString(3, film.getReleaseDate().toString());
            ps.setObject(4, film.getDuration());
            ps.setObject(5, film.getAgeRating().getId());
            return ps;
        }, keyHolder);

        Long filmId = keyHolder.getKeyAs(Long.class);
        Film filmFromBd = jdbc.queryForObject(FIND_BY_ID_QUERY, rowMapperFilm, filmId);
        return rowMapperFilm.mapToFilmDto(filmFromBd);
    }

    public FilmDto updateFilm(UpdateFilmRequestDto film) {
        Film filmFromBd = jdbc.queryForObject(FIND_BY_ID_QUERY, rowMapperFilm, film.getId());
        filmFromBd = rowMapperFilm.updateFilmFields(filmFromBd, film);
        jdbc.update(UPDATE_FILM, filmFromBd.getName(), filmFromBd.getDescription(), filmFromBd.getReleaseDate(),
                filmFromBd.getDuration(), filmFromBd.getAgeRating().getId(), filmFromBd.getId());
        filmFromBd = jdbc.queryForObject(FIND_BY_ID_QUERY, rowMapperFilm, filmFromBd.getId());
        return rowMapperFilm.mapToFilmDto(filmFromBd);
    }

    public void addNewLike(Long userId, Long filmId) {
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_NEW_LIKE);
            ps.setString(1, userId.toString());
            ps.setString(2, filmId.toString());
            return ps;
        });
    }

    public boolean hasLike(Long userId, Long filmId) {
        List<Integer> results = jdbc.queryForList(FIND_LIKE, Integer.class, userId, filmId);
        return !results.isEmpty() && results.get(0) > 0;
    }

    //
//    public boolean isAnIncomingRequest(Long id, Long fromUserId) {
//        return jdbc.queryForObject(FIND_BY_ID_INCOMING_REQUEST, Integer.class, id, fromUserId) > 0;
//    }
//
//    public boolean isFriendRequestPending(Long id, Long friendId) {
//        return jdbc.queryForObject(FIND_SUBMITTED_APPLICATION, Integer.class, id, friendId) > 0;
//    }
//
//    public void confirmFriendship(Long friendId, Long userId) {
//        jdbc.update(UPDATE_QUERY_FRIENDSHIP, friendId, userId);
//    }
//
//    public void addNewFriendAfterConfirmFriendship(Long userId, Long friendId) {
//        jdbc.update(INSERT_QUERY_ACCEPTED, userId, friendId);
//    }
//
    public void deleteLikeFromFilm(Long userId, Long friendId) {
        jdbc.update(DELETE_LIKE_FROM_FILM, userId, friendId);
    }
//
//    public void deleteFriendships(Long userId, Long friendId) {
//        jdbc.update(DELETE_QUERY_FRIENDSHIPS, userId, friendId);
//    }
//
//    public void addNewFriend(Long user_id, Long friendId) {
//        try {
//            if (insertWithoutKeys(INSERT_QUERY_PENDING, user_id, friendId)) {
//                log.info("Пользователь {} отправил заявку на добавление в друзья пользователю {}", user_id, friendId);
//            }
//
//        } catch (DuplicateKeyException e) {
//            log.warn("Не удалось сохранить запрос на отправку дружбы от: {} к {}", user_id, friendId);
//            throw new ValidationException("Не удалось сохранить запрос на отправку дружбы от: " +
//                    "from_user_id=" + user_id + ", to_user_id=" + friendId);
//        }
//    }
//
//    public void addIncomingRequestToFriends(Long friendId, Long user_id) {
//        try {
//            if (insertWithoutKeys(INSERT_QUERY_INCOMING_REQUEST_TO_FRIENDS, friendId, user_id)) {
//                log.info("Создана входящая заявка на добавление друга пользователю {} от {}", friendId,
//                        user_id);
//            }
//        } catch (DuplicateKeyException e) {
//            log.warn("Не удалось сохранить запрос на создание заявки в друзья между пользователем {} и {}",
//                    friendId, user_id);
//            throw new ValidationException(String.format("Не удалось сохранить запрос на создание заявки в друзья между пользователем %s и %s",
//                    friendId, user_id));
//        }
//    }
//
//    public boolean insertWithoutKeys(String sql, Long userId, Long friendId) {
//        try {
//            int rowsAffected = jdbc.update(connection -> {
//                PreparedStatement ps = connection.prepareStatement(sql);
//                ps.setLong(1, userId);
//                ps.setLong(2, friendId);
//                return ps;
//            });
//            return rowsAffected > 0;
//        } catch (DuplicateKeyException e) {
//            throw new ValidationException("Ошибка в методе insertWithoutKeys при вставке данных");
//        }
//    }
//
//    public List<UserDto> getCommonFriends(Long userId1, Long userId2) {
//        return jdbc.query(FIND_COMMON_FRIENDS, new RowMapperUser(), userId1, userId1,
//                        userId2, userId2,
//                        userId1, userId2).stream()
//                .map(rowMapperUser::mapToUserDto)
//                .toList();
//    }
}
