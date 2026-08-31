package ru.yandex.practicum.filmorate.storage.dal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dal.dto.*;
import ru.yandex.practicum.filmorate.storage.dal.mappers.PopularFilmRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.RowMapperAgeRating;
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
    private static final String FIND_ALL_QUERY = """
    SELECT f.* 
    FROM \"Films\" f
    JOIN \"FilmGenre\" fg
    ON f.film_id = fg.film_id;
    """;
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
    private static final String FIND_ALL_MPA = "SELECT * FROM \"AgeRating\";";
    private static final String FIND_MPA_BY_ID_QUERY = "SELECT * FROM \"AgeRating\" WHERE id = ?;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM \"Films\" WHERE film_id = ?";

    private static final String INSERT_NEW_GENRE_TO_FILM = "INSERT INTO \"FilmGenre\"(film_id, genre_id) " +
            "VALUES (?, ?);";
    private static final String INSERT_NEW_FILM = "INSERT INTO \"Films\"(name, description, release_date, duration, age_rating_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_NEW_LIKE = "INSERT INTO \"FavoriteFilms\"(user_id, film_id)" +
            "VALUES (?, ?)";

    private static final String UPDATE_FILM =
            """
                    UPDATE \"Films\" 
                    SET name = ?, description = ?, release_date = ?, 
                    duration = ?, age_rating_id = ? 
                    WHERE film_id = ?
                    """;

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

    public List<AgeRatingDto> findAllMpa() {
        return jdbc.query(FIND_ALL_MPA, new RowMapperAgeRating());
    }

    public AgeRatingDto findMpaById(Long id) {
        try {
            return jdbc.queryForObject(FIND_MPA_BY_ID_QUERY, new RowMapperAgeRating(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundDataException("Указанный возрастной рейтинг не найден");
        }
    }

    public GenreDto findGenreById(Long id) {
        return jdbc.queryForObject(FIND_GENRE_BY_ID_QUERY, new RowMapperGenreDto(), id);
    }

    public void addNewGenreToFilmForFirstAdd(NewFilmRequest film, FilmDto filmDto) {
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    INSERT_NEW_GENRE_TO_FILM
            );
            ps.setString(1, filmDto.getId().toString());
            ps.setString(2, String.valueOf(film.getGenre().getId()));
            return ps;
        });
    }

    public void addNewGenreToFilm(UpdateFilmRequestDto film) {
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    INSERT_NEW_GENRE_TO_FILM
            );
            ps.setString(1, film.getId().toString());
            ps.setString(2, String.valueOf(film.getGenre()));
            return ps;
        });
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
        addNewGenreToFilmForFirstAdd(film, rowMapperFilm.mapToFilmDto(filmFromBd));
        return rowMapperFilm.mapToFilmDto(filmFromBd);
    }

    public FilmDto updateFilm(UpdateFilmRequestDto film) {
        Film filmFromBd = jdbc.queryForObject(FIND_BY_ID_QUERY, rowMapperFilm, film.getId());
        filmFromBd = rowMapperFilm.updateFilmFields(filmFromBd, film);
        jdbc.update(UPDATE_FILM, filmFromBd.getName(), filmFromBd.getDescription(), filmFromBd.getReleaseDate(),
                filmFromBd.getDuration(), filmFromBd.getAgeRating().getId(), filmFromBd.getId());
        addNewGenreToFilm(film);
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

    public void deleteLikeFromFilm(Long userId, Long friendId) {
        jdbc.update(DELETE_LIKE_FROM_FILM, userId, friendId);
    }
}
